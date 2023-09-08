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

    /**
     * 执行.py 文件 接受一个python脚本路径作为参数
     *
     * @param scriptPath
     * @return String 返回脚本的输出结果
     */
    public String executePythonScript(String scriptPath) {
        try {
            //使用ProcessBuilder来启动一个新的进程，并指定Python可执行文件和脚本路径作为参数
            ProcessBuilder processBuilder = new ProcessBuilder(pythonExecutable, scriptPath);
            // 通过读取进程的输出流，将输出内容逐行读取并存储到StringBuilder中。
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();

            //逐行读取进程的输入流，并将每行内容添加到一个StringBuilder对象中
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            //脚本执行完毕后，通过调用process.waitFor()方法获取脚本的退出码
            int exitCode = process.waitFor();
            //如果退出码为0，则表示脚本执行成功将StringBuilder中的内容转换为字符串并返回。
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

    //util 单元测试
    public static void main(String[] args) {
//        String pythonExecutable = "C:\\Users\\22304\\anaconda3\\python.exe"; // 或者使用具体的Python解释器路径，如"C:/Python38/python.exe"
        String pythonExecutable = "python";
        String pythonScriptPath = "src/main/resources/pyfile/test.py"; //Python脚本文件的路径

        ExecutePythonScript executor = new ExecutePythonScript(pythonExecutable);
        String result = executor.executePythonScript(pythonScriptPath);
        System.out.println("Python output:\n" + result);

    }
}
