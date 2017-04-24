package com.van.receiver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by van on 17-4-24.
 */
@ConfigurationProperties
@Component
public class EntryConfig {
    private String zookeeperServerList;
    private String seatServer;
    private String rtServer;
    private String timeout;
    private String seatInitMsg;
    private String rtInitMsg;
    private String seatIfLog;
    private String rtIfLog;
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

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
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

    public String getSeatIfLog() {
        return seatIfLog;
    }

    public void setSeatIfLog(String seatIfLog) {
        this.seatIfLog = seatIfLog;
    }

    public String getRtIfLog() {
        return rtIfLog;
    }

    public void setRtIfLog(String rtIfLog) {
        this.rtIfLog = rtIfLog;
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
}
