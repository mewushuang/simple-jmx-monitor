package com.van.service;

import com.rtdb.interfaces.merge.ConcreteRtdbMerge;
import com.rtdb.interfaces.merge.RtdbMerge;
import com.rtdb.interfaces.query.ConcreteRtdbQuery;
import com.rtdb.interfaces.query.RtdbQuery;
import com.van.common.Assert;
import com.van.common.ScodeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by van on 2016/12/10.
 */
@Service
public class RTDBService {

    private final Logger logger = LoggerFactory.getLogger(RTDBService.class);
    private final Lock tableLock = new ReentrantLock();
    private final Lock fieldLock = new ReentrantLock();

    @Autowired
    private ProducerService producerService;

    private final String contextId = "01";
    private final String appId = "0101";
    private final String keyDelimiter = "-";

    //“tt”字段说明:  01:天累计， 02：5分钟分段 ，03：月累计。
    private final String TT01 = "8";
    private final String TT02 = "7";
    private final String TT03 = "9";
    private final String TIME_FIELD_RT = "time";
    private final String TIME_FIELD_SEAT = "gtime";

    /**
     * RTDB查询实例
     */
    private RtdbQuery rtdbQuery = new ConcreteRtdbQuery();
    private RtdbMerge rtdbMerge = new ConcreteRtdbMerge();

    /**
     * 缓存指标对于的表Id集合
     */
    private Map<String, Set<String>> pid_TableIds;

    /**
     * 缓存接口json属性名与业务表域Id的对应集合
     */
    private Map<String, Map<String, String>> tableId_JFieldNameDFieldId;

    @Value("${kafka.topic}")
    private String updateTopic;


    /**
     * 保存一条entity到rtdb
     *
     * @param entity
     */
    @Async
    public void saveEntity(ScodeEntity entity) {
        List<String> toNotice = new ArrayList<>();
        Assert.asertNotNull(entity.getType(), "entity type unset");
        Set<String> tableIds = getTableIdsByPId(entity.getScode());

        if (tableIds == null || tableIds.size() == 0) {
            logger.error("cannot get table id with scode[" + entity.getScode() + "]");
            return;
        }
        for (String str : tableIds) {
            Map<String, Map<String, String>> outer = new HashMap<>();
            if (entity.getData() == null) {
                logger.warn(String.format("empty packet, type:%d, scode:%s, time:%s\n", entity.getType(), entity.getScode(), entity.getTime()));
                continue;
            }
            for (Object o : entity.getData()) {
                String code;
                String scode = entity.getScode();
                Map<String, String> unit = (Map<String, String>) o;
                unit.put(TIME_FIELD_RT, entity.getTime());
                unit.put(TIME_FIELD_SEAT, entity.getTime());
                String keyPrefix = contextId + keyDelimiter + appId + keyDelimiter + str + keyDelimiter + entity.getScode() + "_";
                if (entity.getType() == ScodeEntity.SEAT_TYPE) {
                    code = dealSeatUnit(str, scode, unit, outer, toNotice, keyPrefix);
                } else {
                    code = dealRTUnit(str, scode, unit, outer, toNotice, keyPrefix);
                }


            }
            //入实时库
            rtdbMerge.mergeTableRecordsById(contextId, appId, str, outer);
            producerService.noticeChangedKeys(updateTopic, toNotice);

        }

    }


    /**
     * 实时指标包含频度字段，设计的乱七八糟的数模只靠维度编码不足以确定唯一性，给出的解决办法
     * 是根据维度编码put前先查看是否包含，是更新，否添加
     *
     * @param tableName 数据库表名
     * @param unit      指标数据集合
     * @param scode     指标编码 和tableName拼接成recordId
     * @param outer     用来批量入库的外层map
     * @param toNotice  用来批量通知消息队列的list
     * @param keyPrefix 放入toNotice的key的前缀
     */
    private String dealRTUnit(String tableName, String scode, Map<String, String> unit, Map<String, Map<String, String>> outer, List<String> toNotice, String keyPrefix) {
        String code = unit.get("code");

        if (code != null) {
            keyPrefix = keyPrefix + code;
            String key = scode + "_" + code;
            Map<String, String> exists = outer.get(key);
            Map<String, String> ret = convertMap(tableName, unit, exists, toNotice, keyPrefix);
            outer.put(key, ret);
            return code;
        }
        return null;
    }

    /**
     * 坐席不存在实时字段的问题，但要根据dnum判断判断code值
     *
     * @param tableName 数据库表名
     * @param unit      指标数据集合
     * @param scode     指标编码 和tableName拼接成recordId
     * @param outer     用来批量入库的外层map
     * @param toNotice  用来批量通知消息队列的list
     * @param keyPrefix 放入toNotice的key的前缀
     */
    private String dealSeatUnit(String tableName, String scode, Map<String, String> unit, Map<String, Map<String, String>> outer, List<String> toNotice, String keyPrefix) {
        Object dnum = unit.get("dnum");
        if (dnum == null) return null;
        String code = null;
        switch (Integer.parseInt(dnum.toString())) {
            case 1://中心维度
                code = unit.get("p_center_no");
                break;
            case 2://分中心维度
                code = unit.get("center_no");
                break;
            case 3://部门维度
                code = unit.get("dept_no");
                break;
            case 5://班组维度
                code = unit.get("team_no");
                break;
            case 6://坐席维度
                code = unit.get("seat_no");
                break;
            default:
                code = null;
        }
        if (code != null) {
            keyPrefix = keyPrefix + code;
            Map<String, String> ret = convertMap(tableName, unit, null, toNotice, keyPrefix);
            outer.put(scode + "_" + code, ret);
            return code;
        }
        return null;
    }

