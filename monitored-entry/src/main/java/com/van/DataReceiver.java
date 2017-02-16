package com.van;

import com.van.common.SeatRecorder;
import com.van.monitor.api.Metric;
import com.van.monitor.api.MonitoredService;
import com.van.monitor.api.RunningStatusMetric;
import com.van.transfer.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by van on 2016/12/12.
 */
public class DataReceiver implements MonitoredService {
    public static final String SEAT_ARG = "seat";
    public static final String RT_ARG = "rt";

    private final Logger logger = LoggerFactory.getLogger(DataReceiver.class);


    private Client seat = null;
    private Client rt = null;


    @Override
    public void stop(String[] strings) {
        checkParams(strings);
        if (isSeat(strings)) {
            if(seat!=null) seat.stop();
        } else if (isRt(strings)) {
            if(rt!=null) rt.stop();
        } else {
            checkParam(strings[0]);
        }
    }

    @Override
    public void start(String[] strings) {
        checkParams(strings);
        if (isSeat(strings)) {
            logger.info("received start command for "+SEAT_ARG);
            seat = new Client(Client.SEAT_MODULE);
            seat.startHighAvailable();
        } else if (isRt(strings)) {
            logger.info("received start command for "+RT_ARG);
            rt = new Client(Client.RT_MODULE);
            rt.startHighAvailable();
        } else {
            checkParam(strings[0]);
        }
    }
    private boolean isSeat(String[] args){
        for(int i=0;i<args.length;i++){
            if(SEAT_ARG.equalsIgnoreCase(args[i])){
                return true;
            }
        }
        return false;
    }
    private boolean isRt(String[] args){
        for(int i=0;i<args.length;i++){
            if(RT_ARG.equalsIgnoreCase(args[i])){
                return true;
            }
        }
        return false;
    }
    private void checkParam(String string) {
        logger.error("wrong param:" + string + ", expect " + SEAT_ARG + " or " + RT_ARG);
        throw new IllegalArgumentException("wrong param:" + string + ", expect " + SEAT_ARG + " or " + RT_ARG);
    }

    private void checkParams(String[] strings) {
        if (strings == null || strings.length < 1) {
            logger.error("empty params on method start");
            throw new IllegalArgumentException("please input correct start param");
        }
    }

    @Override
    public void startDefault(Executor threadPool) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                DataReceiver.this.start(new String[]{DataReceiver.this.RT_ARG});
            }
        });
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                DataReceiver.this.start(new String[]{DataReceiver.this.SEAT_ARG});
            }
        });
    }

    @Override
    public void stopDefault() {
        stop(new String[]{SEAT_ARG});
        stop(new String[]{RT_ARG});
    }

    @Override
    public RunningStatusMetric getRunningStatus(String[] strings) {
        checkParams(strings);
        if (isSeat(strings)) {
            return new RunningStatusMetric(seat==null? RunningStatusMetric.RunningStatus.stopped:seat.getRunningStatus());
        } else if (isRt(strings)) {
            return new RunningStatusMetric(rt==null? RunningStatusMetric.RunningStatus.stopped:rt.getRunningStatus());
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
        final  DataReceiver d=new DataReceiver();
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        d.startDefault(threadPool);

        /*new Thread(){
            @Override
            public void run() {
                d.start(new String[]{d.SEAT_ARG});
            }
        }.start();
        d.start(new String[]{d.RT_ARG});*/
        /*Thread.sleep(20000);
        d.stopDefault();
        threadPool.shutdown();*/
    }
}
