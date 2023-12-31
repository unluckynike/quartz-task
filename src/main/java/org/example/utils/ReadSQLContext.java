package org.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * @Package org.example.utils
 * @Author hailin
 * @Date 2023/9/16
 * @Description :  读取sql文件内容工具类
 */

public class ReadSQLContext {

    /**
     * 读到.sql文件内容
     *
     * @param filePath
     * @return String 返回文件中的内容
     */
    public static String readSQLFile(String filePath) {
        StringBuilder sqlContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sqlContent.toString();}
}
