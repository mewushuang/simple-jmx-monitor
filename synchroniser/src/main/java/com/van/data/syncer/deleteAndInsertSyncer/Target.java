package com.van.data.syncer.deleteAndInsertSyncer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by van on 2016/12/27.
 */
public class Target {

    private final Logger logger = LoggerFactory.getLogger(Target.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final String tableName;


    public Target(JdbcTemplate jdbcTemplate, String destinationTable) {
        this.jdbcTemplate = jdbcTemplate;
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName(destinationTable);
        tableName = destinationTable;
    }

    public void deleteAll() {
        jdbcTemplate.execute("delete from " + tableName);
        if (logger.isDebugEnabled())
            logger.debug(tableName + " deleted");
    }

    public SqlParameterSource convert(ResultSet col) {
        try {
            return new ResultSetSqlParameterSource(col);
        } catch (SQLException e) {
            JdbcUtils.closeResultSet(col);
            throw jdbcTemplate.getExceptionTranslator().translate("parse resultSet", "parse resultSet of src ", e);
        }

    }

    /**
     * executeBatch只接受数组参数
     * 此处使用数组是为了避免每次使用toArray()时的不必要的数组复制
     *
     * @param rowsToInsert
     * @param len
     */
    public void completeBatch(SqlParameterSource[] rowsToInsert, int len) {
        Assert.notNull(rowsToInsert);
        if (len == -1) {//最后一次，数组没有被放满，需截去为空的部分
            int notNull = -1;
            for (int i = 0; i < rowsToInsert.length; i++) {
                if (rowsToInsert[i] != null) {
                    notNull = i;
                } else break;
            }
            //此时notNull是数组中最后一个非空值的下标
            SqlParameterSource[] ret=Arrays.copyOfRange(rowsToInsert, 0, notNull + 1);

            //logger.error(String.format("notNull : %d, ret size: %d",notNull,ret.length));
            jdbcInsert.executeBatch(ret);
            if (logger.isDebugEnabled()) {
                logger.debug("batch insert on table " + tableName + " complete");
            }
        } else {
            jdbcInsert.executeBatch(rowsToInsert);
            if (logger.isDebugEnabled()) {
                logger.debug(len + " rows insert into table " + tableName);
            }
        }

    }

}
