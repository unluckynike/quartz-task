package org.example.service;

import org.example.controller.TaskController;
import org.example.job.SampleJob;
import org.example.pojo.Task;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/*
 * @Package org.example.service
 * @Author hailin
 * @Date 2023/8/11
 * @Description : 任务服务类
 */

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private Scheduler scheduler;


    /**
     * 创建多次循环任务
     *
     * @param task
     * @throws SchedulerException
     */
    public void createLoopTask(Task task) throws SchedulerException {
        //创建任务详情
        JobDetail jobDetail = JobBuilder //可以加description 用remake值
                .newJob(SampleJob.class)
                .withIdentity(task.getTaskId().toString())//给的是id 因为考虑到任务名字可能重复
                .usingJobData("type", task.getType().name())
                .usingJobData("remark", task.getRemark())
                .usingJobData("codeScript", task.getCodeScript()) // 往JobData里面放内容
                .build();
        //创建触发器
        CronTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(task.getTaskId() + "Trigger") //给的是id 因为考虑到任务名字可能重复   ?? 试试 + task.getTaskName()
                //得到cron表达式
                .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression())).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 暂停任务 传入taskId 暂停任务 任务留在内存中
     *
     * @param taskId
     * @throws SchedulerException
     */
    public boolean pauseTask(Integer taskId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        if (triggerKey == null) {
            return false;
        }
        if (scheduler.getTrigger(triggerKey) == null) {
            return false;
        }
        scheduler.pauseTrigger(triggerKey);
        return true;
    }

    /**
     * 重启任务 传入taskId 重启内存中的任务
     *
     * @param taskId
     * @throws SchedulerException
     */
    public boolean resumeTask(Integer taskId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        if (triggerKey == null) {
            return false;
        }
        if (scheduler.getTrigger(triggerKey) == null) {
            return false;
        }
        scheduler.resumeTrigger(triggerKey);
        return true;
    }

    /**
     * 删除任务 传入taskId 删除内存中的任务
     *
     * @param taskId
     * @throws SchedulerException
     */
    public boolean deleteTask(Integer taskId) throws SchedulerException {
        boolean isDelete = scheduler.deleteJob(JobKey.jobKey(String.valueOf(taskId)));
        return isDelete;
    }

    /**
     * 修改任务的cron 传入taskId
     *
     * @param taskId
     * @param newCronExpression
     * @throws SchedulerException
     */
    public void rescheduleCronTask(Integer taskId, String newCronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        //构建一个新的触发器，使用新的Cron表达式
        Trigger newTrigger = TriggerBuilder.newTrigger()// 设置触发器的唯一标识
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                .build();
        // 调度器重新调度任务，使用新的触发器来替换原有的触发器
        try {
            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    /**
     * 修改任务的time 传入taskId
     *
     * @param taskId
     * @param newTimeExpression
     */
    public void rescheduleOnceTask(Integer taskId, Date newTimeExpression) {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        Trigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(newTimeExpression) // 设置任务的开始时间为新的时间表达式
                .build();
        try {
            scheduler.rescheduleJob(triggerKey, newTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前内存中的所有任务
     *
     * @throws SchedulerException
     */
    public List<String> queryTaskInMemory() throws SchedulerException {
        // 获取所有 TriggerKey
        List<String> groupNames = scheduler.getTriggerGroupNames();

        //存储任务列表
        List<String> tasklist = new ArrayList<String>();

//        logger.info("任务状态： NORMAL（正常）、PAUSED（暂停）、COMPLETE（已完成）");
        for (String groupName : groupNames) {
            GroupMatcher<TriggerKey> matcher = GroupMatcher.triggerGroupEquals(groupName);
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(matcher);

            for (TriggerKey triggerKey : triggerKeys) {
                Trigger trigger = scheduler.getTrigger(triggerKey);

                // 获取 Trigger 的状态
                Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                JobKey jobKey = trigger.getJobKey();

                // 获取 JobDetail
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                // 获取 JobDetail 的 JobDataMap
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                // 获取任务关联的数据
                String type = jobDataMap.getString("type");
                String remark = jobDataMap.getString("remark");


                // 吐出信息 NORMAL（正常）、PAUSED（暂停）、COMPLETE（已完成） 考虑这里与db交互查一次？
                tasklist.add("任务id:" + jobKey.getName() + " 任务类型:" + type + " 任务在内置Group任务组:" + jobKey.getGroup() + " 任务状态:" + triggerState + " 任务描述:" + remark);
            }
        }

        //内存中没有任务
        if (tasklist.size() == 0) {
            logger.info("当前内存中暂无任务");
        }
        return tasklist;

    }

    /**
     * 获取内存任务状态
     *
     * @return
     * @throws SchedulerException
     */
    public Map<Trigger.TriggerState, Integer> queryTaskStateInMemory() throws SchedulerException {
        // 获取所有 TriggerKey
        List<String> groupNames = scheduler.getTriggerGroupNames();
        // 创建一个 Map 来存储任务状态和数量
        Map<Trigger.TriggerState, Integer> statusCountMap = new HashMap<>();

        logger.info("任务状态： NORMAL（正常）、PAUSED（暂停）、COMPLETE（已完成）");
        for (String groupName : groupNames) {
            GroupMatcher<TriggerKey> matcher = GroupMatcher.triggerGroupEquals(groupName);
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(matcher);

            for (TriggerKey triggerKey : triggerKeys) {
                // 获取 Trigger 的状态
                Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);

                // 将状态和数量存入 Map
                statusCountMap.put(triggerState, statusCountMap.getOrDefault(triggerState, 0) + 1);
            }
        }

        // 吐出信息 打印内存任务状态 统计信息
        for (Map.Entry<Trigger.TriggerState, Integer> entry : statusCountMap.entrySet()) {
            logger.info("任务状态 " + entry.getKey() + ": " + entry.getValue() + " 个任务");
        }

        //内存中没有任务
        if (statusCountMap.size() == 0) {
            logger.info("当前内存中暂无任务");
        }
        return statusCountMap;

    }

    //创建单次时间任务
    public void createOnceTimeTask(Task task) throws SchedulerException {
        // 创建一个JobDetail实例，并与任务类关联
        JobDetail jobDetail = JobBuilder
                .newJob(SampleJob.class)
                .withIdentity(task.getTaskId().toString())
                .usingJobData("type", task.getType().name())// 往JobData里面放内容
                .usingJobData("remark", task.getRemark())
                .usingJobData("codeScript", task.getCodeScript())
                .build();

        // 创建一个Trigger实例，指定任务在特定时间执行一次
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(task.getTaskId() + "Trigger")
                .startAt(Date.from(task.getTimeExpression().toInstant())) //得到执行时间点
                //这里设置任务的重复频率，如果不需要重复，改为.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).withRepeatCount(1))
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever())
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withRepeatCount(0)//设置重复次数为0，表示只触发一次
                ).build();

        // 将JobDetail和Trigger关联起来，然后加入到Scheduler中
        scheduler.scheduleJob(jobDetail, trigger);
    }


}
