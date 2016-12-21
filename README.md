# simple-jmx-monitor
this is a simple jmx monitor , supplys some basic function like resource monitor, logview and service
starting/stopping.
#使用说明：
###部署
将你的服务打包，入口类需要实现*com.van.monitor.api.MonitoredService*接口。
将打好的jar包放到lib目录下，并在conf/monitior.properties文件中将入口类
的类路径配置上即可。你可以将你的服务依赖的jar包一同拷贝至lib目录下，
但要注意jar包冲突：lib目录下已经包含了jackson、slf4j/log4j和sigar。
###配置
你可以修改conf/log4j.xml来适应你的应用。
按照配置文件中的说明完成objectName的设置，
你也可以空置使用默认配置，但不推荐这种做法。
此处配置的objectName应与数据库中对应的objectName一致。
###其它
你可以将你的配置文件放到conf文件夹下，然后如下在代码中使用：
```java
config = new Properties();
String path=System.getProperty("application.confDir");
config.load(new InputStreamReader(new FileInputStream(path+"monitor.properties"), Charset.forName("utf-8")));
```
