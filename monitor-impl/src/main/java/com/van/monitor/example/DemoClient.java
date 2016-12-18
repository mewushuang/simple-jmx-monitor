package com.van.monitor.example;

import com.van.monitor.api.ControllerMXBean;
import com.van.monitor.api.SysInfoMonitorMXBean;
import com.van.monitor.client.SimpleJMXClient;

/**
 * Created by van on 2016/11/16.
 */
public class DemoClient {

    public static void main(String[] args) throws InterruptedException {
        SimpleJMXClient sc=new SimpleJMXClient("service:jmx:rmi:///jndi/rmi://127.0.0.1:9999/jmxrmi");
        ControllerMXBean cm=sc.getMBean(ControllerMXBean.class,"com.example:type=ThreadMonitor");
        //cm.start();
        Thread.sleep(3000);
        SysInfoMonitorMXBean mm=sc.getMBean(SysInfoMonitorMXBean.class,"com.example.SysInfo:type=ThreadMonitor");
        System.out.println(mm.getChangefulInfo());
        //cm.stop();
        //
    }
}
