package com.van.common.kafka.message;

/**
 * 单个服务端报文体内容实体
 * 
 * @author zsk
 *
 */
public class ServerMessage {
	public static final String KEYID = "keyid";
	public static final String VALUE = "value";

	private String keyid;
	private String value;

	public ServerMessage() {
		super();
	}

	public String getKeyid() {
		return keyid;
	}

	public void setKeyid(String keyid) {
		this.keyid = keyid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
