package com.van.receiver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "kafka.topics")
@Component
public class TopicConfig {

    /**
     * Folder location for storing files
     */
    private String itemUpdate="datadublishderver.realtimedata";
    private String rtData="data.rt";
    private String seatData="data.seat";


    public String getItemUpdate() {
        return itemUpdate;
    }

    public void setItemUpdate(String itemUpdate) {
        this.itemUpdate = itemUpdate;
    }

    public String getRtData() {
        return rtData;
    }

    public void setRtData(String rtData) {
        this.rtData = rtData;
    }

    public String getSeatData() {
        return seatData;
    }

    public void setSeatData(String seatData) {
        this.seatData = seatData;
    }
}