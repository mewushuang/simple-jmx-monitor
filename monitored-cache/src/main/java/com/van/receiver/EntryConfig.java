package com.van.receiver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by van on 17-4-24.
 */
@ConfigurationProperties(locations = "application.yaml")
@Component
public class EntryConfig {
    private String zookeeperServerList;
    private String seatServer;
    private String rtServer;
    private int timeout;
    private String seatInitMsg;
    private String rtInitMsg;
    private boolean seatIfLog;
    private boolean rtIfLog;
    private String seatLogPrefix;
    private String rtLogPrefix;

    public String getZookeeperServerList() {
        return zookeeperServerList;
    }

    public void setZookeeperServerList(String zookeeperServerList) {
        this.zookeeperServerList = zookeeperServerList;
    }

    public String getSeatServer() {
        return seatServer;
    }

    public void setSeatServer(String seatServer) {
        this.seatServer = seatServer;
    }

    public String getRtServer() {
        return rtServer;
    }

    public void setRtServer(String rtServer) {
        this.rtServer = rtServer;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getSeatInitMsg() {
        return seatInitMsg;
    }

    public void setSeatInitMsg(String seatInitMsg) {
        this.seatInitMsg = seatInitMsg;
    }

    public String getRtInitMsg() {
        return rtInitMsg;
    }

    public void setRtInitMsg(String rtInitMsg) {
        this.rtInitMsg = rtInitMsg;
    }



    public String getSeatLogPrefix() {
        return seatLogPrefix;
    }

    public void setSeatLogPrefix(String seatLogPrefix) {
        this.seatLogPrefix = seatLogPrefix;
    }

    public String getRtLogPrefix() {
        return rtLogPrefix;
    }

    public void setRtLogPrefix(String rtLogPrefix) {
        this.rtLogPrefix = rtLogPrefix;
    }

    public boolean isSeatIfLog() {
        return seatIfLog;
    }

    public void setSeatIfLog(boolean seatIfLog) {
        this.seatIfLog = seatIfLog;
    }

    public boolean isRtIfLog() {
        return rtIfLog;
    }

    public void setRtIfLog(boolean rtIfLog) {
        this.rtIfLog = rtIfLog;
    }
}
