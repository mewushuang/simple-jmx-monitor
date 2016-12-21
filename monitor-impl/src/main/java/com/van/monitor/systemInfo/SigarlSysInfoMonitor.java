package com.van.monitor.systemInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.monitor.api.SysInfoMonitorMXBean;
import com.van.monitor.util.ResponseJsonSerialization;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by van on 2016/11/15.
 */
public class SigarlSysInfoMonitor extends ResponseJsonSerialization implements SysInfoMonitorMXBean {

    private Logger logger= LoggerFactory.getLogger(SigarlSysInfoMonitor.class);
    private DecimalFormat numberFormat=new DecimalFormat("#.00");
    private ObjectMapper mapper;

    private Sigar sigar ;

    public SigarlSysInfoMonitor() {
        this.mapper=new ObjectMapper();
        sigar = new Sigar();
    }

    /**
     * 获取元数据信息，一般不会变化
     *
     * @return josn格式的字符串
     */
    @Override
    public String getInvariableInfo() {
        return getChangefulInfo();
    }

    /**
     * 获取状态的断面信息，随时间变化
     *
     * @return josn格式的字符串
     */
    @Override
    public String getChangefulInfo() {
        Map<String,Object> changeful=new HashMap<>();
        Mem mem;
        CpuPerc cpuList[] = null;

        try {
            mem = sigar.getMem();
            // 当前内存剩余量
            changeful.put("freeMemGB", mem.getFree()/ 1024L / 1024L/1024L);
            // 内存总量
            changeful.put("totalMemGB", mem.getTotal() / 1024L / 1024L/1024L);
            // 当前内存使用量
            changeful.put("memUsagePercent", numberFormat.format(mem.getUsed()*100D/mem.getTotal()));

            //主机名
            changeful.put("hostname",sigar.getNetInfo().getHostName());
            //cpu占用率
            cpuList = sigar.getCpuPercList();
            double cpuTotal = 0.0;
            for (int i = 0; i < cpuList.length; i++) {
                //System.out.println(cpuList[i].getCombined());
                cpuTotal += cpuList[i].getCombined();// 总的使用率
            }
            changeful.put("cpuUsagePercent",numberFormat.format(cpuTotal / cpuList.length*100.0));
            changeful.put("cpuTotalNum",cpuList.length);

            FileSystem fslist[] = sigar.getFileSystemList();
            double total = 0;
            double avail = 0;
            for (int i = 0; i < fslist.length; i++) {
                FileSystem fs = fslist[i];
                FileSystemUsage usage = null;
                try {
                    usage = sigar.getFileSystemUsage(fs.getDirName());
                } catch (SigarException e) {
                    if (fs.getType() == 2)
                        throw e;
                    continue;
                }
                switch (fs.getType()) {
                    case 0: // TYPE_UNKNOWN ：未知
                        break;
                    case 1: // TYPE_NONE
                        break;
                    case 2: // TYPE_LOCAL_DISK : 本地硬盘
                        // 文件系统总大小
                        total += usage.getTotal() / 1024L/1024L;//GB
                        // 文件系统可用大小
                        avail += usage.getAvail() / 1024L/1024L;//GB
                        break;
                    case 3:// TYPE_NETWORK ：网络
                        break;
                    case 4:// TYPE_RAM_DISK ：闪存
                        break;
                    case 5:// TYPE_CDROM ：光驱
                        break;
                    case 6:// TYPE_SWAP ：页面交换
                        break;
                }

            }

            changeful.put("totalDiskGB",total);
            changeful.put("diskUsagePercent",numberFormat.format(100-avail*100D/total));

            return getSuccResponse(changeful);
        } catch (Exception e) {
            String msg="error happened when collection sys info:"+e.getMessage()+"\n";
            logger.error(msg,e);
            return getResponse(500,msg,null);
        }
    }

    public void close(){
        if(sigar!=null) {
            sigar.close();
            logger.info("sigar resource released");
        }

    }






    // d)获取网络流量等信息
    // Address = 10.35.33.204
    // Netmask = 255.255.255.0
    // RxBytes = 30594886
    // TxBytes = 18858778
   /* public void testNetIfList(SystemInfoBean systemInfoBean) {
        try {
            // System.out.println("\n~~~~~~~~~~获取网络流量信息 ~~~~~~~~~~");
            Sigar sigar = new Sigar();
            String ifNames[] = sigar.getNetInterfaceList();
            String RxMB = null;
            String TxMB = null;
            for (int i = 0; i < ifNames.length; i++) {
                // System.out.println("\n~~~~~~~~~~" + i + "~~~~~~~~~~");
                String name = ifNames[i];
                NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                // print("\nname = " + name);// 网络设备名
                // print("Address = " + ifconfig.getAddress());// IP地址
                // print("Netmask = " + ifconfig.getNetmask());// 子网掩码
                if ((ifconfig.getFlags() & 1L) <= 0L) {
                    // print("!IFF_UP...skipping getNetInterfaceStat");
                    continue;
                }
                try {
                    NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                    if (0.0 != ifstat.getRxBytes()
                            && 0.0 != ifstat.getTxBytes()) {
                        RxMB = ifstat.getRxBytes() / 1024L / 1024L + "M";// 接收到的总字节数
                        TxMB = ifstat.getTxBytes() / 1024L / 1024L + "M";// 发送的总字节数
                        break;
                    }
                } catch (SigarNotImplementedException e) {
                } catch (SigarException e) {
                    System.out.println(e.getMessage());
                }
            }
            log.info("接收到的流量 = " + RxMB);// 接收到的总字节数
            log.info("发送的流量= " + TxMB);// 发送的总字节数
            systemInfoBean.setI(RxMB);
            systemInfoBean.setO(TxMB);
        } catch (Exception e) {

        }
    }

    // e)一些其他的信息
    public void getEthernetInfo(SystemInfoBean systemInfoBean) {
        Sigar sigar = null;
        try {
            sigar = new Sigar();
            String[] ifaces = sigar.getNetInterfaceList();
            for (int i = 0; i < ifaces.length; i++) {
                NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
                if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress())
                        || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                        || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                    continue;
                }
                System.out.println("cfg.getAddress() = " + cfg.getAddress());// IP地址
                System.out
                        .println("cfg.getBroadcast() = " + cfg.getBroadcast());// 网关广播地址
                System.out.println("cfg.getHwaddr() = " + cfg.getHwaddr());// 网卡MAC地址
                System.out.println("cfg.getNetmask() = " + cfg.getNetmask());// 子网掩码
                System.out.println("cfg.getDescription() = "
                        + cfg.getDescription());// 网卡描述信息
                System.out.println("cfg.getType() = " + cfg.getType());//
                System.out.println("cfg.getDestination() = "
                        + cfg.getDestination());
                System.out.println("cfg.getFlags() = " + cfg.getFlags());//
                System.out.println("cfg.getMetric() = " + cfg.getMetric());
                System.out.println("cfg.getMtu() = " + cfg.getMtu());
                System.out.println("cfg.getName() = " + cfg.getName());
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error while creating GUID" + e);
        } finally {
            if (sigar != null)
                sigar.close();
        }
    }*/

    public static void main(String[] args) {
        SigarlSysInfoMonitor sb=new SigarlSysInfoMonitor();
        System.out.println("--------final--------------");
        System.out.println(sb.getChangefulInfo());
    }
}
