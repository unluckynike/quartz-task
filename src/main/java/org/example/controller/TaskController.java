package org.example.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
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

    /**
     * 查看数据库任务 查到的是数据库中的任务
     * 请求地址：127.0.0.1:8080/tasks
     */
    @ApiOperation(value="查看数据库任务", notes="查到的是存在数据库中的任务",hidden=false)
    @GetMapping()
    public List<Task> queryTask() {
        List<Task> allTasks = taskDataService.getAllTasks();
        //吐出数据
        for (Task t : allTasks)
            System.out.println(t);

        return allTasks;
    }

    /**
     * 查看内存任务 查看内存中的任务
     * 请求地址: 127.0.0.1:8080/memory
     *
     * @throws SchedulerException
     */
    @ApiOperation(value="查看内存任务", notes="查看到的是当前内存中的任务")
    @GetMapping("/memory")
    public void queryTaskInMemory() throws SchedulerException {
        taskService.queryTaskInMemory();
    }

    /**
     * 创建并开启任务 自动触发 传入任务信息
     *
     * 请求地址: 127.0.0.1:8080/tasks
     * 请求参数 json数据：
     * {
     * "taskName": "My Task",
     * "cronExpression": "0/10 * * * * ?"  //每10秒执行一次
     * }
     *
     * @param task
     * @throws SchedulerException
     */
    @ApiOperation(value="创建并开启任务", notes="传入任务对象 任务自动触发 任务信息存入数据库 存入内存")
    @PostMapping
    public void createTask(@RequestBody Task task) throws SchedulerException {
        logger.info(" taskName: " + task.getTaskName());
        boolean successAdd = taskDataService.addTask(task);

        if (successAdd) {
            //执行的时候 查到最后一条（也就是最新添加的）任务执行
            taskService.createTask(taskDataService.getLastTask());
        }

    }

//    @ApiOperation(value="创建任务", notes="传入任务对象 任务不触发 任务信息存入数据库 存入内存")
//    @PostMapping

    /**
     * 暂停任务 传入任务id
     * 请求地址：127.0.0.1:8080/tasks/1/pause
     * 请求参数：taskId
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value="暂停任务", notes="传入任务id 通过id暂停内存中的任务 任务留在内存中")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PostMapping("/{taskId}/pause")
    public void pauseTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("暂停任务 id：" + taskId);
        taskService.pauseTask(taskId);
    }

    /**
     * 启动任务 传入任务id
     * 请求地址：127.0.0.1:8080/tasks/1/resume
     * 请求参数：taskId
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value="启动任务", notes="传入任务id 通过id重新启动内存中的任务")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PostMapping("/{taskId}/resume")
    public void resumeTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("重启任务 id：" + taskId);
        taskService.resumeTask(taskId);
    }

    /**
     * 删除任务 传入任务id
     * 请求地址: http://localhost:8080/tasks/1
     * 请求参数：taskId
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value="删除任务", notes="传入任务id 通过id删除内存中的任务 任务在内存中清空 数据库中删除数据")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @DeleteMapping("/{taskId}")
    public void deleteTask(@PathVariable("taskId") Integer taskId) throws SchedulerException {
        logger.info("删除任务 id：" + taskId);
        taskDataService.deleteTask(taskId);
        taskService.deleteTask(taskId);
    }

    /**
     * 修改任务 修改taskName cron表达式
     * 请求地址：http://localhost:8080/tasks/6
     * 请求参数：jsons数据
     * {
     *     "taskName": "每月最后一天23点执行一次",
     *     "cronExpression": "0 0 000 23 L * ?"
     * }
     *
     * @throws SchedulerException
     */
    @ApiOperation(value="修改任务", notes="传入任务id和taks对象， 修改该id下的taskName cron表达式 ")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PutMapping("/{taskId}")
    public void rescheduleTask(@PathVariable Integer taskId,
                               @RequestBody Task task) throws SchedulerException {
        logger.debug("修改任务 id：{} newTaskName: {} cron: {}", taskId, task.getTaskName(), task.getCronExpression());

        Task newTask = new Task(taskId, task.getTaskName(), task.getCronExpression());
        //更新数据库中的任务
        boolean successUpdate = taskDataService.updateTask(newTask);

        //成功更新 重启任务
        if (successUpdate) {
            taskService.rescheduleTask(taskId, task.getCronExpression());
        }

    }
}
