package com.van.monitor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by van on 2016/12/2.
 */
public class ResponseJsonSerialization {


    private ObjectMapper mapper=new ObjectMapper();

    protected String serialize(Object o){
        String ret;
        try {
            ret= mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            ret= "{\"code\":500,\"msg\":\"error when serializing result into json on remote machine\"}";
        }
        return ret;
    }

    protected String getSuccResponse(Object data){
        return serialize(RestResponse.getSuccResponse(data));
    }

    protected String getEmptySuccResponse(){
        return getSuccResponse(null);
    }

    protected String getResponse(int code,String msg,Object data){
        return serialize(new RestResponse(code,msg,data));
    }
}
