package com.van.data.syncer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "sync")
@Configuration
public class ScheduledTaskConfiguration {

    private List<ScheduledTask> tasks;
    private int batchSize;

    public List<ScheduledTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ScheduledTask> tasks) {
        this.tasks = tasks;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @ConfigurationProperties(prefix = "sync.tasks")
    @Configuration
    public static class ScheduledTask{
        private String name;
        private String sql;
        private String des;
        private String cron;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }

}