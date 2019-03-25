package com.alvkeke.tools.todo;

import java.util.ArrayList;
import java.util.Date;

public class Project {

    private String name;
    private ArrayList<TaskItem> tasks;
    private long Id;

    Project(String name){
        this.name = name;
        this.tasks = new ArrayList<>();

        this.Id = new Date().getTime();
    }

    void changeName(String name){
        this.name = name;
    }

    public long getId() {
        return Id;
    }

    public String getName() {
        return name;
    }
}
