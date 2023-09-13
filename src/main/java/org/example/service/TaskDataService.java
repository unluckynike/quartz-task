package org.example.service;

import org.example.controller.TaskController;
import org.example.pojo.Task;
import org.example.utils.CronUtil;
import org.example.utils.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
 * @Package org.example.service
 * @Author hailin
 * @Date 2023/8/11
 * @Description : 任务信息的数据库操作服务类
 */

@Service
public class TaskDataService {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    //数据库连接 这里先置 下面具体方法里才得到连接对象
    private Connection connection = null;

    /**
     * 添加Cron任务信息到数据库 包含cron表达式合法性校验
     *
     * @param task 传入一个Task对象
     * @return boolean 成功true 失败false
     */
    public boolean addCronTask(Task task) {
        //检查cron表达式合不合法 成功加入数据库
        if (CronUtil.isValid(task.getCronExpression())) {
            //得到连接对象
            connection = new DatabaseConnector().connect();

            try {
                String sqlQuery = "INSERT INTO tasks (taskName, cronExpression) VALUES (?, ?)";//id自增 不传参数
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                preparedStatement.setString(2, task.getCronExpression());
                preparedStatement.executeUpdate();
                //关闭预编译语句，释放相关资源。
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //关闭数据库连接
                new DatabaseConnector().closeConnection(connection);
            }
            return true;
        } else {
            //不执行添加 错误信息cron日志
            logger.error("无效的Cron表达式，任务未添加到数据库: " + CronUtil.getInvalidMessage(task.getCronExpression()));
            return false;
        }
    }

    /**
     * 查到最后一条（最新添加）任务 这个用于创建任务之后调用 查到数据库最后一条（新插入的）数据 放进内存里触发任务
     * <p>
     * ！！！！！ 实体同时有 cronExpression 和 timeExpression  改方法需要改写 ！！！！！！
     *
     * @return Task 返回在数据库中查到的task
     */
    public Task getLastTask() {
        Task task = new Task();

        connection = new DatabaseConnector().connect();

        try {
            String sqlQuery = "SELECT taskid,taskName,cronExpression,timeExpression FROM tasks ORDER BY taskid DESC LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                task.setTaskId(result.getInt("taskid"));
                task.setTaskName(result.getString("taskName"));
                task.setCronExpression(result.getString("cronExpression"));
                task.setTimeExpression(result.getTimestamp("timeExpression"));
            }
            //关闭结果集和预编译语句。
            result.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return task;
    }

    /**
     * 更新数据库task表里Cron任务数据
     *
     * @param task
     * @return boolean
     */
    public boolean updateCronTask(Task task) {
        //检查cron表达式是否合法
        if (CronUtil.isValid(task.getCronExpression())) {
            //cron表达式合法再打开数据库连接
            connection = new DatabaseConnector().connect();
            try {
                String sqlQuery = "UPDATE tasks SET taskName = ?, cronExpression = ? WHERE taskId = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                preparedStatement.setString(2, task.getCronExpression());
                preparedStatement.setInt(3, task.getTaskId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                //关闭数据库连接
                new DatabaseConnector().closeConnection(connection);
            }
            return true;
        } else {
            //不执行修改 输出错误cron日志
            logger.error("无效的Cron表达式，任务未添加到数据库: " + CronUtil.getInvalidMessage(task.getCronExpression()));
            return false;
        }
    }

    /**
     * 删除taks  通过taskId
     *
     * @param taskId
     */
    public void deleteTask(Integer taskId) {
        connection = new DatabaseConnector().connect();

        try {
            String sqlQuery = "DELETE FROM tasks WHERE taskId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
    }


    /**
     * 查询数据库中的全部task
     *
     * @return List
     */
    public List<Task> getAllTasks() {
        connection = new DatabaseConnector().connect();
        List<Task> tasks = new ArrayList<>();
        try {
            String sqlQuery = "SELECT * FROM tasks";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            //封装出task对象
            while (resultSet.next()) {
                Task task = new Task();
                task.setTaskId(resultSet.getInt("taskId"));
                task.setTaskName(resultSet.getString("taskName"));
                task.setCronExpression(resultSet.getString("cronExpression"));
                task.setTimeExpression(resultSet.getDate("timeExpression"));
                task.setCreatetime(resultSet.getDate("createtime"));
                task.setUpdatetime(resultSet.getDate("updatetime"));
                tasks.add(task);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);

        }

        return tasks;
    }


    // 新增单次定点时间任务
    public boolean addOnceTimeTask(Task task) {
        //检查时间格式合不合法 成功加入数据库
        if (true) {//先来一个true 后面加时间校验
            //得到连接对象
            connection = new DatabaseConnector().connect();

            try {
                String sqlQuery = "INSERT INTO tasks (taskName, timeExpression) VALUES (?, ?)";//id自增 不传参数
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                //Timestamp 继承自 java.util.Date
                preparedStatement.setTimestamp(
                        2,
                        new Timestamp(task.getTimeExpression().getTime()) //getTime 返回自1970-1-1自现在的秒
                );
                preparedStatement.executeUpdate();
                //关闭预编译语句，释放相关资源。
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // 关闭数据库连接
                new DatabaseConnector().closeConnection(connection);
            }
            return true;
        } else {
            //不执行添加 错误信息cron日志
            logger.error("无效的时间表达式，任务未添加到数据库: ");
            return false;
        }
    }


}
