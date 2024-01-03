package org.example.utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * @Package org.example.utils
 * @Author hailin
 * @Date 2023/12/27
 * @Description : 配置文件加载类
 */

public class PropertyLoader {

    public static Properties loadProperties() {
        Properties properties = new Properties();
        // getResourceAsStream() 加载资源文件时如果资源文件位于类路径的根目录下，只需要指定文件名即可。若资源文件位于类路径的子目录下，需要在文件名之前加上子目录的路径。
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
