package com.alvkeke.tools.todo.MainFeatures;

public interface ProCallBack {
    void createProject(long proId, String name, int color);
    void modifyProject(long proId, String name, int color);
    void deleteProject(long proId);
}
