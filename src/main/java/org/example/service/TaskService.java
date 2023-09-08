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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TaskDataService taskDataService;

    /**
     * 创建任务并调度执行（自动触发）
     *
     * @param task
     * @throws SchedulerException
     */
    public void createTask(Task task) throws SchedulerException {
        //创建任务详情
        JobDetail jobDetail = JobBuilder
                .newJob(SampleJob.class)
                .withIdentity(task.getTaskId().toString())//给的是id 因为考虑到任务名字可能重复
                .build();
        //创建触发器
        CronTrigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(task.getTaskId() + "Trigger") //给的是id 因为考虑到任务名字可能重复
                //得到cron表达式
                .withSchedule(CronScheduleBuilder.cronSchedule(task.getCronExpression()))
                .build();
        //task 的id name cron 存入数据库  ？？耦合高？？
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 执行在内存中暂停的任务 （手动触发） 任务已经在内存中
     *
     * @param taskId
     * @throws SchedulerException
     */
    public void executeTask(Integer taskId) throws SchedulerException {
        // 根据任务的ID获取任务的JobKey
        JobKey jobKey = new JobKey(taskId.toString());//给的是id 因为考虑到任务名字可能重复
        // 触发任务执行
        scheduler.triggerJob(jobKey);
    }

    /**
     * 暂停任务 传入taskId 暂停任务留在内存中
     *
     * @param taskId
     * @throws SchedulerException
     */
    public void pauseTask(Integer taskId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        scheduler.pauseTrigger(triggerKey);
    }

    /**
     * 重启任务 传入taskId 重启内存中的任务
     *
     * @param taskId
     * @throws SchedulerException
     */
    public void resumeTask(Integer taskId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        scheduler.resumeTrigger(triggerKey);
    }

    /**
     * 删除任务 传入taskId 删除内存中的任务
     *
     * @param taskId
     * @throws SchedulerException
     */
    public void deleteTask(Integer taskId) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(String.valueOf(taskId)));
    }

    /**
     * 修改任务的cron 传入taskId
     *
     * @param taskId
     * @param newCronExpression
     * @throws SchedulerException
     */
    public void rescheduleTask(Integer taskId, String newCronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId + "Trigger");
        //构建一个新的触发器，使用新的Cron表达式
        Trigger newTrigger = TriggerBuilder
                .newTrigger()// 设置触发器的唯一标识
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                .build();
        // 调度器重新调度任务，使用新的触发器来替换原有的触发器
        scheduler.rescheduleJob(triggerKey, newTrigger);
    }


    /**
     * 获取当前内存中的所有任务 得到任务状态
     *
     * @throws SchedulerException
     */
    public void queryTaskInMemory() throws SchedulerException {
        // 获取所有 TriggerKey
        List<String> groupNames = scheduler.getTriggerGroupNames();
        // 创建一个 Map 来存储任务状态和数量
        Map<Trigger.TriggerState, Integer> statusCountMap = new HashMap<>();

        for (String groupName : groupNames) {
            GroupMatcher<TriggerKey> matcher = GroupMatcher.triggerGroupEquals(groupName);
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(matcher);

            for (TriggerKey triggerKey : triggerKeys) {
                Trigger trigger = scheduler.getTrigger(triggerKey);

                // 获取 Trigger 的状态
                Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                JobKey jobKey = trigger.getJobKey();

                // 将状态和数量存入 Map
                statusCountMap.put(triggerState, statusCountMap.getOrDefault(triggerState, 0) + 1);

                // 吐出信息 NORMAL（正常）、PAUSED（暂停）、COMPLETE（已完成）
               logger.info("任务id: " + jobKey.getName() + " 在内置Group任务组 " + jobKey.getGroup() +
                        " 的状态是 " + triggerState);
            }
        }

        // 吐出信息 打印任务状态统计信息
        for (Map.Entry<Trigger.TriggerState, Integer> entry : statusCountMap.entrySet()){
            logger.info("状态 " + entry.getKey() + ": " + entry.getValue() + " 个任务");
        }

        //内存中没有任务
        if(statusCountMap.size()==0){
            logger.info("当前内存中暂无任务");
        }
    }



}
