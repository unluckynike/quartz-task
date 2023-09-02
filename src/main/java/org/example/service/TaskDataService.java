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

    //连接数据库
    private Connection connection = new DatabaseConnector().connect();

    public boolean addTask(Task task) {
        //检查cron表达式合不合法 成功加入数据库
        if (CronUtil.isValid(task.getCronExpression())){
            try {
                String sqlQuery = "INSERT INTO tasks (taskName, cronExpression) VALUES (?, ?)";//id 自增
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                preparedStatement.setString(2, task.getCronExpression());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            //不执行
            logger.error("无效的Cron表达式，任务未添加到数据库: "+CronUtil.getInvalidMessage(task.getCronExpression()));
            return false;
        }
    }

    //查到最后一条（最新添加）任务
    public Task getLastTask()  {
        Task task=new Task();
        String sqlQuery="SELECT taskid,taskName,cronExpression FROM tasks ORDER BY taskid DESC LIMIT 1";
        try {
            PreparedStatement  preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                task.setTaskId(result.getInt("taskid"));
                task.setTaskName(result.getString("taskName"));
                task.setCronExpression(result.getString("cronExpression"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    public void deleteTask(Integer taskId) {
        try {
            String sqlQuery = "DELETE FROM tasks WHERE taskId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
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
    }

    public List<Task> getAllTasks() {
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
