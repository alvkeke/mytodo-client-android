package com.alvkeke.tools.todo.MainFeatures;

import java.util.Date;

public class TaskItem {

    private String content;
    private long time;
    private int level;
    private long proId;
    private long Id;

    public TaskItem(long proId, String todo, long time, int level){
        this.proId = proId;
        content = todo;
        this.time = time;
        this.level = level;
        this.Id = generateId();
    }

    public TaskItem(long proId, long taskId, String todo, long time, int level){
        this.proId = proId;
        content = todo;
        this.time = time;
        this.level = level;
        this.Id = taskId;
    }

    private long generateId(){
        return new Date().getTime();
    }

    public int getLevel() {
        return level;
    }

    public long getId() {
        return Id;
    }

    public long getProId() {
        return proId;
    }

    public long getTime() {
        return time;
    }

    public String getTaskContent() {
        return content;
    }

}
