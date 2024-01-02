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

    @ApiModelProperty("任务描述")
    private String remark;

    @ApiModelProperty("任务类型")
    private TaskType taskType;

    @ApiModelProperty("代码脚本")
    private String codeScript;

    @ApiModelProperty("代码脚本版本")
    private float version;

    @ApiModelProperty("代码脚本状态")
    private CodeState state;

    @ApiModelProperty("代码脚本是否激活")
    private Byte isActivate;

    @ApiModelProperty("删除")
    private Byte isDelete;


    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createtime = null;
    @ApiModelProperty("上次更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatetime = null;


    public Task() {
    }

    //cronExpression constructor

    public Task(Integer taskId, String taskName, String cronExpression, TaskType taskType, String remark) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.cronExpression = cronExpression;
        this.taskType = taskType;
        this.remark = remark;
    }

    //timeExpression constructor

    public Task(Integer taskId, String taskName, Date timeExpression, TaskType taskType, String remark) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.timeExpression = timeExpression;
        this.taskType = taskType;
        this.remark = remark;
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

    public TaskType getType() {
        return taskType;
    }

    public void setType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getCodeScript() {
        return codeScript;
    }

    public void setCodeScript(String codeScript) {
        this.codeScript = codeScript;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public CodeState getState() {
        return state;
    }

    public void setState(CodeState state) {
        this.state = state;
    }

    public Byte getIsActivate() {
        return isActivate;
    }

    public void setIsActivate(Byte isActivate) {
        this.isActivate = isActivate;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", timeExpression=" + timeExpression +
                ", remark='" + remark + '\'' +
                ", taskType=" + taskType +
                ", codeScript='" + codeScript + '\'' +
                ", version=" + version +
                ", state=" + state +
                ", isActivate=" + isActivate +
                ", isDelete=" + isDelete +
                ", createtime=" + createtime +
                ", updatetime=" + updatetime +
                '}';
    }
}

