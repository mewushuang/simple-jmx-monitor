# simple-jmx-monitor
this is client side of a simple jmx monitor , [here is the server side](https://github.com/mewushuang/monitor-center).

## 功能
* 目的主要是为了应对国网体制内复杂冗长的检修流程
* 查看服务状态，控制服务起停
* 查看日志，提供下载、删除、检索
* 上传并更新程序包

## 使用
1. 基于本项目开发, 参考已有的模块synchroniser.
2. 使用已开发好的jar包:将你的服务打包，入口类需要实现*com.van.monitor.api.MonitoredService*接口。下载完成后在项目根目录下运行gradlew distZip,该命令会在会在monitor-impl/build/distributions目录下生成打好的zip包,解压后将已开发好的服务jar包放到lib目录下，并在conf/monitior.properties文件中将入口类的类路径配置上即可。你可以将你的服务依赖的jar包一同拷贝至lib目录下，但要注意jar包冲突：lib目录下已经包含了jackson、slf4j/logback和sigar。

## 配置
* 你可以修改conf/logback.xml来适应你的应用。
* 修改conf/monitor.properties中的url为本机,objectName为一个可以保证不重复的名字,如该服务的包名.此处配置的objectName应与monitor_center前端新建服务时填写的objectName一致。

## 其它
* 脚本中传递了程序主路径作为虚拟机参数, 可以在程序中引用.
* 仔细编写MonitoredService的实现类,确保获取到的状态与运行状态一致,确保stop时可以释放所有资源关闭所有线程
