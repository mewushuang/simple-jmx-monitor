package com.van.monitor.example;

import com.van.monitor.api.ControllerMXBean;
import com.van.monitor.api.LogViewerMXBean;
import com.van.monitor.api.SysInfoMonitorMXBean;
import com.van.monitor.client.SimpleJMXClient;
import com.van.monitor.systemInfo.SigarlSysInfoMonitor;
import com.van.monitor.systemInfo.SimpleLogViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by van on 2016/11/16.
 */
public class SimpleServer {

    private String CONTROLLER_MB_NAME = "com.example";
    private static final String DAEMON_CONTROLLER_BEAN_NAME = "com.daemon.controller";
    private static Logger logger = LoggerFactory.getLogger(SimpleServer.class);
    private String pid;

    private Properties config;
    private ExecutorService mainThread;
    private SimpleLogViewer logViewer;
    private Path[] logPaths;

    private SigarlSysInfoMonitor sysInfoMonitor;
    private ServerMB monitor;
    private MBeanServer platformMBeanServer;
    private JMXConnectorServer connectorServer;
    private String path;
    private String logDirs;


    public SimpleServer() {
        config = new Properties();
        try {
            path = System.getProperty("user.dir");
            logger.info("path:" + path);
            if (path == null) {//ide内调试用
                config.load(new InputStreamReader(
                        SimpleServer.class.getClassLoader().getResourceAsStream("monitor.properties")));
            } else {
                File cf = new File(path, "conf" + File.separator + "monitor.properties");
                config.load(new InputStreamReader(new FileInputStream(cf), Charset.forName("utf-8")));
            }
            logger.info("config of monitor:\n"+config.toString().replace(',','\n'));
            //获取日志目录的配置
            logDirs = config.getProperty("log.dirs", "logDirs");
        } catch (IOException e) {
            logger.error("error loading from file classpath:monitor.properties. check if exists or is damaged", e);
        }
    }

    /**
     * 运行时需添加虚拟机参数
     * <p>
     * -Dcom.sun.management.jmxremote
     * -Dcom.sun.management.jmxremote.port=9999
     * -Dcom.sun.management.jmxremote.authenticate=false
     * -Dcom.sun.management.jmxremote.ssl=false
     *
     * @param args
     */
    public static void main(String[] args) {
        String action = System.getProperty("com.van.monitor.server.action", "start");

        if ("close".equalsIgnoreCase(action)) {
            getControllerMXBean().shutDownDaemon();
        } else if ("restart".equalsIgnoreCase(action)) {
            getControllerMXBean().restartDaemon();
        } else {
            //String url1="service:jmx:rmi:///jndi/rmi://127.0.0.1:"+ getUrl()+ "/jmxrmi";
            startDaemon();

        }
    }


    public void init() throws JMException {

        initJMX();

        //线程池
        mainThread = Executors.newFixedThreadPool(5);
        logger.info("threadPool sized 5 inited");

        //实例化服务
        String className = config.getProperty("moniteredService.impl", "com.van.monitor.example.DefaultService");
        logger.info("get service className from config:" + className);
        monitor = new ServerMB(mainThread, className, connectorServer, pid);

        //注册服务的controller bean
        String cn = config.getProperty("controllerBean.name", CONTROLLER_MB_NAME);
        ObjectName name = new ObjectName(cn + ControllerMXBean.BEAN_TYPE);
        platformMBeanServer.registerMBean(monitor, name);
        logger.info("controller bean registered with name:" + cn);

        //注册资源监控bean
        sysInfoMonitor = new SigarlSysInfoMonitor();
        String sin = config.getProperty("sysInfoBean.name", SysInfoMonitorMXBean.BEAN_NAME);
        ObjectName n2 = new ObjectName(sin);
        platformMBeanServer.registerMBean(sysInfoMonitor, n2);
        logger.info("sysInfo bean registered with name:" + sin);

        //注册日志查看bean
        logViewer = new SimpleLogViewer(path,logDirs);
        String lvn = config.getProperty("logViewerBean.name", LogViewerMXBean.BEAN_NAME);
        ObjectName n3 = new ObjectName(lvn);
        platformMBeanServer.registerMBean(logViewer, n3);
        logger.info("logView bean registered with name:" + lvn);


        logger.info("JMX server initialization done on url: " + getUrl());

    }

    private void initJMX() throws JMException {
        //127.0.0.1:9999
        String[] des = getUrl().trim().split(":");
        JMXServiceURL url = null;
        System.setProperty("java.rmi.server.hostname", des[0]);
        Map<String, Object> env = new HashMap<>();
        try {
            LocateRegistry.createRegistry(Integer.parseInt(des[1]));
            url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + getUrl() + "/jmxrmi");
            platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            connectorServer = JMXConnectorServerFactory
                    .newJMXConnectorServer(url, env, platformMBeanServer);
            connectorServer.start();
            logger.info("rmi connect server started");
        } catch (RemoteException e) {
            throw new JMException("cannot register rmi server on url:" + getUrl() + "\n" + e.getMessage());
        } catch (MalformedURLException e) {
            throw new JMException(String.format("invalid params of JMXServiceURL:{protocol:%s,url:%s\n%s}", "rmi", getUrl(), e.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("invalid params of JMXServiceURL:{protocol:%s,url:%s\n%s}", "rmi", getUrl(), e.getMessage()), e);
        }
    }


    public void destroy() {
        logger.info("starting to release daemon resource");
        mainThread.shutdownNow();
        logger.info("thread pool shutting down");

        //关闭日志bean中缓存的日志流
        if (logViewer != null) logViewer.clearCache();
        if (sysInfoMonitor != null) sysInfoMonitor.close();
        logger.info("program exit");
    }


    private static void startDaemon() {
        final SimpleServer simpleServer = new SimpleServer();
        try {
            //初始化
            simpleServer.getPid();
            simpleServer.init();

            //启动服务线程
            logger.info("service starting");
            simpleServer.monitor.startDefault();

            //如果服务线程和该循环均停止，则程序因只剩守护进程而退出
            //监听monitor的shutdownDaemon字段。在调用shutdown方法后结束循环，回收资源结束程序
            while (!simpleServer.monitor.isShutDownDaemon()) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
            }
        } catch (JMException e) {
            logger.error("JMX error occurred when init JMXServer", e);
        } finally {
            simpleServer.destroy();
        }
    }

    private static ControllerMXBean getControllerMXBean() {
        SimpleServer server = new SimpleServer();
        SimpleJMXClient sc = new SimpleJMXClient(
                "service:jmx:rmi:///jndi/rmi://"
                        + server.getUrl()
                        + "/jmxrmi");
        String cn = server.config.getProperty("controllerBean.name");
        if (cn == null) {
            logger.error("please set value of key 'controllerBean.name' in file monitor.properties");
            throw new IllegalArgumentException("please set value of key 'controllerBean.name' in file monitor.properties");
        }
        //TODO c/s端的beanname统一。监控平台化：服务间的日志隔离
        ControllerMXBean cm = sc.getMBean(ControllerMXBean.class, cn + ControllerMXBean.BEAN_TYPE);
        return cm;
    }

    private String getUrl() {
        //System.getProperty("com.van.monitor.server.port")
        return config.getProperty("server.url", "127.0.0.1:9999");
    }

    private String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        System.out.println("Pid is:" + pid);
        this.pid=pid;
        return pid;
    }
}
