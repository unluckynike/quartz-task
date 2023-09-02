package org.example.controller;

import org.example.pojo.Task;
import org.example.service.TaskDataService;
import org.example.service.TaskService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    //操作任务
    @Autowired
    private TaskService taskService;

    //操作数据库
    @Autowired
    private TaskDataService taskDataService;

    /*
    查看任务 查到的是数据库中的任务
    127.0.0.1:8080/tasks
     */
    @GetMapping()
    public void queryTask() {
        List<Task> allTasks = taskDataService.getAllTasks();
        for (Task t : allTasks)
            System.out.println(t);
    }

    /*
    查看内存中的任务
    127.0.0.1:8080/memory
    */
    @GetMapping("/memory")
    public void queryTaskInMemory() throws SchedulerException {
       taskService.queryTaskInMemory();
    }

    /*
    创建并开启任务 自动触发 传入任务信息
    API请求: 127.0.0.1:8080/tasks
    {
    "taskName": "My Task",
    "cronExpression": "0/10 * * * * ?"  //每10秒执行一次
   }
     */
    @PostMapping
    public void createTask(@RequestBody Task task) throws SchedulerException {
        logger.info( " taskName: " + task.getTaskName());
        boolean successAdd = taskDataService.addTask(task);

        if (successAdd){
            ////执行的时候 查到最后一条（也就是最新添加的）任务执行
            taskService.createTask(taskDataService.getLastTask());
        }

    }

    /*
    暂停任务 传入任务id
    127.0.0.1:8080/tasks/1/pause
     */
    @PostMapping("/{taskId}/pause")
    public void pauseTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("暂停任务 id：" + taskId);
        taskService.pauseTask(taskId);
    }

    /*
    重启任务 传入任务id
    127.0.0.1:8080/tasks/1/resume
     */
    @PostMapping("/{taskId}/resume")
    public void resumeTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("重启任务 id：" + taskId);
        taskService.resumeTask(taskId);
    }

    /*
    删除任务 传入任务id
     API请求:  http://localhost:8080/tasks/1
     */
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable("taskId") Integer taskId) throws SchedulerException {
        logger.info("删除任务 id：" + taskId);
        taskDataService.deleteTask(taskId);
        taskService.deleteTask(taskId);
    }

    /*
    修改任务 修改taskName cron表达式
    http://localhost:8080/tasks/3?newTaskName=linlinTask&newCronExpression=0/10 * * * * ?
    Query设置参数
     */
    @PutMapping("/{taskId}")
    public void rescheduleTask(@PathVariable Integer taskId,
                               @RequestParam String newTaskName,
                               @RequestParam String newCronExpression) throws SchedulerException {
        logger.info("修改任务 id：" + taskId + " newTaskName: " + newTaskName + " cron: " + newCronExpression);
        Task new_task = new Task(taskId, newTaskName, newCronExpression);
        taskDataService.updateTask(new_task);
        taskService.rescheduleTask(taskId, newCronExpression);
    }


}
