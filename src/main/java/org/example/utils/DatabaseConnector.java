package org.example.utils;

import org.example.controller.TaskController;
import org.example.pojo.Task;
import org.example.service.TaskDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * @Package org.example.utils
 * @Author hailin
 * @Date 2023/8/14
 * @Description :  数据库连接工具类 开关数据库
 */

@Component
public class DatabaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    //一直不能从配置文件获取值！！
//    @Value("${spring.datasource.url}")
    private String jdbcUrl="jdbc:mysql://121.37.188.176:3307/dataGovernance?useSSL=false&characterEncoding=utf-8";

//    @Value("${spring.datasource.username}")
    private String username="root";

//    @Value("${spring.datasource.password}")
    private String password="root";

    /**
     * 得到Connection建立数据库连接
     *
     * @return Connection
     */
    public Connection connect() {
        try {
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            logger.error("无法建立数据库连接", e);
            throw new RuntimeException("无法建立数据库连接", e);
        }
    }

    /**
     * 关闭数据库连接
     *
     * @param connection
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("关闭数据库连接时出错", e);
            }
        }
    }

    //util工具类单元测试
    public static void main(String[] args) {
        DatabaseConnector databaseConnector = new DatabaseConnector();
        Connection connect = databaseConnector.connect();
        if (connect==null){
            logger.info("数据库连接失败");
        }else {
            logger.info("数据库连接成功");
        }
    }
}
