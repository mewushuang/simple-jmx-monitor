package com.van.service;

import com.van.common.ScodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by van on 17-4-25.
 * 记录最后一次收到该指标的时间
 */
@Service
public class ScodeTimeRecorderService {

    private final Logger logger = LoggerFactory.getLogger(ScodeTimeRecorderService.class);

    @Autowired
    private DataSource dataSource;

    private final String sql = "merge into scode_history h using(\n" +
            "  select ? id,? frequency,? gen_time,sysdate local_time,? data_src from dual\n" +
            ") s on (s.id=h.scode_id and s.frequency=h.frequency)\n" +
            "when MATCHED then update set h.local_server_time=s.local_time,h.updated_at=s.gen_time,h.data_src=s.data_src\n" +
            "when not MATCHED THEN INSERT (h.scode_id,h.frequency,h.data_src,h.local_server_time,h.updated_at)\n" +
            "  VALUES (s.id,s.frequency,s.data_src,s.local_time,s.gen_time)";

    private Connection conn;
    private PreparedStatement ps;
    private int batchSize=200;
    private int currentSize=0;

    //@Async
    public  void recordTime(ScodeEntity e) {
        int f=getFrequency(e);
        synchronized(this) {
            initStmt();
            try {
                if (f % 7 == 0) {
                    ps.setString(1, e.getScode());
                    ps.setInt(2, 7);
                    ps.setTimestamp(3, Timestamp.valueOf(e.getTime()));
                    ps.setInt(4, e.getType());
                    ps.addBatch();
                    currentSize++;
                }
                if (f % 8 == 0) {
                    ps.setString(1, e.getScode());
                    ps.setInt(2, 8);
                    ps.setTimestamp(3, Timestamp.valueOf(e.getTime()));
                    ps.setInt(4, e.getType());
                    ps.addBatch();
                    currentSize++;
                }
                if (f % 9 == 0) {
                    ps.setString(1, e.getScode());
                    ps.setInt(2, 9);
                    ps.setTimestamp(3, Timestamp.valueOf(e.getTime()));
                    ps.setInt(4, e.getType());
                    ps.addBatch();
                    currentSize++;
                }
                if (currentSize >= batchSize) {
                    ps.executeBatch();
                    ps.close();
                    conn.close();
                    ps = null;
                    conn = null;
                }
            } catch (SQLException e1) {
                logger.error(e1.getMessage());
            }
        }
    }


    private int getFrequency(ScodeEntity e){
        int ret=1;
        if(e.getType()==ScodeEntity.RT_TYPE){
            if (e.getData()==null){
                return ret;
            }
            int f7=0;
            int f8=0;
            int f9=0;
            for (Object o: e.getData()){
                Map<String,String> map= (Map<String, String>) o;
                if("02".equals(map.get("tt"))){
                    f7++;
                }else if("01".equals(map.get("tt"))){
                    f8++;
                }else if("03".equals(map.get("tt"))){
                    f9++;
                }
            }
            if(f7>0){
                ret=ret*7;
            }
            if(f8>0){
                ret=ret*8;
            }
            if(f9>0){
                ret=ret*9;
            }
        }else if(e.getType()==ScodeEntity.SEAT_TYPE){
            ret=ret*7;
        }
        return ret;
    }


    private void initStmt() {
        if (ps != null) return;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
        } catch (SQLException e1) {
            logger.error(e1.getMessage());
        }
    }
}
