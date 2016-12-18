package com.van.deprecated;

import java.util.Map;

/**
 * Created by van on 2016/10/31.
 */
public interface Persistencer {
    //void persitence(String json);
    //void persitence(Object obj);
    void persitence(String tablename, Map map);
}
