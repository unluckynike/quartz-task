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
    @ApiModelProperty("任务表达式 cron表达式 针对多次循环时间任务")
    private String cronExpression;

    @ApiModelProperty("任务表达式 时间表达式 针对单次定点时间任务 ")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeExpression;

    @ApiModelProperty("任务类型")
    private Type type;

    @ApiModelProperty("任务描述")
    private String remark;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createtime = null;
    @ApiModelProperty("上次更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatetime = null;


    public Task() {
    }

    //cronExpression constructor
    public Task(Integer taskId, String taskName, String cronExpression) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.cronExpression = cronExpression;
    }

    //timeExpression constructor
    public Task(Integer taskId, String taskName, Date timeExpression) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.timeExpression = timeExpression;
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

    public Date getTimeExpression() {
        return timeExpression;
    }

    public void setTimeExpression(Date timeExpression) {
        this.timeExpression = timeExpression;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", timeExpression=" + timeExpression +
                ", type=" + type +
                ", remark='" + remark + '\'' +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}

