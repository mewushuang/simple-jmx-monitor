package com.van;

import com.van.common.ScodeEntity;
import com.van.entry.Client;
import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.api.RunningStatusMetric;
import com.van.service.ParseService;
import com.van.service.ScodeTimeRecorderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by van on 2016/12/12.
 */
public class DataReceiver implements MonitoredService {

    private final Logger logger = LoggerFactory.getLogger(DataReceiver.class);


    private ApplicationContext applicationContext;
    private final String conf = "--spring.config.location=";

    private EntryContext context;



    @Override
    public void stop(String[] strings) {
        checkParams(strings);
        if (isSeat(strings)) {
            context.getSeat().stop();
            logger.info("client["+Client.SEAT_MODULE+"] is stopped");
        } else if (isRt(strings)) {
            context.getRt().stop();
            logger.info("client["+Client.RT_MODULE+"] is stopped");
        } else {
            checkParam(strings[0]);
        }
    }

    @Override
    public void start(String[] strings) {
        checkParams(strings);
        if (isSeat(strings)) {
            logger.info("received start command for " + Client.SEAT_MODULE);
            context.getSeat().startHighAvailable();
        } else if (isRt(strings)) {
            logger.info("received start command for " + Client.RT_MODULE);
            context.getRt().startHighAvailable();
        } else {
            checkParam(strings[0]);
        }
    }

    private boolean isSeat(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (Client.SEAT_MODULE.equalsIgnoreCase(args[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isRt(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (Client.RT_MODULE.equalsIgnoreCase(args[i])) {
                return true;
            }
        }
        return false;
    }

    private void checkParam(String string) {
        logger.error("wrong param:" + string + ", expect " + Client.SEAT_MODULE + " or " + Client.RT_MODULE);
        throw new IllegalArgumentException("wrong param:" + string + ", expect " + Client.SEAT_MODULE + " or " + Client.RT_MODULE);
    }

    private void checkParams(String[] strings) {
        if (strings == null || strings.length < 1) {
            logger.error("empty params on method start");
            throw new IllegalArgumentException("please input correct start param");
        }
    }

    @Override
    public void startDefault(Executor threadPool) {

        String[] strings = addSpringBootArg(new String[]{});
        //正式环境有日志系统，此处移除了springboot自带的日志系统
        this.applicationContext = SpringApp.run(strings);
        this.context = applicationContext.getBean(EntryContext.class);

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                DataReceiver.this.start(new String[]{Client.SEAT_MODULE});
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                DataReceiver.this.start(new String[]{Client.RT_MODULE});
            }
        });
    }

    @Override
    public void stopDefault() {
        stop(new String[]{Client.SEAT_MODULE});
        stop(new String[]{Client.RT_MODULE});
    }

    @Override
    public RunningStatusMetric getRunningStatus(String[] strings) {
        checkParams(strings);
        if (isSeat(strings)) {
            return new RunningStatusMetric(context.getSeat().getRunningStatus());
        } else if (isRt(strings)) {
            return new RunningStatusMetric(context.getRt().getRunningStatus());
        } else {
            checkParam(strings[0]);
            return null;
        }
    }

    @Override
    public List<Metric> getExtraMetrics(String[] strings) {
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        final DataReceiver d = new DataReceiver();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        d.startDefault(threadPool);

        //测试
        /*DataReceiver d = new DataReceiver();
        String[] strings = d.addSpringBootArg(new String[]{});
        d.applicationContext = SpringApp.run(strings);
        ParseService p=d.applicationContext.getBean(ParseService.class);
        ScodeTimeRecorderService s=d.applicationContext.getBean(ScodeTimeRecorderService.class);
        String v="{\"time\":\"2016-02-22 15:31:29\",\"data\":[{\"dim\":\"dn\",\"v\":\"99.98\",\"tt\":\"02\",\"code\":\"711010209\"},{\"dim\":\"dn\",\"v\":\"99.98\",\"tt\":\"03\",\"code\":\"711010209\"}]}";
        ScodeEntity entity=p.parseRawMsgOfRt("P02_02189", v);
        s.recordTime(entity);*/
    }

    private String[] addSpringBootArg(String[] args) {
        int len = 0;
        if (args != null) len = args.length;
        String[] ret = new String[len + 1];
        System.arraycopy(args, 0, ret, 0, args.length);
        ret[len] = conf + Paths.get(System.getProperty("user.dir"), "conf","application.yaml").toAbsolutePath().toString();
        logger.info(ret[len]);
        return ret;
    }
}
