package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/*
 * @Package org.example.pojo
 * @Author hailin
 * @Date 2023/8/11
 * @Description : 任务实体
 */

@ApiModel
public class Task {

    @ApiModelProperty("任务id")
    private Integer taskId;
    @ApiModelProperty("任务名称")
    private String taskName;
    @ApiModelProperty("任务表达式 cron表达式 ")
    private String cronExpression;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createtime = null;
    @ApiModelProperty("上次更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatetime = null;


    public Task() {
    }

    //cron constructor
    public Task(Integer taskId, String taskName, String cronExpression) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.cronExpression = cronExpression;
    }

    // getters and setters
    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String expression) {
        this.cronExpression = expression;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}

