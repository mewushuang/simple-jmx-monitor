package com.van.data.syncer.config;

import com.rtdb.interfaces.query.ConcreteRtdbQuery;
import com.rtdb.interfaces.query.RtdbQuery;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Created by van on 2016/12/27.
 */
public class ScheduledTaskConfigurationTest {
    private Hashtable<String, Set<String>> pid_TableIds;

    @Test
    public void array() {
        String[] i = Arrays.copyOfRange(new String[]{"a", "b", "c", "d", "e"}, 0, 0);
        System.out.println(i);

    }
    @Test
    public void testPid(){
        getPid();
    }
    private String getPid(){
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);
        String pid = name.split("@")[0];
        System.out.println("Pid is:" + pid);
        return pid;
    }

    @Test
    public void testRtdb(){
        Set<String> i=getTableIdsByPId("P01_02503");
        System.out.println(i);
    }

    /**
     * 根据指标编码查询对应的tableId
     *
     * @param v_PId 指标编码
     * @return 表Id集合 没有查找到则返回null
     */
    public Set<String> getTableIdsByPId(String v_PId) {
        Set<String> v_TableIds = null;
        if (pid_TableIds == null) {
            reloadPTCache();
        }

        if (pid_TableIds != null && pid_TableIds.containsKey(v_PId)) {
            v_TableIds = pid_TableIds.get(v_PId);
        }

        return v_TableIds;
    }

    /**
     * 重新加载指标-数据库表名映射关系
     */
    public void reloadPTCache() {
        pid_TableIds = new Hashtable<String, Set<String>>();
        RtdbQuery rtdbQuery = new ConcreteRtdbQuery();
        Map<String, Map<String, String>> dataMap = rtdbQuery.getTableRecordsById(null, null, "9999");
        Iterator<String> iterator = dataMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Map<String, String> record = dataMap.get(key);
            if (record != null && !record.isEmpty()) {
                String pId = record.get("PID");
                String tableId = record.get("TID");

                if (tableId != null && !tableId.isEmpty()) {
                    if (!pid_TableIds.containsKey(pId)) {
                        Set<String> tableIds = new HashSet<String>();
                        tableIds.add(tableId);
                        pid_TableIds.put(pId, tableIds);
                    } else {
                        pid_TableIds.get(pId).add(tableId);
                    }
                }
            }
        }
    }
}