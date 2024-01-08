package org.example.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.example.pojo.Task;
import org.example.service.TaskDataService;
import org.example.service.TaskService;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/*
 * @Package org.example.controller
 * @Author hailin
 * @Date 2023/8/11
 * @Description : 任务调度接口
 */

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    //操作任务
    @Autowired
    private TaskService taskService;

    //操作数据库任务信息
    @Autowired
    private TaskDataService taskDataService;


    /**
     * 查看数据库任务 查到的是数据库中的任务
     * @return
     */
    @ApiOperation(value = "查看数据库任务", notes = "查到的是存在数据库中的任务", hidden = false)
    @GetMapping()
    public Map<String, Object> queryTask() {
        Map<String, Object> returnMap = new HashMap<>();  //返回参数

        List<Task> allTasks = taskDataService.getAllTasks();
        //吐出数据
        for (Task t : allTasks)
            System.out.println(t);

        //处理返回参数
        returnMap.put("code", 1);
        returnMap.put("msg", "数据库任务获取失败");
        returnMap.put("count", 0);
        returnMap.put("data", "");
        if (allTasks.size() > 0) {
            returnMap.put("code", 0);
            returnMap.put("count", allTasks.size());
            returnMap.put("msg", "获取成功");
            returnMap.put("data", allTasks);
        }
        return returnMap;
    }

    /**
     * 查看内存任务 查看内存中的任务
     *
     * @throws SchedulerException
     */
    @ApiOperation(value = "查看内存任务", notes = "查看到的是当前内存中的任务")
    @GetMapping("/memory")
    public Map<String, Object> queryTaskInMemory() throws SchedulerException {

        Map<String, Object> returnMap = new HashMap<>();  //返回参数
        returnMap.put("code", 1);
        returnMap.put("msg", "查看内存任务失败");
        returnMap.put("count", 0);
        returnMap.put("data", "");

        List<String> taskList = taskService.queryTaskInMemory();
        if (taskList.size() == 0) {
            returnMap.put("code", 1);
            returnMap.put("msg", "查看内存任务 当前内存中任务数量为0");
            returnMap.put("count", 0);
            returnMap.put("data", "");
        } else {
            returnMap.put("code", 0);
            returnMap.put("msg", "成功查看内存任务");
            returnMap.put("count", taskList.size());
            returnMap.put("data", taskList);
        }

        return returnMap;
    }

    /**
     * 查看内存任务 查看内存任务状态
     *
     * @throws SchedulerException
     */
    @ApiOperation(value = "查看内存任务状态", notes = "查看到的是当前内存中任务的状态")
    @GetMapping("/memoryState")
    public Map<String, Object> queryTaskStateInMemory() throws SchedulerException {
        Map<String, Object> returnMap = new HashMap<>();  //返回参数
        returnMap.put("code", 1);
        returnMap.put("msg", "查看内存任务状态失败");
        returnMap.put("count", 0);
        returnMap.put("data", "");

        Map<Trigger.TriggerState, Integer> stateMap = taskService.queryTaskStateInMemory();
        if (stateMap.size() == 0) {
            returnMap.put("code", 1);
            returnMap.put("msg", "查看内存任务状态 当前内存中任务数量为0");
            returnMap.put("count", 0);
            returnMap.put("data", "");
        } else {
            returnMap.put("code", 0);
            returnMap.put("msg", "成功查看内存任务状态");
            returnMap.put("count", stateMap.size());
            returnMap.put("data", stateMap);
        }

        return returnMap;
    }

    /**
     * 创建开启多次循环任务
     *
     * @param task
     * @throws SchedulerException
     */
    @ApiOperation(value = "创建开启多次循环任务", notes = "传入cron任务对象 任务触发 任务信息存入数据库 存入内存")
    @PostMapping("/createLoopTask")
    public Map<String, Object> createCronTask(@RequestBody Task task) throws SchedulerException {
        logger.info("task infor: " + task.toString());

        Map<String, Object> returnMap = new HashMap<>();  //返回参数
        returnMap.put("status", 0);
        returnMap.put("desc", "创建并开启多次循环任务失败");

        boolean successAdd = taskDataService.addCronTask(task);

        if (successAdd) {
            //执行的时候 查到最新添加的任务执行
            Task lastTask = taskDataService.getLastTask();
            taskService.createLoopTask(lastTask);
            taskService.pauseTask(lastTask.getTaskId());
            taskDataService.codeScriptStatePause(lastTask.getTaskId());
            returnMap.put("status", 1);
            returnMap.put("desc", "成功创建并开启多次循环任务");
        }

        return returnMap;
    }

    /**
     * 创建单次时间任务
     *
     * @param task
     * @throws SchedulerException
     */
    @ApiOperation(value = "创建开启单次时间任务", notes = "传入任务对象 任务自动触发 任务信息存入数据库 存入内存")
    @PostMapping("/createOnceTimeTask")
    public Map<String, Object> createOnceTimeTask(@RequestBody Task task) throws SchedulerException {
        logger.info("task infor : " + task.toString());

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("status", 0);
        returnMap.put("desc", "创建单次定时任务并开启执行失败");
        //单次任务 加入数据库
        boolean successAdd = taskDataService.addOnceTimeTask(task);

        if (successAdd) {
            Task lastTask = taskDataService.getLastTask();
            taskService.createOnceTimeTask(lastTask);
            taskService.pauseTask(lastTask.getTaskId());
            taskDataService.codeScriptStatePause(lastTask.getTaskId());
            returnMap.put("status", 1);
            returnMap.put("desc", "成功创建单次定时任务并开启执行");
        }
        return returnMap;
    }


    /**
     * 暂停任务 传入任务id
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value = "暂停任务", notes = "传入任务id 通过id暂停内存中的任务 任务留在内存中")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PostMapping("/{taskId}/pause")
    public Map<String, Object> pauseTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("暂停任务 id：" + taskId);
        Map<String, Object> returnMap = new HashMap<>();  //返回参数

        boolean isPause = taskService.pauseTask(taskId);
        if (isPause) {
            taskDataService.codeScriptStatePause(taskId);
            returnMap.put("status", 1);
            returnMap.put("desc", "成功暂停任务");
        } else {
            returnMap.put("status", 0);
            returnMap.put("desc", "暂停任务失败");
        }
        return returnMap;
    }

    /**
     * 启动任务
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value = "启动任务 重启任务", notes = "传入任务id 通过id重新启动内存中的任务")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PostMapping("/{taskId}/resume")
    public Map<String, Object> resumeTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("重启任务 id：" + taskId);
        Map<String, Object> returnMap = new HashMap<>();  //返回参数

        boolean isResume = taskService.resumeTask(taskId);
        if (isResume) {
            taskDataService.codeScriptStateEnable(taskId);
            returnMap.put("status", 1);
            returnMap.put("desc", "成功重启任务");
        } else {
            returnMap.put("status", 0);
            returnMap.put("desc", "重启任务失败");
        }
        return returnMap;
    }

    /**
     * 删除任务
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value = "删除任务", notes = "传入任务id 通过id删除内存中的任务 任务在内存中清空（如果存在） 数据库中删除数据（已经由物理删除改为逻辑删除）")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @DeleteMapping("/{taskId}")
    public Map<String, Object> deleteTask(@PathVariable("taskId") Integer taskId) throws SchedulerException {
        logger.info("删除任务 id：" + taskId);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("status", 0);
        returnMap.put("desc", "删除任务失败");

        boolean isDelete = taskService.deleteTask(taskId);
        if (isDelete){
            taskDataService.deleteTask(taskId);
            taskDataService.codeScriptStateStopped(taskId);
            returnMap.put("status", 1);
            returnMap.put("desc", "删除任务成功");
        }

        return returnMap;
    }

    /**
     * 修改任务
     *
     * @throws SchedulerException
     */
    @ApiOperation(value = "修改任务", notes = "传入任务id和taks对象， 修改该id下任务 cron或time ")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PutMapping("/{taskId}")
    public Map<String, Object> updateTask(
            @PathVariable Integer taskId,
            @RequestBody Task task) throws SchedulerException {
        logger.info("修改任务 task：{} ", task.toString());

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("status", 0);
        returnMap.put("desc", "修改任务失败");

        boolean successUpdate = false;
        Task newTask = null;
//        cron
        if (taskId != null && task.getCronExpression() != null && !task.getCronExpression().isEmpty()) {
            newTask = new Task(taskId, task.getTaskName(), task.getCronExpression(),task.getType(),task.getRemark());
            successUpdate = taskDataService.updateCronTask(newTask);
            //成功更新 重启任务
            if (successUpdate) {
                taskService.rescheduleCronTask(taskId, task.getCronExpression());
                returnMap.put("status", 1);
                returnMap.put("desc", "成功修改循环任务");
            }
        }
//       time
        if (taskId != null && task.getTimeExpression() != null && !task.getTimeExpression().equals(new Date(0))) {
            newTask = new Task(taskId, task.getTaskName(), task.getTimeExpression(),task.getType(),task.getRemark());
            successUpdate = taskDataService.updateOnceTask(newTask);
            //成功更新 重启任务
            if (successUpdate) {
                taskService.rescheduleOnceTask(taskId, task.getTimeExpression());
                returnMap.put("status", 1);
                returnMap.put("desc", "成功修改单次任务");
            }
        }

        return returnMap;
    }


}
