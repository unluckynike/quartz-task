package org.example.utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * @Package org.example.utils
 * @Author hailin
 * @Date 2023/12/27
 * @Description : 配置文件值获取
 */

public class PropertyLoader {

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Sorry, unable to find ");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties from " , e);
        }
        return properties;
    }
}
