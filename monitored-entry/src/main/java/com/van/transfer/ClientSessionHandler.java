package com.van.transfer;

import com.van.service.PacketConsumer;
import com.van.service.RtConsumer;
import com.van.service.SeatConsumer;
import com.van.service.TransferConfiguration;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} for SumUp client.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ClientSessionHandler extends IoHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);

    private PacketConsumer consumer;

    private int module;

    public ClientSessionHandler(int module,TransferConfiguration config) {
        this.module=module;
        if(this.module==Client.SEAT_MODULE) {
            this.consumer = new SeatConsumer(config);
        }else if(this.module==Client.RT_MODULE){
            this.consumer=new RtConsumer(config);
        }else {
            throw new IllegalArgumentException("illegal argument module:"+module+",expect "+Client.RT_MODULE+" or "+Client.SEAT_MODULE);
        }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        String initMsg=consumer.getInitParam();
        if (initMsg!=null) {
            session.write(initMsg);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("打开socket,写入初始化参数："+consumer.getInitParam());
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if(LOGGER.isErrorEnabled()){
            LOGGER.error("IO异常！",cause);
        }
        session.closeNow();

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String rawMsg= (String) message;
        String resp=consumer.getResponse(rawMsg);
        if(resp!=null){
            session.write(resp);
        }
        consumer.consume(rawMsg);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        if(LOGGER.isErrorEnabled()){
            LOGGER.error("remote socket closed!");
        }
        session.closeNow();
    }

    public void release(){
        consumer.releaseResource();
    }
}
