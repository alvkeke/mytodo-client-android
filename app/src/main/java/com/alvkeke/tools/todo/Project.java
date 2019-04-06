package com.alvkeke.tools.todo;

import java.util.ArrayList;
import java.util.Date;

class Project {

    private String name;
    private ArrayList<TaskItem> tasks;
    private int color;
    private long Id;

    Project(String name, int color){
        this.name = name;
        this.tasks = new ArrayList<>();
        this.color = color;
        this.Id = new Date().getTime();
    }

    void changeName(String name){
        this.name = name;
    }

    long getId() {
        return Id;
    }

    String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    void addTask(TaskItem e){
        tasks.add(e);
    }

    TaskItem getTask(int pos){
        return tasks.get(pos);
    }

    ArrayList<TaskItem> getTaskList(){
        return tasks;
    }

}
