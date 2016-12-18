package com.van.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.common.JsonParseExcepion;
import com.van.common.ScodeEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by van on 2016/12/10.
 */
@Service
public class ParseService {

    //private final Logger logger= LoggerFactory.getLogger(ParseService.class);


    private ObjectMapper mapper=new ObjectMapper();

    /**
     * 报文结构：
     *{"time":"2016-10-12 11:48:41","data":[
     *              {"dim":"con","v":"0","tt":"01","code":"21408"},...
     * ]}
     * @param scode 前文重报文头得到的指标名
     * @param rawMsg 待解析的报文体
     * @return
     */
    public ScodeEntity parseRawMsgOfRt(String scode, String rawMsg) {
        try {
            JsonNode root = mapper.readTree(rawMsg);

            ScodeEntity entity=new ScodeEntity();
            entity.setScode(scode);
            entity.setType(ScodeEntity.RT_TYPE);
            entity.setTime(root.get("time").asText());
            entity.setData(mapper.convertValue(root.get("data"),List.class));
            return entity;
        } catch (IOException e) {
            throw new JsonParseExcepion("parse failure on rt data:"+scode+"msg:\n"+rawMsg);
        }
    }

    /**
     * 报文结构：
     *[{data:[
     *      {},...
     *      ],gtime:"2016-10-12 11:48:46","scode":"P01_02007"}]
     * @param rawMsg 待解析的报文体
     * @return
     */
    public List<ScodeEntity> parseRawMsgOfSeat(String rawMsg)  {
        try {
            JsonNode root = mapper.readTree(rawMsg);
            List<ScodeEntity> ret=new ArrayList<>(10);

            for(JsonNode node : root){
                ScodeEntity entity=new ScodeEntity();
                entity.setType(ScodeEntity.SEAT_TYPE);
                entity.setScode(node.get("scode").asText());
                entity.setTime(node.get("gtime").asText());
                entity.setData(mapper.convertValue(node.get("data"),List.class));
                ret.add(entity);
            }
            return ret;
        } catch (IOException e){
            throw new JsonParseExcepion("parse failure on seat data msg:\n"+rawMsg);
        }
    }



}
