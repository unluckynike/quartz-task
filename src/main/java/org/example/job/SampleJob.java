package org.example.job;

import org.example.controller.TaskController;
import org.example.utils.ExecutePythonScript;
import org.example.utils.ReadSQLContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * @Package org.example.job
 * @Author hailin
 * @Date 2023/8/11
 * @Description : 具体任务实现类  实现Job接口的 execute（）方法
 */

@Component
public class SampleJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    //SQL文件路径
    private final String SQL_FILE_PAHT = "src/main/resources/sqlfile/test.sql";

    //default python interceptor
    private final String PY_INTERCPTER_PATH = "python";
    ExecutePythonScript executor = new ExecutePythonScript(PY_INTERCPTER_PATH);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logger.info(format.format(now) + " 执行job...");

        JobDataMap jobDataMap = context.getMergedJobDataMap();//context.getJobDetail().getJobDataMap();
        String codeScript = jobDataMap.getString("codeScript");


        //执行.py
        String pyOutContex = executor.executePythonScriptByCode(codeScript);
        logger.info("\npy执行输出内容：\n" + pyOutContex);

        //读.sql
//        String sqlContex = ReadSQLContext.readSQLFile(SQL_FILE_PAHT);
//        logger.info("\n sql内容 : " + sqlContex);
    }

}

