package com.van.data.syncer.deleteAndInsertSyncer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


@Service
public class SyncService {

    @Autowired
    @Qualifier("sourceJdbcTemplate")
    private JdbcTemplate sourceJdbcTemplate;
    @Autowired
    @Qualifier("destinationJdbcTemplate")
    private JdbcTemplate destinationJdbcTemplate;

    @Value("${sync.size}")
    private int size =1000;


    @Transactional
    public void sync(String sql,String destinationTable){
        final Source src=new Source(sourceJdbcTemplate,sql);
        final Target target=new Target(destinationJdbcTemplate,destinationTable);
        final SqlParameterSource[] batch=new SqlParameterSource[size];
        target.deleteAll();
        src.queryAndHandle(new RowCallbackHandler() {
            int idx=0;
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                SqlParameterSource i=target.convert(rs);
                batch[idx++]=i;
                if(idx>= size){
                    target.completeBatch(batch,idx);
                    idx=0;
                    Arrays.fill(batch,null);
                }
            }
        });
        target.completeBatch(batch,-1);
    }
}
