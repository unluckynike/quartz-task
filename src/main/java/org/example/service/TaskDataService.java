package org.example.service;

import org.example.controller.TaskController;
import org.example.pojo.Task;
import org.example.utils.CronUtil;
import org.example.utils.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskDataService {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    //数据库连接 这里先置 下面具体方法里才得到连接对象
    private Connection connection = null;

    /**
     * 添加任务信息到数据库 包含cron表达式合法性校验
     *
     * @param task 传入一个Task对象
     * @return boolean 成功true 失败false
     */
    public boolean addTask(Task task) {
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
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //关闭数据库连接
            new DatabaseConnector().closeConnection(connection);

            return true;
        } else {
            //不执行添加 错误信息cron日志
            logger.error("无效的Cron表达式，任务未添加到数据库: " + CronUtil.getInvalidMessage(task.getCronExpression()));
            return false;
        }
    }

    /**
     * 查到最后一条（最新添加）任务 这个用于创建任务之后调用
     *
     * @return Task 返回在数据库中查到的task
     */
    public Task getLastTask() {
        Task task = new Task();
        String sqlQuery = "SELECT taskid,taskName,cronExpression FROM tasks ORDER BY taskid DESC LIMIT 1";

        connection = new DatabaseConnector().connect();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                task.setTaskId(result.getInt("taskid"));
                task.setTaskName(result.getString("taskName"));
                task.setCronExpression(result.getString("cronExpression"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        new DatabaseConnector().closeConnection(connection);
        return task;
    }

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
        }
        new DatabaseConnector().closeConnection(connection);

    }

    public boolean updateTask(Task task) {
        //检查cron表达式是否合法
        if (CronUtil.isValid(task.getCronExpression())) {
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
            }
            new DatabaseConnector().closeConnection(connection);
            return true;
        } else {
            //不执行修改 输出错误cron日志
            logger.error("无效的Cron表达式，任务未添加到数据库: " + CronUtil.getInvalidMessage(task.getCronExpression()));
            return false;
        }
    }

    public List<Task> getAllTasks() {
        connection = new DatabaseConnector().connect();
        List<Task> tasks = new ArrayList<>();
        try {
            String sqlQuery = "SELECT * FROM tasks";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Task task = new Task(
                        resultSet.getInt("taskId"),
                        resultSet.getString("taskName"),
                        resultSet.getString("cronExpression")
                );
                tasks.add(task);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
