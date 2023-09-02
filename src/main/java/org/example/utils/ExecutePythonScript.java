package org.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//启动python代码工具类
public class ExecutePythonScript {
    private String pythonExecutable;

    public ExecutePythonScript(String pythonExecutable) {
        this.pythonExecutable = pythonExecutable;
    }

    //执行 .py代码
    public String executePythonScript(String scriptPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return " Python 脚本错误";
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "An error occurred.";
        }
    }

    //util测试
    public static void main(String[] args) {
//        String pythonExecutable = "C:\\Users\\22304\\anaconda3\\python.exe"; // 或者使用具体的Python解释器路径，如"C:/Python38/python.exe"
        String pythonExecutable = "python";
        String pythonScriptPath = "D:\\Project\\IDEA\\task\\src\\main\\resources\\pyfile\\test.py"; //Python脚本文件的路径

        ExecutePythonScript executor = new ExecutePythonScript(pythonExecutable);
        String result = executor.executePythonScript(pythonScriptPath);
        System.out.println("Python output:\n" + result);

    }
}
