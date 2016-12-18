package com.van.deprecated.oracle;/*
package com.van.persistence.oracle;

import com.van.persistence.common.Column;
import com.van.persistence.common.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * Created by van on 2016/10/31.
 * 针对大规模多表无序频繁插入做了优化：
 * 考虑到目前preparedStatement不支持异构sql的批量更新
 * 1，根据表名缓存sql语句
 * 2，手动注入参数生成最终sql批量执行
 *//*

@Component("oracleDao")
public class OracleDao {
    @Resource
    private JdbcTemplate template;

    @Resource
    private DateUtil dateUtil;

    private static Logger logger= LoggerFactory.getLogger(OracleDao.class);

    private Map<String,List<Column>> tables=new HashMap<>();



    protected List<Column> getTableMeta(String tablename){
        List<Column> cols=tables.get(tablename);
        if(cols==null){
            //TODO
        }
        return cols;
    }

    protected void createTable(String tablename){

    }

    protected void insert(String tablename,Map<String,Object> kvs){
        if(kvs==null||kvs.size()==0){
            if(logger.isWarnEnabled()){
                logger.warn("trying to insert null or empty object into table:"+tablename);
            }
            return;
        }
        StringBuilder sql=new StringBuilder("insert into "+tablename+" (");
        StringBuilder values=new StringBuilder(" values(");
        Object[] params=new Object[kvs.size()];
        int i=0;
        for (Map.Entry<String,Object> e : kvs.entrySet()) {
            sql=sql.append(e.getKey()).append(',');
            values=values.append('?').append('<').append(e.getKey()).append(">,");
            params[i++]=e.getValue();
        }
        sql.deleteCharAt(sql.length()-1);
        values.deleteCharAt(values.length()-1);
        sql.append(")").append(values).append(")");
        template.update(sql.toString(),params);
    }

    */
/**
     * 参数的手动注入,生成可直接插入的sql
     *//*

    private String genSqlFromTemplate(String template,Map<String,Object> kvs){
        //TODO
        return null;
    }
}
*/
