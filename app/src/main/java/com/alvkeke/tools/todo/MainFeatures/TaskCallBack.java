package com.alvkeke.tools.todo.MainFeatures;

public interface TaskCallBack{
    void addTask(long taskId, long proId, String content, long time, int level);
    void modifyTask(long taskId, long oldProId, long newProId, String content, long time, int level);
    void toggleTaskFinishState(TaskItem taskItem);
    void deleteTask(long taskId, long proId);
}

