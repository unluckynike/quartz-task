package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;


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
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);

    private Properties properties;

    public DatabaseConnector() {
        this.properties = PropertyLoader.loadProperties();
    }

    public Connection connect() {
        String jdbcUrl = properties.getProperty("spring.datasource.url");
        String username = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");

        try {
            // Register the MySQL JDBC driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            logger.error("Error establishing database connection", e);
            throw new RuntimeException("Error establishing database connection", e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    public static void main(String[] args) {
        DatabaseConnector databaseConnector = new DatabaseConnector();
        Connection connect = databaseConnector.connect();
        if (connect == null) {
            logger.info("Database connection failed");
        } else {
            logger.info("Database connection successful");
            // Close the connection if needed
            databaseConnector.closeConnection(connect);
        }
    }
}