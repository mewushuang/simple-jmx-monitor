package com.van.deprecated.common;

import java.util.List;

/**
 * Created by van on 2016/10/31.
 */
public abstract class AbstractTable {
    private String name;
    private List<Column> columns;
    private Column partition;

    /**
     * 获取分表/分区的表名
     * @return
     */
    abstract public String getSubName();

    /**
     * 生成建表语句
     * @return
     */
    abstract public String genSQL();


}
