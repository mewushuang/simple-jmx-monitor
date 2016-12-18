package com.van.service;

/**
 * Created by van on 2016/10/27.
 */
public interface PacketConsumer {


    String getInitParam();

    String getResponse(String rawMsg);

    void consume(String rawMsg);

    boolean isValid(String rawMsg);

    void releaseResource();

}
