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
import java.nio.file.Paths;
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
    private static final String DAEMON_CONTROLLER_BEAN_NAME="com.daemon.controller";

    private static Logger logger = LoggerFactory.getLogger(SimpleServer.class);

    private Properties config;
    private ExecutorService mainThread;
    private SimpleLogViewer logViewer;
    private Path[] logPaths;

    private SigarlSysInfoMonitor sysInfoMonitor;
    private ServerMB monitor;
    private MBeanServer platformMBeanServer;
    private JMXConnectorServer connectorServer;


    public SimpleServer() {
        config = new Properties();
        try {
            String path = System.getProperty("app.home");
            logger.info("path:" + path);
            if (path == null) {//ide内调试用
                config.load(new InputStreamReader(
                        SimpleServer.class.getClassLoader().getResourceAsStream("monitor.properties")));
            } else {
                File cf=new File(path,"conf"+ File.separator+"monitor.properties");
                config.load(new InputStreamReader(new FileInputStream(cf), Charset.forName("utf-8")));
            }
            //获取日志目录的配置
            String[] dirs=config.getProperty("log.dirs","logs").split(",");
            logPaths=new Path[dirs.length];
            Path p= Paths.get(path);
            for(int i=0;i<dirs.length;i++){
                logPaths[i]=p.resolve(dirs[i]);
            }
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
            stopDaemon();
        } else {
            //String url1="service:jmx:rmi:///jndi/rmi://127.0.0.1:"+ getPort()+ "/jmxrmi";
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
        monitor = new ServerMB(mainThread, className,connectorServer);

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
        logViewer = new SimpleLogViewer(logPaths);
        String lvn = config.getProperty("logViewerBean.name", LogViewerMXBean.BEAN_NAME);
        ObjectName n3 = new ObjectName(lvn);
        platformMBeanServer.registerMBean(logViewer, n3);
        logger.info("logView bean registered with name:" + lvn);


        logger.info("JMX server initialization done on port " + getPort());

    }

    private void initJMX() throws JMException {
        String port=getPort();
        JMXServiceURL url=null;
        String protocol="rmi";
        String host="127.0.0.1";
        Map<String,Object> env=new HashMap<>();
        try {
            LocateRegistry.createRegistry(Integer.parseInt(port));
            url=new JMXServiceURL("service:jmx:rmi:///jndi/rmi://127.0.0.1:"+ getPort()+ "/jmxrmi");
            platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

            connectorServer = JMXConnectorServerFactory
                    .newJMXConnectorServer(url, env, platformMBeanServer);
            connectorServer.start();
            logger.info("rmi connect server started");
        } catch (RemoteException e) {
            throw new JMException("cannot regist rmi server on port:" +port+"\n"+e.getMessage());
        } catch (MalformedURLException e) {
            throw new JMException(String.format("invalid params of JMXServiceURL:{protocol:%s,host:%s,port:%s\n%s}",protocol,host,port,e.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(String.format("invalid params of JMXServiceURL:{protocol:%s,host:%s,port:%s\n%s}",protocol,host,port,e.getMessage()),e);
        }
    }


    public void destroy() {
        logger.info("starting to release daemon resource");

        mainThread.shutdownNow();
        logger.info("thread pool shutdown");
        //关闭日志bean中缓存的日志流
        if (logViewer != null) logViewer.clearCache();
        if (sysInfoMonitor != null) sysInfoMonitor.close();
        logger.info("program exit");
    }



    private static void startDaemon() {
        final SimpleServer simpleServer = new SimpleServer();
        try {
            //初始化
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

    private static void stopDaemon() {
        SimpleServer server=new SimpleServer();
        SimpleJMXClient sc = new SimpleJMXClient(
                "service:jmx:rmi:///jndi/rmi://127.0.0.1:"
                        + server.getPort()
                        + "/jmxrmi");
        String cn = server.config.getProperty("controllerBean.name");
        if(cn==null){
            logger.error("please set value of key 'controllerBean.name' in file monitor.properties");
            return;
        }
        //TODO c/s端的beanname统一。监控平台化：服务间的日志隔离
        ControllerMXBean cm = sc.getMBean(ControllerMXBean.class, cn + ControllerMXBean.BEAN_TYPE);
        cm.shutDownDaemon();
    }

    private String getPort(){
        //System.getProperty("com.van.monitor.server.port")
        return config.getProperty("server.port","9999");
    }
}
