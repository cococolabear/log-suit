# 日志实时处理系统

### 1.项目结构

```
drwxr-xr-x  10 join  staff   340B  8  9 17:32 .
drwxr-xr-x   7 join  staff   238B  8  9 09:50 ..
-rw-r--r--   1 join  staff   1.4K  8  9 09:50 .classpath
-rw-r--r--   1 join  staff   537B  8  9 09:50 .project
drwxr-xr-x   5 join  staff   170B  8  9 09:53 .settings
-rw-r--r--   1 join  staff   4.9K  8  9 09:50 README.MD
-rw-r--r--   1 join  staff   5.8K  8  9 16:15 pom.xml
drwxr-xr-x   4 join  staff   136B  8  9 09:50 src
drwxr-xr-x   5 join  staff   170B  8  9 09:52 target

```

- `src` 是程序代码


### 2、注意事项：
- 弹窗日志指标计算公式  
1、`click_ip_count`：点击唯一ip数，以`channel_id,campaign_id`为key，缓存一天的ip数量来去重。  
2、`redirect_ip_count`：重定向唯一ip数，需要该行数据 `redirect_campaign_id > 0`，以`channel_id,campaign_id`为key，缓存一天的ip数量来去重

### 3.项目部署：
在项目的根目录`(README.MD所在文件夹)`处执行 `mvn package`，会在主程序目录下生成`target`文件夹

例如 ls -alh log-suit：

```
-rw-r--r--   1 join  staff    43B  8 9 17:56 .gitignore
-rw-r--r--   1 join  staff   5.7K  8 9 09:18 pom.xml
drwxr-xr-x   4 join  staff   136B  8 9 14:31 src
drwxr-xr-x   6 join  staff   204B  8 9 09:18 target
```

`target` 文件夹下会生成项目文件夹，例如：

```
drwxr-xr-x   8 join  staff   272B  8  9 18:23 .
drwxr-xr-x  11 join  staff   374B  8  9 18:23 ..
drwxr-xr-x   6 join  staff   204B  8  9 18:23 classes
drwxr-xr-x   3 join  staff   102B  8  9 18:23 generated-sources
drwxr-xr-x   5 join  staff   170B  8  9 18:23 log-suit-1.0.1
drwxr-xr-x   3 join  staff   102B  8  9 18:23 maven-archiver
drwxr-xr-x   3 join  staff   102B  8  9 18:23 maven-status
drwxr-xr-x   2 join  staff    68B  8  9 18:23 test-classes
```

`log-suit-1.0.1`就是运行程序，典型的**tomcat**结构：

```
drwxr-xr-x   5 join  staff   170B  2 22 09:19 bin
drwxr-xr-x   4 join  staff   136B  2 22 09:18 conf
drwxr-xr-x  53 join  staff   1.8K  2 22 16:50 lib
```

`logs`目录会在程序启动后，自动生成。  
只需要把**这个文件夹所有的文件**全部拷贝到服务器上，就能运行了，不需要上层目录的文件，
比如现在就能运行此程序。

### 4.程序启动：
`conf`目录存放程序需要的配置文件

`lib` 目录存放程序需要的jar包

`bin`目录下有三个脚本：

- `setclasspath.sh` 自动适配java环境变量的
- `startup.sh` 程序启动脚本，直接 `sh startup.sh`就能启动了
- `shutdown.sh` 程序的停止脚本，直接 `sh shutdown.sh`就能停止程序

**＊注意：** 当前程序的关闭方式是通过文件夹关键字来关闭的，**没有pid文件**，所以不能一
个文件夹运行多个实例。

### 5、程序配置：
```
# 统计结果数据库的配置
spring.datasource.url=jdbc:mysql://host:3306/test
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

```
# kafka的配置
application.kafka.zookeeper=zk1:2181,zk2:2182,zk3:2183/kafka
application.kafka.topic=test
application.kafka.batch-size=100
application.kafka.group=group
```


```
# 程序监控的配置,使用方式：curl http://localhost:26860/metrics
application.monitor.port=26860
```


```
# rocksdb的路径配置
application.rocksdb.path=/tmp/rocksdb
```
