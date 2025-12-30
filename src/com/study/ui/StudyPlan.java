package com.study.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 学习计划数据类
 * 学号：你的学号 姓名：你的姓名
 */
public class StudyPlan {
    private String planName;
    private String category;
    private String priority;
    private String dailyTime;
    private String studyDays;
    private String difficulty;
    private String createTime;
    private String target;
    private String reminder;
    private String description;
    private String[] tags;
    
    public StudyPlan() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.createTime = sdf.format(new Date());
    }
    
    public StudyPlan(String planName, String category, String priority, 
                    String dailyTime, String studyDays, String difficulty) {
        this();
        this.planName = planName;
        this.category = category;
        this.priority = priority;
        this.dailyTime = dailyTime;
        this.studyDays = studyDays;
        this.difficulty = difficulty;
    }
    
    // Getter和Setter方法
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getDailyTime() { return dailyTime; }
    public void setDailyTime(String dailyTime) { this.dailyTime = dailyTime; }
    
    public String getStudyDays() { return studyDays; }
    public void setStudyDays(String studyDays) { this.studyDays = studyDays; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    @Override
    public String toString() {
        return planName + " [" + category + "] - " + priority + "优先级";
    }
}