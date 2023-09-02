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

@Component
public class DatabaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
//    @Value("${spring.datasource.url}")
    private static String JDBC_URL="jdbc:mysql://121.37.188.176:3307/dataGovernance?useSSL=false&characterEncoding=utf-8";
//    @Value("${spring.datasource.username}")
    private static String USERNAME="root";
//    @Value("${spring.datasource.password}")
    private static String PASSWORD="root";

    // 建立数据库连接
    public static Connection connect() {
        try {
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 关闭数据库连接
    public static  void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Connection con= DatabaseConnector.connect();
        if (con==null){
            System.out.println("连接失败");
        }else {
            System.out.println("连接成功");
        }
    }
}
