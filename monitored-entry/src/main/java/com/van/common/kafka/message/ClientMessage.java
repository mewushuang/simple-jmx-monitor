package com.van.common.kafka.message;

import java.util.Set;

/**
 * 单个客户端报文体内容实体
 * 
 * @author lq
 *
 */
public class ClientMessage {
	public static final String KEYID = "keyid";
	public static final String FREQ = "freq";
	public static final String SOURCE = "source";

	private Set<String> keyid;
	private String freq;
	private String source;

	public ClientMessage() {
		super();
	}

	public ClientMessage(Set<String> keyid, String freq, String source) {
		super();
		this.keyid = keyid;
		this.freq = freq;
		this.source = source;
	}

	public Set<String> getKeyid() {
		return keyid;
	}

	public void setKeyid(Set<String> keyid) {
		this.keyid = keyid;
	}

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "ClientMessage [keyid=" + keyid + ", freq=" + freq + ", source=" + source + "]";
	}
}
