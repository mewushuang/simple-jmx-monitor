package com.van.common.kafka.message;

import java.util.Set;

/**
 * 服务内部传递报文实体
 * 
 * @author lq
 *
 */
public class TransferMessage {
	private String messageType;
	private Set<String> realTimePoints;
	private Set<String> taskDelayPoints;
	private String source;

	public TransferMessage() {
		super();
	}

	public TransferMessage(String messageType, Set<String> realTimePoints, Set<String> taskDelayPoints, String source) {
		super();
		this.messageType = messageType;
		this.realTimePoints = realTimePoints;
		this.taskDelayPoints = taskDelayPoints;
		this.source = source;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Set<String> getRealTimePoints() {
		return realTimePoints;
	}

	public void setRealTimePoints(Set<String> realTimePoints) {
		this.realTimePoints = realTimePoints;
	}

	public Set<String> getTaskDelayPoints() {
		return taskDelayPoints;
	}

	public void setTaskDelayPoints(Set<String> taskDelayPoints) {
		this.taskDelayPoints = taskDelayPoints;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "TransferMessage [messageType=" + messageType + ", realTimePoints=" + realTimePoints + ", taskDelayPoints=" + taskDelayPoints
				+ ", source=" + source + "]";
	}

}
