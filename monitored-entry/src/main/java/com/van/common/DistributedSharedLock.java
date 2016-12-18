package com.van.common;


import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.KeeperException;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;


/**
 * 从zookeeper获取分布式锁
 * @author rabit
 *
 */
public class DistributedSharedLock  {

    //private static  String addr = "127.0.0.1:2181";
    private static final String LOCK_NODE = "guid-lock-";
    private String rootLockNode; //锁目录
    private ZkClient zk = null;
    private Integer mutex;
    private Long currentLock;
    
    /*synchronized private static ZkClient getZkClient(){
    	if(zk==null){
    		zk=new ZkClient(addr,1000*2);
    	}
    	return zk;
    }*/
    
    /**
     * 
     * 初始化实例后不可再做地址更改
     * @param addr
     */
    /*synchronized public static void setZkAddress(String addr){
    	if(zk==null){
    		DistributedSharedLock.addr=addr;
    	}else{
    		throw new IllegalStateException("ZkClient已初始化，不可再次更改地址");
    	}
    }*/
    

    /**
     * 创建zk锁目录
     *
     * @param rootLockNode
     */
    public DistributedSharedLock(String addr,String rootLockNode) {
        this.rootLockNode = rootLockNode;
        //连接zk服务器
		zk = new ZkClient(addr,4*1000,10*1000);
        mutex = new Integer(-1);
        // Create ZK node name
        if (zk != null) {
        	zk.createPersistent(rootLockNode, true);
        }
    }

    /**
     * 请求zk服务器，获得锁
     * 该线程将阻塞直至获得锁
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void acquire() throws KeeperException {
        ByteBuffer b = ByteBuffer.allocate(4);
        byte[] value;
        // Add child with value i
        b.putInt(ThreadLocalRandom.current().nextInt(10));
        value = b.array();

        // 创建锁节点
        String lockName =zk.createEphemeralSequential(rootLockNode + "/" + LOCK_NODE, value);
        zk.subscribeChildChanges(rootLockNode, new ChildListener());
        synchronized (mutex) {
            while (!Thread.currentThread().isInterrupted()) {
                // 获得当前锁节点的number，和所有的锁节点比较
            	Long acquireLock = new Long(lockName.substring(lockName.lastIndexOf('-') + 1));
                List<String> childLockNode = zk.getChildren(rootLockNode);

                SortedSet<Long> sortedLock = new TreeSet<Long>();
                for (String temp : childLockNode) {
                    Long tempLockNumber = Long.parseLong(temp.substring(temp.lastIndexOf('-') + 1));
                    sortedLock.add(tempLockNumber);
                }

                currentLock = sortedLock.first();

                //如果当前创建的锁的序号是最小的那么认为这个客户端获得了锁
                if (currentLock >= acquireLock) {
                    System.err.println("thread_name=" + Thread.currentThread().getName() + "|attend lcok|lock_num=" + currentLock);
                    return;
                } else {
                    //没有获得锁则等待下次事件的发生
                    System.err.println("thread_name=" + Thread.currentThread().getName() + "|wait lcok|lock_num=" + currentLock);
                    try {
						mutex.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }
        }
    }


    /**
     * 释放锁
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public boolean release() throws KeeperException{
        String lockName = String.format("%010d", currentLock);
        boolean ret=zk.delete(rootLockNode + "/" + LOCK_NODE + lockName);
        if(ret){
        	System.err.println("thread_name=" + Thread.currentThread().getName() + "|release lcok|lock_num=" + currentLock);
        }else{
        	System.err.println("释放锁失败：thread_name=" + Thread.currentThread().getName() + "|release lcok|lock_num=" + currentLock);
        }
        return ret;
    }

    class ChildListener implements IZkChildListener{


		public void handleChildChange(String arg0, List<String> arg1)
				throws Exception {
			synchronized (mutex) {
	            mutex.notify();
	        }
			
		}
    	
    }

    /*public static void main(String[] args) throws KeeperException, InterruptedException {
    	String zookeeperServerList=TransferConfiguration.instance().getString("zookeeperServerList");
    	DistributedSharedLock.setZkAddress(zookeeperServerList);
    	final String path=TransferConfiguration.instance().getString("zookeeperPath");
		DistributedSharedLock lock=new DistributedSharedLock(path);
		lock.acquire();
		Thread.sleep(1000*20);
		lock.release();
		
	}*/
    
}