package com.alvkeke.tools.todo;

import java.util.Date;

public class TaskItem {

    private String content;
    private long time;
    private int level;
    private long proId;
    private long Id;

    TaskItem(int proId, String todo){
        this.proId = proId;
        content = todo;

        this.Id = generateId();
    }

    TaskItem(String todo, long time){
        content = todo;
        this.time = time;
        this.Id = generateId();
    }

    TaskItem(String todo, int level){
        content = todo;
        this.level = level;
        this.Id = generateId();
    }

    TaskItem(String todo, long time, int level){
        content = todo;
        this.time = time;
        this.level = level;
        this.Id = generateId();
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
