package com.alvkeke.tools.todo.MainFeatures;

import java.util.ArrayList;

public class Project {

    private String name;
    private ArrayList<TaskItem> tasks;
    private int color;
    private long Id;

    public Project(long projectId, String name, int color){
        this.name = name;
        this.tasks = new ArrayList<>();
        this.color = color;
        this.Id = projectId;
    }

    public void changeName(String name){
        this.name = name;
    }

    public void changeColor(int Color){this.color = Color;}

    public long getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public void addTask(TaskItem e){
        tasks.add(e);
    }

    public TaskItem getTask(int pos){
        return tasks.get(pos);
    }

    public ArrayList<TaskItem> getTaskList(){
        return tasks;
    }

}
