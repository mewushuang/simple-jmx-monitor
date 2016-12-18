package com.van.service;/*
package com.van.service;

import com.van.filter.Filter;
import com.van.filter.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Map;

*/
/**
 * 存入缓存
 * Created by van on 2016/11/9.
 *//*

@Component
public class CacheFilter implements Filter {

    private final static Logger logger= LoggerFactory.getLogger(CacheFilter.class);

    private Filter next;

    @Resource
    private MainCachePool mainCachePool;

    @Override
    public void filt(MessageContext context) {
        Jedis jedis;
        try {
            jedis=mainCachePool.getResource();
            String key= (String) context.getRecord().key();
            String val= (String) context.getRecord().value();

            getNext().filt(context);//触发next动作
        } catch (Exception e) {
            // TODO: 2016/11/9 解析异常，通过kafka通知接口发送失败回馈

            if (logger.isErrorEnabled()) {
                logger.error("parse failure on msg from kafka",e);
            }
            //该消息缓存失败，退出针对该消息的filterChain
        }
    }

    public void indexItem(Map<String,Object> item,Jedis jedis){

    }

    public void saveItem(Map<String,Object> item,Jedis jedis){

    }

    @Override
    public Filter getNext() {
        return next;
    }

    public void setNext(Filter next) {
        this.next = next;
    }

    @Override
    public boolean hasNext() {
        return getNext()!=null;
    }
}
*/
