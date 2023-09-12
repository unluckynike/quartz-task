package org.example.job;

import org.example.controller.TaskController;
import org.example.utils.ExecutePythonScript;
import org.example.utils.ReadSQLContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class SampleJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    //SQL文件路径
    private final String SQL_FILE_PAHT = "/Users/hailin/Project/IdeaCode/quartz-task/src/main/resources/sqlfile/test.sql";
    //    private final String SQL_FILE_PAHT = "D:\\Project\\IDEA\\task\\src\\main\\resources\\sqlfile\\test.sql";
    //Python文件路径
    private final String PY_FILE_PATH = "/Users/hailin/Project/IdeaCode/quartz-task/src/main/resources/pyfile/test.py";
    //    private final String PY_FILE_PATH = "D:\\Project\\IDEA\\task\\src\\main\\resources\\pyfile\\test.py";
    //python解释器
    private final String PY_INTERCPTER_PATH = "python";//可以换成本地解释器

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(now) + " 执行job...");

        //实现定时任务逻辑

        //抓数据

        //执行.py文件 方法样例
        ExecutePythonScript executor = new ExecutePythonScript(PY_INTERCPTER_PATH);
        String pyOutContex = executor.executePythonScript(PY_FILE_PATH);
        logger.info("\n .py 执行输出内容： " + pyOutContex);

        //读.sql文件 方法样例
        String sqlContex = ReadSQLContext.readSQLFile(SQL_FILE_PAHT);
        logger.info("\n sql内容 : " + sqlContex);
    }

}

