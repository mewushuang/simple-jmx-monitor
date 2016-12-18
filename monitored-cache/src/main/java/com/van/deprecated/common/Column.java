package com.van.deprecated.common;

/**
 * Created by van on 2016/10/31.
 */
public class Column {
    public Column() {
    }

    private String name;
    private String value;
    private String default_val;
    private ColumnType type;
    private boolean isPrimary;
    private boolean isIndex;
    private boolean isPartition;

    public Column(String name, ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefault_val() {
        return this.default_val;
    }

    public void setDefault_val(String default_val) {
        this.default_val = default_val;
    }
}
