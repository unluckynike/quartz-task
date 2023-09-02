package org.example.pojo;

 public class Task {
    private Integer taskId;
    private String taskName;
    private String cronExpression;

    // getters and setters

    public Task() {
    }

    public Task(Integer taskId, String taskName, String cronExpression) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.cronExpression = cronExpression;
    }

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

     public void setCronExpression(String cronExpression) {
         this.cronExpression = cronExpression;
     }

     @Override
     public String toString() {
         return "Task{" +
                 "taskId=" + taskId +
                 ", taskName='" + taskName + '\'' +
                 ", cronExpression='" + cronExpression + '\'' +
                 '}';
     }
 }

