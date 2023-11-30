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

    /******************************************************** 循环执行的任务 cron ****************************************************************/

    /**
     * 查看数据库任务 查到的是数据库中的任务
     * 请求地址：127.0.0.1:8080/tasks
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
     * 请求地址: 127.0.0.1:8080/memory
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
        } else if (taskList.size() > 0) {
            returnMap.put("code", 0);
            returnMap.put("msg", "成功查看内存任务");
            returnMap.put("count", taskList.size());
            returnMap.put("data", taskList);
        }

        return returnMap;
    }

    /**
     * 查看内存任务 查看内存任务状态
     * 请求地址: 127.0.0.1:8080/memoryState
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
        } else if (stateMap.size() > 0) {
            returnMap.put("code", 0);
            returnMap.put("msg", "成功查看内存任务状态");
            returnMap.put("count", stateMap.size());
            returnMap.put("data", stateMap);
        }

        return returnMap;
    }

    /**
     * 创建并开启任务 自动触发 传入任务信息
     * <p>
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
    @ApiOperation(value = "创建并开启多次循环任务", notes = "传入cron任务对象 任务自动触发 任务信息存入数据库 存入内存")
    @PostMapping("/createLoopTask")
    public Map<String, Object> createCronTask(@RequestBody Task task) throws SchedulerException {
        logger.info("task infor: " + task.toString());

        Map<String, Object> returnMap = new HashMap<>();  //返回参数
        returnMap.put("status", 0);
        returnMap.put("desc", "创建并开启多次循环任务失败");

        boolean successAdd = taskDataService.addCronTask(task);

        if (successAdd) {
            //执行的时候 查到最后一条（也就是最新添加的）任务执行
            taskService.createLoopTask(taskDataService.getLastTask());
            returnMap.put("status", 1);
            returnMap.put("desc", "成功创建并开启多次循环任务");
        }

        return returnMap;
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
    @ApiOperation(value = "暂停任务", notes = "传入任务id 通过id暂停内存中的任务 任务留在内存中")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PostMapping("/{taskId}/pause")
    public Map<String, Object> pauseTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("暂停任务 id：" + taskId);
        Map<String, Object> returnMap = new HashMap<>();  //返回参数

        boolean isPause = taskService.pauseTask(taskId);
        if (isPause) {
            returnMap.put("status", 1);
            returnMap.put("desc", "成功暂停任务");
        } else {
            returnMap.put("status", 0);
            returnMap.put("desc", "暂停任务失败");
        }
        return returnMap;
    }

    /**
     * 启动任务 传入任务id
     * 请求地址：127.0.0.1:8080/tasks/1/resume
     * 请求参数：taskId
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value = "启动任务", notes = "传入任务id 通过id重新启动内存中的任务")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PostMapping("/{taskId}/resume")
    public Map<String, Object> resumeTask(@PathVariable Integer taskId) throws SchedulerException {
        logger.info("重启任务 id：" + taskId);
        Map<String, Object> returnMap = new HashMap<>();  //返回参数

        boolean isResume = taskService.resumeTask(taskId);
        if (isResume) {
            returnMap.put("status", 1);
            returnMap.put("desc", "成功重启任务");
        } else {
            returnMap.put("status", 0);
            returnMap.put("desc", "重启任务失败");
        }
        return returnMap;
    }

    /**
     * 删除任务 传入任务id
     * 请求地址: http://localhost:8080/tasks/1
     * 请求参数：taskId
     *
     * @param taskId
     * @throws SchedulerException
     */
    @ApiOperation(value = "删除任务", notes = "传入任务id 通过id删除内存中的任务 任务在内存中清空（如果存在） 数据库中删除数据")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @DeleteMapping("/{taskId}")
    public Map<String, Object> deleteTask(@PathVariable("taskId") Integer taskId) throws SchedulerException {
        logger.info("删除任务 id：" + taskId);
        Map<String, Object> returnMap = new HashMap<>();

        int i = taskDataService.deleteTask(taskId);
        //考虑一种需要补充的情况 删除的时候 db中有 但是他并不在内存中
        boolean isDelete = taskService.deleteTask(taskId);

        returnMap.put("status", i);
        returnMap.put("desc", "删除任务成功");

        if (i == 0) {
            returnMap.put("status", i);
            returnMap.put("desc", "删除任务失败");
        }
        return returnMap;
    }

    /**
     * 修改任务 修改task表达式
     * 请求地址：http://localhost:8080/tasks/6
     * 请求参数：jsons数据
     *
     * @throws SchedulerException
     */
    @ApiOperation(value = "修改任务", notes = "传入任务id和taks对象， 修改该id下的taskName cron表达式或者time表达式 ")
    @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Integer")
    @PutMapping("/{taskId}")
    public Map<String, Object> rescheduleLoopTask(
            @PathVariable Integer taskId,
            @RequestBody Task task) throws SchedulerException {

        logger.debug("修改任务 task：{} ", task.toString());

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

    /******************************************************** 单次执行的任务 ****************************************************************/

    /**
     * 创建单次定点任务并开启执行
     *
     * @param task
     * @throws SchedulerException json对象
     */
    @ApiOperation(value = "创建并开启执行单次定时任务", notes = "传入任务对象 任务自动触发 任务信息存入数据库 存入内存")
    @PostMapping("/createOnceTimeTask")
    public Map<String, Object> createOnceTimeTask(@RequestBody Task task) throws SchedulerException {
        logger.info("task infor : " + task.toString());

        Map<String, Object> returnMap = new HashMap<>();  //返回参数
        returnMap.put("status", 0);
        returnMap.put("desc", "创建单次定时任务并开启执行失败");
        //单次任务 加入数据库
        boolean successAdd = taskDataService.addOnceTimeTask(task);

        if (successAdd) {
            //controller如果直接传task对象 拿不到taskId 还得需要过DB数据库
            taskService.createOnceTimeTask(taskDataService.getLastTask());
            returnMap.put("status", 1);
            returnMap.put("desc", "成功创建单次定时任务并开启执行");
        }
        return returnMap;
    }
}
