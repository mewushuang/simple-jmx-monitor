package com.van;

import com.van.entry.AddressManager;
import com.van.entry.Client;
import com.van.entry.PacketConsumer;
import com.van.receiver.EntryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by van on 17-4-25.
 */
@Component
public class EntryContext {

    private Client seat = null;
    private Client rt = null;

    @Autowired
    private EntryConfig entryConfig;
    @Autowired
    private PacketConsumer seatConsumer;
    @Autowired
    private PacketConsumer rtConsumer;
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
