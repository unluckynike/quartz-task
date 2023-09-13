/*
 * @Package PACKAGE_NAME
 * @Author hailin
 * @Date 2023/9/12
 * @Description :
 */


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

public  class MyJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 打印当前的时间
        System.out.println("当前的时间是：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        System.out.println(" 单次任务运行啦  ！ Hello Quartz! MyJob is running.");
    }
}
