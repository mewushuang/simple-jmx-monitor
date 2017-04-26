package com.van.entry;

import com.van.receiver.EntryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rabit on 2016/6/2.
 */
@Component
public class AddressManager {
    private static Logger logger = LoggerFactory.getLogger(AddressManager.class);

    //private static AddressManager manager;


    private List<ServerAddress> rtServers;

    Map<String, AddressConfigurationInstance> instances;

    private EntryConfig config;

    public AddressManager() {
        this.instances = new HashMap<>();
    }

    @Autowired
    public void setConfig(EntryConfig config) {
        this.config = config;
        this.instances.put("seat", new AddressConfigurationInstance("seat", config.getSeatServer()));
        this.instances.put("rt", new AddressConfigurationInstance("rt", config.getRtServer()));
    }

    public AddressConfigurationInstance seatAddress() {
        return this.instances.get("seat");
    }

    public AddressConfigurationInstance rtAddress() {
        return this.instances.get("rt");
    }


    public EntryConfig getConfig() {
        return config;
    }


    static class ServerAddress {
        public String ip;
        public int port;
        public volatile int failedAttempts;

        public ServerAddress(String ip, int port) {
            this.ip = ip;
            this.port = port;
            this.failedAttempts = 0;
        }

    }

    static class AddressConfigurationInstance {
        public int failedAttempts;
        public String name;
        public List<ServerAddress> list;
        //当前所连接服务器地址
        public ServerAddress currentServer;

        public AddressConfigurationInstance(String name, String cfg) {
            this.name = name;
            this.failedAttempts = 0;
            if (cfg == null || cfg.equals("")) {
                throw new IllegalArgumentException("Invalid configuration on server address:" + cfg);
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
            if (list.size() == 0) {
                throw new IllegalArgumentException("Invalid configuration on server address:" + cfg);
            }
            this.list = list;
        }

        /**
         * 连接成功后重置此值
         */
        public void resetFailedAttemps() {
            this.failedAttempts = 0;
        }

        /**
         * 故障时使用轮流切换策略
         */
        public InetSocketAddress getNextAddress() {
            this.failedAttempts++;
            //如果重试了所有节点地址，仍然连不上，记录fatal日志到服务器。
            if (this.failedAttempts > list.size() + 1) {
                if (logger.isErrorEnabled())
                    logger.error("No remote address valid!");
                try {
                    Thread.sleep(1000 * 15);
                } catch (InterruptedException e) {
                }
            }
            if (list.size() == 0) {
                throw new IllegalArgumentException("Invalid configuration on server address: no server configured");
            }
            if (currentServer != null) {
                currentServer.failedAttempts++;
            }
            int curIdx = list.indexOf(currentServer);
            if (curIdx == -1) {
                currentServer = list.get(0);
                return new InetSocketAddress(list.get(0).ip, list.get(0).port);
            }
            if (++curIdx >= list.size()) {
                curIdx = curIdx % list.size();
            }
            ServerAddress tmp = list.get(curIdx);
            currentServer = tmp;
            return new InetSocketAddress(tmp.ip, tmp.port);
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (ServerAddress sa : list) {
                sb.append(sa.ip).append(":").append(sa.port).append(",");
            }

            if (sb.lastIndexOf(",") == sb.length() - 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }
    }


}
