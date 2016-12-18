package com.van.common;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PacketRecorder {
	private static Logger logger=Logger.getLogger(PacketRecorder.class);
	private static int i=0;
	private String path;
	private File des;
	private PrintWriter pw;
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy_MM_dd");
	private SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	private Date curDate;
	private static Map<String,PacketRecorder> recorders=new HashMap<>();
	private PacketRecorder(String  logPrefix) {
		File dir=new File("recv");
		if(!dir.exists()||dir.isFile()){
			dir.mkdir();
		}
		this.path="recv"+File.separator+logPrefix;

	}

	public static PacketRecorder instance(String  logPrefix){
		synchronized (PacketRecorder.class){
			if(recorders.get(logPrefix)==null){
				recorders.put(logPrefix,new PacketRecorder(logPrefix));
			}
			return recorders.get(logPrefix);
		}
	}

	private Date testDate() {
		Date now=new Date();
		if(pw!=null&&des!=null&&curDate!=null&&sdf.format(now).equals(sdf.format(curDate))&&des.exists()){
			return now;
		}else{
			des=new File(path+"_"+sdf.format(now)+".log");
			if(pw!=null)
				pw.close();
			try {
				pw=new PrintWriter(des,"UTF-8");
			} catch (FileNotFoundException e) {
				logger.error(path+"_"+sdf.format(curDate)+".log未找到", e);
			} catch (UnsupportedEncodingException e) {
			}
			curDate=now;
		}
		return now;
	}

	public void write(String msg,String ps){
		ps=ps==null?"":ps;
		try {
			synchronized (PacketRecorder.class) {
				Date now=testDate();
				pw.println(sdf1.format(now)+":"+ps);
				pw.println(msg);
				pw.flush();
				//logger.info("logged msgs:"+(++PacketRecorder.i));
			}
		} catch (Throwable e) {
			String ret="null";
			if(des!=null){
				ret=des.getPath();
			}
			logger.error("io异常，File:"+ret, e);
		}
	}
	public void write(String msg){
		write(msg,"");
	}

}
