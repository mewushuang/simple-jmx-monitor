/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.van.entry;

import com.van.monitor.api.RunningStatusMetric;
import com.van.monitor.api.SimpleAtomicStatus;
import com.van.receiver.EntryConfig;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * (<strong>Entry Point</strong>) Starts SumUp client.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class Client {

    private final static Logger logger = LoggerFactory.getLogger(Client.class);
    public static final String SEAT_MODULE = "seat";
    public static final String RT_MODULE = "rt";
    private final EntryConfig cfg;

    private NioSocketConnector connector;
    private IoSession session;
    private AddressManager.AddressConfigurationInstance am;
    private SimpleAtomicStatus<RunningStatusMetric.RunningStatus> status;
    private ClientSessionHandler handler;
    private volatile boolean stopping=false;
    private String propertyPrefix;
    private PacketConsumer consumer;

    public Client(String module, EntryConfig cfg, AddressManager.AddressConfigurationInstance am,PacketConsumer consumer) {
        this.cfg=cfg;
        this.am=am;
        this.propertyPrefix=module;
        this.consumer=consumer;
        status = new SimpleAtomicStatus<>(RunningStatusMetric.RunningStatus.stopped);
        handler = new ClientSessionHandler(module, consumer);

        //connector stop后不能继续使用, 此段逻辑移到start方法中
        //initMina(module);
    }


    public void startHighAvailable() {

        //此处判断用户是否重复启动
        boolean canStart = status.inAndSet(RunningStatusMetric.RunningStatus.starting,
                RunningStatusMetric.RunningStatus.stopped,
                RunningStatusMetric.RunningStatus.stopping);
        if (!canStart) return;
        initMina(propertyPrefix);
        stopping=false;
        while (!isStopping()) {
            try {
                //循环开始，将状态重置为blocking
                status.notInAndSet(RunningStatusMetric.RunningStatus.blocking, RunningStatusMetric.RunningStatus.blocking);

                logger.info("接口服务["+propertyPrefix+"]启动。。。");

                service();
            } catch (Throwable e) {
                logger.warn("blocking interrupted or service occurs error, retrying to acquire the lock", e);
            }
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupted");
            }
        }
        status.set(RunningStatusMetric.RunningStatus.stopped);

    }

    private boolean isStopping() {
        return stopping;
    }

    public RunningStatusMetric.RunningStatus getRunningStatus() {
        return status.get();
    }

    public void service() throws Throwable {
        //连接断开后handle主动断开并关闭session，此处怎重新连接进入下一个事件循环
        status.notInAndSet(RunningStatusMetric.RunningStatus.blocking, RunningStatusMetric.RunningStatus.blocking);
        while (!isStopping()) {
            InetSocketAddress isa = am.getNextAddress();
            if (logger.isInfoEnabled()) {
                logger.info("instance["+propertyPrefix+"] starting to connect remote server:" + isa.toString());
            }
            try {
                ConnectFuture future = connector.connect(isa);
                future.awaitUninterruptibly();
                session = future.getSession();
                //设置读写超时时间
                session.getConfig().setBothIdleTime(cfg.getTimeout());

                //client放入session，以便异常时重新发起连接
                //session.setAttribute("client",this);
                if (logger.isInfoEnabled()) {
                    logger.info("instance["+propertyPrefix+"] session initialized with remote address:" + isa.toString());
                }
                am.resetFailedAttemps();
                // 阻塞在事件循环进程
                status.notInAndSet(RunningStatusMetric.RunningStatus.running, RunningStatusMetric.RunningStatus.running);
                if (session != null) session.getCloseFuture().await();
            } catch (Exception e) {
                String msg=e.getMessage();
                if(e.getCause()!=null) msg=msg+",caused by "+e.getCause().getMessage();
                logger.warn("instance["+propertyPrefix+"] failed when working with " + isa.toString()+", error msg is: "+msg);
                e.printStackTrace();
                Thread.sleep(5000);

            }
        }


    }

    public void stop() {
        stopping=true;
        status.notInAndSet(RunningStatusMetric.RunningStatus.stopping, RunningStatusMetric.RunningStatus.stopping);
        connector.dispose(true);
        connector=null;
        handler.release();
        status.notInAndSet(RunningStatusMetric.RunningStatus.stopped, RunningStatusMetric.RunningStatus.stopped);
    }

    private void initMina(String module) {
        connector = new NioSocketConnector();
        // 设置连接超时时间
        connector.setConnectTimeoutMillis(cfg.getTimeout());
        //读写超时时间在session里设置

        TextLineCodecFactory codecFactory = new TextLineCodecFactory(Charset.forName("utf-8"), "\0", "\0");
        codecFactory.setDecoderMaxLineLength(Integer.MAX_VALUE);
        codecFactory.setEncoderMaxLineLength(Integer.MAX_VALUE);
        connector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(codecFactory));

        //connector.getFilterChain().addLast("logger", new LoggingFilter());


        connector.setHandler(handler);

    }

    @Override
    protected void finalize() throws Throwable {
        connector.dispose();
        super.finalize();
    }
}
