# simple-jmx-monitor
this is client side of a simple jmx monitor , [here is the server side](https://github.com/mewushuang/monitor-center).

##功能
* 查看服务状态，控制服务起停
* 查看日志，提供下载、删除、检索
* 上传并更新程序包

##部署
将你的服务打包，入口类需要实现*com.van.monitor.api.MonitoredService*接口。
将打好的jar包放到lib目录下，并在conf/monitior.properties文件中将入口类
的类路径配置上即可。你可以将你的服务依赖的jar包一同拷贝至lib目录下，
但要注意jar包冲突：lib目录下已经包含了jackson、slf4j/logback和sigar。
##配置
你可以修改conf/logback.xml来适应你的应用。
按照配置文件中的说明完成objectName的设置，
你也可以空置使用默认配置，但不推荐这种做法。
此处配置的objectName应与monitor_center数据库中对应的objectName一致。
##其它
* 脚本中传递了程序主路径作为虚拟机参数, 可以在程序中引用.
* 仔细编写MonitoredService的实现类,确保获取到的状态与运行状态一致,确保stop时可以释放所有资源关闭所有线程
