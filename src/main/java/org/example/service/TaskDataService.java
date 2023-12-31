package org.example.service;

import io.swagger.models.auth.In;
import org.example.controller.TaskController;
import org.example.pojo.CodeState;
import org.example.pojo.Task;
import org.example.pojo.TaskType;
import org.example.utils.CronUtil;
import org.example.utils.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.utils.DataUtil.isValidAndFormatTime;

/*
 * @Package org.example.service
 * @Author hailin
 * @Date 2023/8/11
 * @Description : 任务信息的数据库操作服务类
 */

@Service
public class TaskDataService {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    //数据库连接 这里先置空 下面具体方法里才得到连接对象
    private Connection connection = null;

    /**
     * 添加Cron任务信息到数据库 包含cron表达式合法性校验
     *
     * @param task
     * @return boolean
     */
    public boolean addCronTask(Task task) {
        //检查cron表达式是否合法 合法则加入数据库
        if (CronUtil.isValid(task.getCronExpression())) {
            //得到连接对象
            connection = new DatabaseConnector().connect();
            long identifyGroup = System.currentTimeMillis();

            try {
                String sqlQuery = "INSERT INTO tasks (task_name, cron_expression,type,remark,code_script,identify_group) VALUES (?, ?, ?, ?,?,?)";//id自增 不传参数
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                preparedStatement.setString(2, task.getCronExpression());
                preparedStatement.setString(3, task.getType().name()); //.name() 获取枚举常量的名称作为字符串。
                preparedStatement.setString(4, task.getRemark());
                preparedStatement.setString(5, task.getCodeScript());
                preparedStatement.setLong(6, identifyGroup);
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
     * 新增单次定时任务 包含TIME判空
     *
     * @param task
     * @return
     */
    public boolean addOnceTimeTask(Task task) {
        //检查时间格式是否合法
        if (task.getTimeExpression() != null) {//前端已经做了控制时间格式
            //得到连接对象
            connection = new DatabaseConnector().connect();
            long identifyGroup = System.currentTimeMillis();

            try {
                String sqlQuery = "INSERT INTO tasks (task_name, time_expression,type,remark,code_script,identify_group) VALUES (?, ?, ?, ?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                //Timestamp 继承自 java.util.Date getTime 返回自1970-1-1自现在的秒
                preparedStatement.setTimestamp(2, new Timestamp(task.getTimeExpression().getTime()));
                preparedStatement.setString(3, task.getType().name());
                preparedStatement.setString(4, task.getRemark());
                preparedStatement.setString(5, task.getCodeScript());
                preparedStatement.setLong(6, identifyGroup);
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

    /**
     * 修改任务
     *
     * @param task
     */
    public void updateTask(Task task) {
        connection = new DatabaseConnector().connect();
        if (task.getCronExpression() != null && !task.getCronExpression().isEmpty()) {
            //cron
            if (CronUtil.isValid(task.getCronExpression())) {
                try {
                    String sqlQuery = "INSERT INTO tasks (task_name, cron_expression,type,remark,code_script,identify_group) VALUES (?, ?, ?, ?,?,?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                    preparedStatement.setString(1, task.getTaskName());
                    preparedStatement.setString(2, task.getCronExpression());
                    preparedStatement.setString(3, task.getType().name());
                    preparedStatement.setString(4, task.getRemark());
                    preparedStatement.setString(5, task.getCodeScript());
                    preparedStatement.setLong(6, task.getIdentifyGroup());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    //关闭数据库连接
                    new DatabaseConnector().closeConnection(connection);
                }
            } else {
                //不执行修改 输出错误cron日志
                logger.error("无效的Cron表达式，任务未添加到数据库: " + CronUtil.getInvalidMessage(task.getCronExpression()));
            }

            //time
        } else {
            //time
            try {
                String sqlQuery = "INSERT INTO tasks (task_name, time_expression,type,remark,code_script,identify_group) VALUES (?, ?, ?, ?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                preparedStatement.setTimestamp(2, new Timestamp(task.getTimeExpression().getTime()));//getTime 返回自1970-1-1自现在的秒
                preparedStatement.setString(3, task.getType().name());
                preparedStatement.setString(4, task.getRemark());
                preparedStatement.setString(5, task.getCodeScript());
                preparedStatement.setLong(6, task.getIdentifyGroup());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                new DatabaseConnector().closeConnection(connection);
            }
        }
    }


    /**
     * 修改版本号 版本号迭代
     *
     * @param taskid
     * @return
     */
    public boolean updateVersion(Integer taskid, float version) {
        connection = new DatabaseConnector().connect();
        try {
            String sqlQuery = "UPDATE tasks SET version = ? WHERE task_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setFloat(1, version);
            preparedStatement.setInt(2, taskid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return false;
    }

    /**
     * 更新数据库task表里Cron任务数据
     *
     * @param task
     * @return boolean
     */
    public boolean updateCronTask(Task task) {
        //检查cron表达式是否合法 cron表达式合法再打开数据库连接
        if (CronUtil.isValid(task.getCronExpression())) {
            connection = new DatabaseConnector().connect();
            try {
                String sqlQuery = "INSERT INTO tasks (task_name, cron_expression,type,remark,code_script,identify_group) VALUES (?, ?, ?, ?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, task.getTaskName());
                preparedStatement.setString(2, task.getCronExpression());
                preparedStatement.setString(3, task.getType().name());
                preparedStatement.setString(4, task.getRemark());
                preparedStatement.setInt(5, task.getTaskId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
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
     * 更新数据库task表里time任务数据
     *
     * @param task
     * @return
     */
    public boolean updateOnceTask(Task task) {
        connection = new DatabaseConnector().connect();
        try {
            String sqlQuery = "UPDATE tasks SET task_name = ?, time_expression = ?, type = ?, remark = ? WHERE task_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, task.getTaskName());
            preparedStatement.setTimestamp(2, new Timestamp(task.getTimeExpression().getTime()));//getTime 返回自1970-1-1自现在的秒
            preparedStatement.setString(3, task.getType().name());
            preparedStatement.setString(4, task.getRemark());
            preparedStatement.setInt(5, task.getTaskId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return false;
    }

    /**
     * 删除taks 逻辑删除 通过taskId
     *
     * @param taskId
     */
    public int deleteTask(Integer taskId) {
        connection = new DatabaseConnector().connect();

        int i = 0;
        try {
            String sqlQuery = "UPDATE tasks SET is_delete = 1 WHERE task_id =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return i;
    }

    /**
     * 撤回删除 逻辑删除 通过taskId
     *
     * @param taskId
     * @return
     */
    public int recallDeleteTask(Integer taskId) {
        connection = new DatabaseConnector().connect();
        int i = 0;
        try {
            String sqlQuery = "UPDATE tasks SET is_delete = 0 WHERE task_id =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return i;
    }


    /**
     * 获取版本号
     *
     * @param taskId
     * @return
     */
    public float getOldTaskVersion(Integer taskId) {
        connection = new DatabaseConnector().connect();
        float version = 1;
        try {
            String sqlQuery = "SELECT COUNT(*) AS maxVersion FROM tasks WHERE identify_group = (SELECT identify_group FROM tasks WHERE task_id=?)";
            //SELECT MAX(version) AS maxVersion FROM tasks WHERE identify_group = (SELECT identify_group FROM tasks WHERE task_id = 91)
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                version = resultSet.getFloat("maxVersion");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }

        return version;
    }

    /**
     * 查到最后一条（最新添加）任务  查到数据库最后一条（新插入的）数据
     *
     * @return Task 返回在数据库中查到的task
     */
    public Task getLastTask() {
        connection = new DatabaseConnector().connect();
        Task task = new Task();

        try {
            String sqlQuery = "SELECT task_id,task_name,type,cron_expression,time_expression,remark,code_script FROM tasks ORDER BY task_id DESC LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                task.setTaskId(result.getInt("task_id"));
                task.setTaskName(result.getString("task_name"));
                task.setTaskType(TaskType.valueOf(result.getString("type")));
                task.setCronExpression(result.getString("cron_expression"));
                task.setTimeExpression(result.getTimestamp("time_expression"));
                task.setRemark(result.getString("remark"));
                task.setCodeScript(result.getString("code_script"));
            }
            //关闭结果集和预编译语句。
            result.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return task;
    }


    /**
     * 根据id查询任务
     *
     * @param taskId
     * @return
     */
    public Task getTaskById(Integer taskId) {
        connection = new DatabaseConnector().connect();
        Task task = new Task();
        task.setTaskId(taskId);
        try {
            String sqlQuery = "SELECT task_name,type,cron_expression,time_expression,remark,code_script,identify_group,version,state,is_activate,is_delete FROM tasks WHERE task_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            ResultSet resultSet = preparedStatement.executeQuery();
            //封装出task对象
            while (resultSet.next()) {
                task.setTaskName(resultSet.getString("task_name"));
                task.setType(TaskType.valueOf(resultSet.getString("type")));
                task.setCronExpression(resultSet.getString("cron_expression"));
                task.setTimeExpression(resultSet.getDate("time_expression"));
                task.setRemark(resultSet.getString("remark"));
                task.setCodeScript(resultSet.getString("code_script"));
                task.setIdentifyGroup(resultSet.getLong("identify_group"));
                task.setVersion(resultSet.getFloat("version"));
                task.setState(CodeState.valueOf(resultSet.getString("state")));
                task.setIsActivate(resultSet.getByte("is_activate"));
                task.setIsDelete(resultSet.getByte("is_delete"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }

        return task;
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
            String sqlQuery = "SELECT task_id,task_name,type,cron_expression,time_expression,remark,code_script,identify_group,version,state,is_activate,is_delete,createtime,updatetime FROM tasks WHERE is_delete=0";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            //封装出task对象
            while (resultSet.next()) {
                Task task = new Task();
                task.setTaskId(resultSet.getInt("task_id"));
                task.setTaskName(resultSet.getString("task_name"));
                task.setType(TaskType.valueOf(resultSet.getString("type")));
                task.setCronExpression(resultSet.getString("cron_expression"));
                task.setTimeExpression(resultSet.getDate("time_expression"));
                task.setRemark(resultSet.getString("remark"));
                task.setCodeScript(resultSet.getString("code_script"));
                task.setIdentifyGroup(resultSet.getLong("identify_group"));
                task.setVersion(resultSet.getFloat("version"));
                task.setState(CodeState.valueOf(resultSet.getString("state")));
                task.setIsActivate(resultSet.getByte("is_activate"));
                task.setIsDelete(resultSet.getByte("is_delete"));
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


    /**
     * 根据taskId获得代码脚本
     *
     * @param taskId
     * @return
     */
    public String getCodeScriptById(Integer taskId) {
        String codeScript = null;
        try (Connection connection = new DatabaseConnector().connect()) {
            String sqlQuery = "SELECT code_script FROM tasks WHERE task_id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, taskId);
                // 执行查询
                try (ResultSet resultSet = preparedStatement.executeQuery()) { //使用 try-with-resources 语句来自动关闭资源，确保在发生异常时也会正确关闭连接
                    if (resultSet.next()) {
                        // 从结果集中获取代码脚本
                        codeScript = resultSet.getString("code_script");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codeScript;
    }


    /**
     * 得到同标识的一组任务
     *
     * @param taskid
     * @return
     */
    public List<Task> getTasksIdentityGroupById(Integer taskid) {
        connection = new DatabaseConnector().connect();
        List<Task> tasks = new ArrayList<>();
        try {
            String sqlQuery = "SELECT * FROM tasks WHERE identify_group=(SELECT identify_group FROM tasks WHERE task_id=?) ORDER BY createtime DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskid);
            ResultSet resultSet = preparedStatement.executeQuery();
            //封装出task对象
            while (resultSet.next()) {
                Task task = new Task();
                task.setTaskId(resultSet.getInt("task_id"));
                task.setTaskName(resultSet.getString("task_name"));
                task.setType(TaskType.valueOf(resultSet.getString("type")));
                task.setCronExpression(resultSet.getString("cron_expression"));
                task.setTimeExpression(resultSet.getDate("time_expression"));
                task.setRemark(resultSet.getString("remark"));
                task.setCodeScript(resultSet.getString("code_script"));
                task.setIdentifyGroup(resultSet.getLong("identify_group"));
                task.setVersion(resultSet.getFloat("version"));
                task.setState(CodeState.valueOf(resultSet.getString("state")));
                task.setIsActivate(resultSet.getByte("is_activate"));
                task.setIsDelete(resultSet.getByte("is_delete"));
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

    /**
     * 修改脚本状态为暂停
     *
     * @param taskId
     * @return
     */
    public int codeScriptStatePause(Integer taskId) {
        connection = new DatabaseConnector().connect();
        int i = 0;
        try {
            String sqlQuery = "UPDATE tasks SET state='PAUSED' WHERE task_id =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return i;
    }


    /**
     * 修改脚本状态为启用
     *
     * @param taskId
     * @return
     */
    public int codeScriptStateEnable(Integer taskId) {
        connection = new DatabaseConnector().connect();
        int i = 0;
        try {
            String sqlQuery = "UPDATE tasks SET state='ENABLED' WHERE task_id =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return i;
    }

    /**
     * 修改脚本状态为停止
     *
     * @param taskId
     * @return
     */
    public int codeScriptStateStopped(Integer taskId) {
        connection = new DatabaseConnector().connect();
        int i = 0;
        try {
            String sqlQuery = "UPDATE tasks SET state='STOPPED' WHERE task_id =?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskId);
            i = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return i;
    }
    /**
     * 更新激活状态
     *
     * @param taskid
     * @return
     */
    public boolean updateActivate(Integer taskid) {
        connection = new DatabaseConnector().connect();
        try {
            String sqlQuery = "UPDATE tasks SET is_activate = CASE WHEN is_activate = 0 THEN 1 ELSE 0 END WHERE task_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
        return false;
    }

    /**
     * 激活
     *
     * @param taskid
     */
    public void setActivate(Integer taskid) {
        connection = new DatabaseConnector().connect();
        try {
            String sqlQuery = "UPDATE tasks SET is_activate=1 WHERE task_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
    }

    /**
     * 不激活
     *
     * @param taskid
     */
    public void setUnActivate(Integer taskid) {
        connection = new DatabaseConnector().connect();
        try {
            String sqlQuery = "UPDATE tasks SET is_activate=0 WHERE task_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, taskid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            new DatabaseConnector().closeConnection(connection);
        }
    }

}
