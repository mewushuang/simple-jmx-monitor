package com.van.common;

import com.van.service.TransferConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rabit on 2016/6/2.
 */
public class AddressManager {
    private static Logger logger= LoggerFactory.getLogger(AddressManager.class);

    private static AddressManager manager;

    private List<ServerAddress> list;

    //当前所连接服务器地址
    private ServerAddress currentServer;

    private TransferConfiguration config;
    private static Map<String,AddressManager> ams=new HashMap<>();

    private int failedAttemps=0;


    /**
     * 连接成功后重置此值
     */
    public void resetFailedAttemps(){
        failedAttemps=0;
    }

    /**
     * 故障时使用轮流切换策略
     */
    public InetSocketAddress getNextAddress(){
        failedAttemps++;
        //如果重试了所有节点地址，仍然连不上，记录fatal日志到服务器。
        if(failedAttemps>list.size()+1){
            if(logger.isErrorEnabled())
                logger.error("No remote address valid!");
            try {
                Thread.sleep(1000*15);
            } catch (InterruptedException e) {
            }
        }
        if(list.size()==0){
            throw new IllegalArgumentException("Invalid configuration on server address: no server configured");
        }
        if(currentServer!=null){
            currentServer.failedAttempts++;
        }
        int curIdx=list.indexOf(currentServer);
        if(curIdx==-1){
        	currentServer=list.get(0);
            return new InetSocketAddress(list.get(0).ip,list.get(0).port);
        }
        if(++curIdx>=list.size()){
           curIdx = curIdx%list.size();
        }
        ServerAddress tmp=list.get(curIdx);
        currentServer=tmp;
        return new InetSocketAddress(tmp.ip,tmp.port);
    }



    private AddressManager(List<ServerAddress> list,TransferConfiguration config){
        this.list=list;
        this.config=config;
    }

    static class ServerAddress{
        public String ip;
        public int port;
        public volatile int failedAttempts;

        public ServerAddress(String ip, int port) {
            this.ip = ip;
            this.port = port;
            this.failedAttempts=0;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb=new StringBuffer();
        for(ServerAddress sa:list){
            sb.append(sa.ip).append(":").append(sa.port).append(",");
        }

        if(sb.lastIndexOf(",")==sb.length()-1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * cfg的格式："ip1:port1,ip2:port2[,ip3:port3...]"
     * @return
     */
    public static synchronized AddressManager instance(TransferConfiguration config,String propertyPrefix) {
        if(ams.get(propertyPrefix)==null){
            manager=reloadFromCfg(config,propertyPrefix);
            ams.put(propertyPrefix,manager);
        }
        return manager;
    }

    /**
     * 修改配置时使用,强制从配置文件更新单例
     * @return
     */
    public static AddressManager reloadFromCfg(TransferConfiguration config,String propertyPrefix){
        String cfg= config.get(propertyPrefix+"connect.node").toString();
        if(cfg==null||cfg.equals("")){
            throw new IllegalArgumentException("Invalid configuration on server address:"+cfg);
        }
        String[] tmp = cfg.split(",");
        ArrayList<ServerAddress> list = new ArrayList<>();
        for (String s : tmp) {
            if (s == null || s.equals("")) {
                continue;
            }
            String[] node = s.split(":");
            if (node.length != 2) {
                continue;
            }
            list.add(new ServerAddress(node[0], Integer.parseInt(node[1])));
        }
        if(list.size()==0){
            throw new IllegalArgumentException("Invalid configuration on server address:"+cfg);
        }
        manager=new AddressManager(list,config);
        return manager;
    }

}
