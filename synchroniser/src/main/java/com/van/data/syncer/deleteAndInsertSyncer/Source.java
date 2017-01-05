package com.van.data.syncer.deleteAndInsertSyncer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * Created by van on 2016/12/27.
 */
public class Source {

    private JdbcTemplate jdbcTemplate;
    private final String sql;

    /*public Source(DataSource dataSource,String sql) {
        jdbcTemplate=new JdbcTemplate(dataSource);
        this.sql=sql;
    }*/

    public Source(JdbcTemplate jdbcTemplate,String sql) {
        this.jdbcTemplate=jdbcTemplate;
        this.sql=sql;
    }

    public void queryAndHandle(RowCallbackHandler handler){
        jdbcTemplate.query(sql, handler);
    }
}
