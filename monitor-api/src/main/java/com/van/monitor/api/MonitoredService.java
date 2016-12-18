package com.van.monitor.api;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 与监控工具约定的服务接口类。接口通过反射构建服务对象（使用无参构造），故其实现类必须包含一个逻辑完整的无参构造
 */
public interface MonitoredService {


    /**
     * 需要有防止用户连续重复点击的逻辑
     * 需要保证stop后所有线程可以结束，资源被释放
     *
     * 在服务需要启动一个以上的实例时使用参数，用来区分启动/关闭不同的实例
     *根据传入的参数获取一个简化的key，用于传递给启动/停止等方法，
     *在控制中心添加服务时需要填写该参数列表
     * @param key 用于
     */
    void stop(String[] key);

    /**
     * 需要有防止用户连续重复点击的逻辑
     * @param key
     */
    void start(String[] key);


    /**
     * 默认启动逻辑：在监控后台启动时被调用一次。
     * 使用预设的参数启动所有实例，如只有一个实例，可直接调用start方法
     */
    void startDefault(Executor threadPool);

    /**
     * 默认关闭逻辑：在监控后台关闭时被调用一次
     */
    void stopDefault();

    /**
     * 获取服务运行状态的Metric对象，每个服务里面必须有，如果返回空会导致异常
     * @param key
     * @return
     */
    RunningStatusMetric getRunningStatus(String[] key);

    /**
     * 获取其它自定义的Metric对象,没有返回null
     * @param key
     * @return
     */
    List<Metric> getExtraMetrics(String[] key);


}
