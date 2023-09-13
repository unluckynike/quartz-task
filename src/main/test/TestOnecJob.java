/*
 * @Package PACKAGE_NAME
 * @Author hailin
 * @Date 2023/9/12
 * @Description : 测试单次任务
 */

import org.example.controller.TaskController;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

public class TestOnecJob {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 创建一个JobDetail实例，并与任务类关联
        JobDetail jobDetail = JobBuilder
                .newJob(MyJob.class)
                .withIdentity("onceJob", "group1")
                .build();

        //执行时间
        ZonedDateTime dateTime = ZonedDateTime.parse("2023-09-12T11:05:00+08:00"); // 这里指定你的日期和时间，使用中国时间（UTC+8）

        // 创建一个Trigger实例，指定任务在特定时间执行一次
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("onceTrigger", "group1")
                .startAt(Date.from(dateTime.toInstant()))
                //这里设置任务的重复频率，如果不需要重复，可以改为.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).withRepeatCount(1))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever())
                .build();

        // 创建一个Scheduler实例
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        // 将JobDetail和Trigger关联起来，然后加入到Scheduler中
        scheduler.scheduleJob(jobDetail, trigger);


//        System.out.println("开始睡眠");
//        Thread.sleep(60000); // 睡眠一分钟
//        System.out.println("睡眠结束 查一下状态");

// 查询当前内存任务状态
        List<JobExecutionContext> jobExecutionContexts = scheduler.getCurrentlyExecutingJobs();
        for (JobExecutionContext jobExecutionContext : jobExecutionContexts) {
            JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
            JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            Trigger trigger2 = jobExecutionContext.getTrigger();
            String triggerKey = trigger2.getKey().getName();
            String triggerGroup = trigger2.getKey().getGroup();
//            String triggerState = trigger2.getTriggerState().name();
            String prevFireTime = new Date(String.valueOf(trigger2.getPreviousFireTime())).toString();
            String nextFireTime = new Date(String.valueOf(trigger2.getNextFireTime())).toString();
            String jobClassName = jobDataMap.getString("jobClassName");

            System.out.println("Job Key: " + jobKey);
            System.out.println("Job Data: " + jobDataMap);
            System.out.println("Trigger Key: " + triggerKey);
            System.out.println("Trigger Group: " + triggerGroup);
//            System.out.println("Trigger State: " + triggerState);
            System.out.println("Previous Fire Time: " + prevFireTime);
            System.out.println("Next Fire Time: " + nextFireTime);
            System.out.println("Job Class Name: " + jobClassName);
            System.out.println("-----------------------");
        }

    }

}

