package com.van.monitor.api;

/**
 * Created by van on 2016/12/2.
 */
public abstract class Metric {
    public enum Level{
        warn,error,info;
    }
    protected String name;
    protected String value;
    protected String description;
    protected Level level;//监控中心根据此字段定义是否需要告警
    protected boolean needSave;//是否需要存历史信息供统计分析

    public Metric() {
    }

    public Metric(String name, String value,boolean needSave, String description) {
        this.name = name;
        this.value = value;
        this.needSave=needSave;
        this.description = description;
        if(needSave){
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("存历史库需要value是数值类型");
            }
        }
    }


    /**
     * 子类重写此方法，可以计算出每个测点实例的消息级别
     * 用于在监控中心实现告警逻辑
     * @return
     */
    public abstract Level getLevel() ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if(needSave){
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("存历史库需要value是数值类型");
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setLevel(Level level) {
        this.level = level;
    }


    public boolean isNeedSave() {
        return needSave;
    }

    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }
}
