package com.van;

import com.van.entry.*;
import com.van.receiver.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by van on 17-4-25.
 */
@Component
public class EntryContext {

    private Client seat = null;
    private Client rt = null;

    @Autowired
    private EntryConfig entryConfig;
    @Resource
    private SeatConsumer seatConsumer;
    @Resource
    private RtConsumer rtConsumer;
    @Autowired
    private AddressManager addressManager;

    @PostConstruct
    public void init(){
        seat=new Client(Client.SEAT_MODULE,entryConfig,addressManager.seatAddress(),seatConsumer);
        rt=new Client(Client.RT_MODULE,entryConfig,addressManager.rtAddress(),rtConsumer);

    }

    public Client getSeat() {
        return seat;
    }

    public Client getRt() {
        return rt;
    }


}
