package com.van.data.syncer.deleteAndInsertSyncer;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by van on 2016/12/27.
 * 参考了org.springframework.jdbc.core.namedparam.MapSqlParameterSource
 */
public class ResultSetSqlParameterSource extends AbstractSqlParameterSource {
    private final Map<String, Object> values = new LinkedHashMap<String, Object>();



    public ResultSetSqlParameterSource() {
    }


    public ResultSetSqlParameterSource(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta=resultSet.getMetaData();
        for(int i=1;i<=meta.getColumnCount();i++){
            addValue(meta.getColumnName(i),resultSet.getObject(i),meta.getColumnType(i));
        }
    }




    public ResultSetSqlParameterSource addValue(String paramName, Object value) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.values.put(paramName, value);
        if (value instanceof SqlParameterValue) {
            registerSqlType(paramName, ((SqlParameterValue) value).getSqlType());
        }
        return this;
    }


    public ResultSetSqlParameterSource addValue(String paramName, Object value, int sqlType) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.values.put(paramName, value);
        registerSqlType(paramName, sqlType);
        return this;
    }

    public ResultSetSqlParameterSource addValue(String paramName, Object value, int sqlType, String typeName) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.values.put(paramName, value);
        registerSqlType(paramName, sqlType);
        registerTypeName(paramName, typeName);
        return this;
    }



    /**
     * Expose the current parameter values as read-only Map.
     */
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.values);
    }


    @Override
    public boolean hasValue(String paramName) {
        return this.values.containsKey(paramName);
    }

    @Override
    public Object getValue(String paramName) {
        if (!hasValue(paramName)) {
            throw new IllegalArgumentException("No value registered for key '" + paramName + "'");
        }
        return this.values.get(paramName);
    }
}
