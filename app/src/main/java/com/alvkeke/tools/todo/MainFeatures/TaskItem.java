package com.alvkeke.tools.todo.MainFeatures;

import android.util.Log;

public class TaskItem {

    private String content;
    private long time;
    private int level;
    private long proId;
    private long Id;


    public TaskItem(long proId, long taskId, String todo, long time, int level){
        this.proId = proId;
        content = todo;
        this.time = time;
        this.level = level;
        this.Id = taskId;
    }

    public void finish(){
        Log.e("task", "task finish:" + content + "[" + Id + "]");
        //todo:finish the feature: finish task.
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

    public void setContent(String content) {
        this.content = content;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setProId(long proId) {
        this.proId = proId;
    }

    public String getTaskContent() {
        return content;
    }

}
