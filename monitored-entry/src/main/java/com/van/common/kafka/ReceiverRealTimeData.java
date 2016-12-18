package com.van.common.kafka;

import java.util.Observer;

/**
 * 订阅kafka消息队列"RealTimeData"主题的数据,数据接入服务向此主题发布实时变化数据
 * 
 * @author zsk
 *
 */
public class ReceiverRealTimeData implements Runnable {
	public static final String dataReceiverTopic = "RealTimeData";
	private Observer observer;

	public ReceiverRealTimeData(Observer observer) {
		this.observer = observer;
	}

	public void run() {
		Receiver receiver = new Receiver();
		receiver.receiveMessage(dataReceiverTopic, observer);
	}
}