    /**
     * 将json解析来的map转化字段准备入库
     * 本可以不必转换，但领导要求的字段名不一致
     * 并且强行把不同频度的值塞到了一个指标+维度确定的对象里
     *
     * @param src
     * @param base      如果不为空，表示根据code检索到了数据（已经被put过一次），在base的基础上做更新
     * @param toNotice
     * @param keyPrefix @return
     */
    private Map<String, String> convertMap(String tableName, Map src, Map base, List<String> toNotice, String keyPrefix) {
        Map<String, String> ret = base == null ? new HashMap<>() : base;
        Map<String, String> map = src;
        for (Map.Entry<String, String> e : map.entrySet()) {
            String key = getFieldIdByTableIdAndJFieldName(tableName, e.getKey());

            //只存入有对应关系的数据
            if (key != null) {
                ret.put(key, e.getValue());
                toNotice.add(keyPrefix + keyDelimiter + key);
            }
        }
        Object tt = map.get("tt");
        if (tt == null) return ret;//没有tt字段
        //“tt”字段说明:  01:天累计， 02：5分钟分段 ，03：月累计。
        if (Objects.equals(tt.toString(), "02")) {
            ret.put(TT02, map.get("v"));
            toNotice.add(keyPrefix + keyDelimiter + TT02);//添加指标更新通知 细化到field
        } else if (Objects.equals(tt.toString(), "01")) {
            ret.put(TT01, map.get("v"));
            toNotice.add(keyPrefix + keyDelimiter + TT01);
        } else if (Objects.equals(tt.toString(), "03")) {
            ret.put(TT03, map.get("v"));
            toNotice.add(keyPrefix + keyDelimiter + TT03);
        }
        return ret;
    }


    /**
     * 根据指标编码查询对应的tableId
     *
     * @param v_PId 指标编码
     * @return 表Id集合 没有查找到则返回null
     */
    public Set<String> getTableIdsByPId(String v_PId) {
        Set<String> v_TableIds = null;
        tableLock.lock();
        try {
            if (pid_TableIds == null) {
                reloadPTCache();
            }
        } finally {
            tableLock.unlock();
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

    /**
     * 根据指定的tableId和json报文中的属性名称查询实时库对应的域Id
     *
     * @param v_TableId    实时库的表Id
     * @param v_JFieldName son报文中的属性名称
     * @return 没有查找到则返回null
     */
    public String getFieldIdByTableIdAndJFieldName(String v_TableId, String v_JFieldName) {
        String v_FieldId = null;

        fieldLock.lock();
        try {
            if (tableId_JFieldNameDFieldId == null) {
                reloadFieldCache();
            }
        } finally {
            fieldLock.unlock();
        }


        if (tableId_JFieldNameDFieldId != null && tableId_JFieldNameDFieldId.containsKey(v_TableId)) {
            v_FieldId = tableId_JFieldNameDFieldId.get(v_TableId).get(v_JFieldName);
        }

        return v_FieldId;
    }

    /**
     * 重新加载json字段-域名映射
     */
    public void reloadFieldCache() {
        tableId_JFieldNameDFieldId = new Hashtable<String, Map<String, String>>();
        Map<String, Map<String, String>> dataMap = rtdbQuery.getTableRecordsById(null, null, "1001");
        Iterator<String> iterator = dataMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Map<String, String> record = dataMap.get(key);
            if (record != null && !record.isEmpty()) {
                String tableId = record.get("2"); // 业务表Id
                String fieldId = record.get("1"); // 域Id
                String jFieldName = record.get("9"); // 对应的接口json属性名

                if (jFieldName != null && !jFieldName.isEmpty()) {
                    if (!tableId_JFieldNameDFieldId.containsKey(tableId)) {
                        Map<String, String> jFieldName_DfieldId = new Hashtable<String, String>();
                        jFieldName_DfieldId.put(jFieldName, fieldId);
                        tableId_JFieldNameDFieldId.put(tableId, jFieldName_DfieldId);
                    } else {
                        tableId_JFieldNameDFieldId.get(tableId).put(jFieldName, fieldId);
                    }
                }
            }
        }
    }

    /**
     * 接口服务关闭时释放缓存数据
     */
    public void release() {
        pid_TableIds = null;
        tableId_JFieldNameDFieldId = null;
    }

    public static void main(String[] args) {
        RTDBService service = new RTDBService();
        ParseService p=new ParseService();
        try {
            BufferedReader src=new BufferedReader(new FileReader(new File("E:\\study\\python\\file_dealing\\one-piece-of-seat-packet.txt")));
            String i=src.readLine();
            List<ScodeEntity> e=p.parseRawMsgOfSeat(i);
            for(ScodeEntity sc:e){
                service.saveEntity(sc);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*String pId = "P01_02503"; // 就绪人数 - 指标编码
        String dimId = "7110101"; // 北方分中心 - 维度编码
        String recordId = pId + "_" + dimId; // 记录Id
        String jFieldName2 = "gtime";
        Set<String> v_TableIds = service.getTableIdsByPId(pId);
        for (String tableId : v_TableIds) {
            String fieldId = service.getFieldIdByTableIdAndJFieldName("2000", jFieldName2);

            // 态Id-应用Id-表Id-记录Id-域Id
            String keyId = "01-0101-" + tableId + "-" + recordId + "-" + fieldId;
            Map<String, String> value = service.rtdbQuery.getPointValue(keyId);
            System.out.println(value);
            service.rtdbMerge.mergeRecordFieldById(keyId, (new Date().toString()));
            value = service.rtdbQuery.getPointValue(keyId);
            System.out.println(value);
        }*/
    }
}
