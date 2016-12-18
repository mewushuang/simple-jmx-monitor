package com.van.monitor.client;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by van on 2016/11/15.
 */
public class SimpleJMXClient {

    private String url;
    JMXServiceURL jmxURL;
    JMXConnector conn;
    MBeanServerConnection mbsc;

    /**
     *
     * @param url "service:jmx:rmi:///jndi/rmi://127.0.0.1:9999/jmxrmi"
     */
    public SimpleJMXClient(String url) {
        try {
            jmxURL = new JMXServiceURL(url);
            conn = JMXConnectorFactory.connect(jmxURL, null);
            mbsc = conn.getMBeanServerConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <T> T getMBean(Class<T> clazz, String objectName){

        System.out.println("Connect to JMX service.");

        ObjectName mbeanName = null;
        try {
            mbeanName = new ObjectName(objectName);
            // 构建代理
            return JMX.newMBeanProxy(mbsc, mbeanName, clazz, true);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
            //return null;
        }

    }

    @Override
    protected void finalize() throws Throwable {
        conn.close();
        super.finalize();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
