package com.van.data.syncer.config;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

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
            //reloadPTCache();
        }

        if (pid_TableIds != null && pid_TableIds.containsKey(v_PId)) {
            v_TableIds = pid_TableIds.get(v_PId);
        }

        return v_TableIds;
    }



}