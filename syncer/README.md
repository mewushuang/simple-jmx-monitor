#简单的jdbc同步工具
从一条sql向目标表同步数据，需保证前者的columns是后者的子集，
且未被前者包含的column不应有非空约束
##usage
在application.yaml中配置源库和目标库的连接信息,以及同步任务
##point:
1. spring boot 配置类书写，yaml中使用对象数组。
2. quartz以及集成spring boot