# quartz-task

## 接口文档

swagger2 ： http://127.0.0.1:8080/swagger-ui.html

在线接口文档： http://121.37.188.176:8080/swagger-ui.html

## 项目模块信息

### 开发环境

- 操作系统：MacOS
- 开发工具：IntelliJ IDEA
- 版本管理：Git
- JAVA：JDK1.8
- Maven版本：Apache Maven 3.8
-

### 技术组成

- SpringBoot 2.0
- 数据库 Mysql 5.6
- 任务调度 Spring Quartz 2.3
- 接口文档 Swagger 2
- 日志 Logback
-

### 架构

❗️该图有待完善
![模块架构图](./info/img/20230909101804.png)

### 数据库表设计

`task`表设计

|       列名       |   数据类型   |  长度 |        默认值        |        备注         |
|:--------------:|:--------:|----:|:-----------------:|:-----------------:|
|     taskid     |   int    | 255 |       自动递增        |      任务ID 主键      |
|    taskName    | varchar  | 255 |       NULL        |       任务名称        |
| cronExpression | varchar  | 255 |       NULL        | Cron表达式 针对循环多轮任务  |
| timeExpression | datetime |     |       NULL        |  时间表达式 针对单次定点任务   |
|   createtime   | datetime |     | CURRENT_TIMESTAMP |      任务创建时间       |
|   updatetime   | datetime |     | CURRENT_TIMESTAMP | 更新时间 修改时根据当前时间戳更新 |

`task`建表语句

```sql
SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`
(
    `taskid`         int(255) NOT NULL AUTO_INCREMENT COMMENT '任务id 主键 自动递增',
    `taskName`       varchar(255) DEFAULT NULL COMMENT '任务名称\n',
    `cronExpression` varchar(255) DEFAULT NULL COMMENT 'Cron表达式 针对循环多轮任务',
    `timeExpression` datetime     DEFAULT NULL COMMENT '时间表达式 针对单次定点任务',
    `createtime`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
    `updatetime`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`taskid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET
FOREIGN_KEY_CHECKS = 1;
```

### 功能

📄**冬冬学长要的接口文档 要素：请求地址 请求方式 入参 出参**

## 表达式样例

### cron

    每隔5秒执行一次：*/5 * * * * ?

    每隔1分钟执行一次：0 */1 * * * ?

    每天23点执行一次：0 0 23 * * ?

    每天凌晨1点执行一次：0 0 1 * * ?

    每月1号凌晨1点执行一次：0 0 1 1 * ?

    每月最后一天23点执行一次：0 0 23 L * ?

    每周星期天凌晨1点实行一次：0 0 1 ? * L

    在26分、29分、33分执行一次：0 26,29,33 * * * ?

    每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
    
    0 0/5 * * *  在每小时的第0分钟和第5分钟执行任务。因此，该任务将在每小时的第0分钟、第5分钟和第10分钟执行三次。

    0 * 14 * * ? 在每天下午2点到2点59分每分钟执行一次

    0 0/5 14 * * ? 在每天下午2点到2点59分，每5分钟执行一次

    0 0/5 14,18 * * ? 在每天下午2点到2点59分和下午6点到6点59分，每5分钟执行一次

    0 0 12 * * ? 在每天中午12点执行一次

    0 15 10 ? * * 在每天上午10点15分执行一次

    0 45 23 ? * * 在每天晚上11点45分执行一次

### 时间

    2023-09-13 17:41:00
    
    2023-09-19 17:41:00

## 待完成

- task任务创建时间 更新时间 ✅
- 补上代码注释 ✅
- 增加cron表达式合法性校验（添加和修改的时候需要校验合法性）✅
- 集成swagger在线接口文档 ✅
- 完善api在线接口文档 ✅
- 查内存任务信息 （可优化拆分成详细信息查询 和 分别给出任务状态总数的任务统计信息）✅
- 单次定点时间任务执行（只跑一次任务就结束）✅
- 单次定点时间合法性校验 ✅
- 传入任务对象 任务不触发 任务信息存入数据库 存入内存或者 存入数据库不上内存 触发
- 从db中拿已有任务上内存触发❌（运行起来是一致的）
- 譬如 我只是需要修改db中的任务 不让他修改了就执行❌（同上）
- 增加三个字段  任务描述（备注） 任务类型  long text类型的code（前端富文本）

## 待解决

- 查内存任务 只能统计到cron循环任务，单次定点的时间任务查不到
- 删除任务 考虑一种需要补充的情况 删除的时候 db中有 但是他并不在内存中
- 修改任务 需要增加修改time表达式 ✅
- 修改任务 传的id不存在不报错


## 一些思考🤔️

    关于结构设计
        面向对象之策略模式

    关于任务状态
        单次定点时间任务是 在内存中执行完便直接删掉
        循环定时任务 COMPLETE（已完成）是针对循环次数的cron表达式莫🤔还没看到过这个状态

    关于单次执行任务 开启就放入内存 执行任务 跑完就从内存删除了 存入数据库有什么意义呢？（数据库表设计逻辑删除）

    关于数据库表 增加逻辑删除列？ 增加是否是单次任务、循环任务标识列 ？

    单次任务与循环任务 增删改可公用service层方法吗！？
        目前来看查内存吐信息是可以公用的😊

    关于controller接口返回值参数设计 
        前端也做成管理 分别可以查到数据库中的任务和内存的任务（两个管理页面）
        做成了管理 那么是否给出一些常用功能 譬如taskName 模糊搜索、分页查询、

    关于db里数据量
        专利数据量亿级；机构、论文都是千万级数据量；新闻数据量是百万级 
        已经建了 BTREE索引

**quartz 失火策略**

Quartz还提供了Misfire处理机制，以便在错过某个作业执行时间时进行处理，并保证作业能够在下一个适当的时间被执行。

Misfire：到了任务触发时间点，但是任务没有被触发。
