package com.alvkeke.tools.todo.Network;

public interface NetOperatorCallback {
    void createProjectSuccess(long proId, String proName, int proColor, long lastModifyTime);
//    void createProjectFailed();

    void deleteProjectSuccess(long proId);
//    void deleteProjectFailed();

    void createTaskSuccess(long taskId, long proId, String todo, long time, int level, long lastModifyTime);
//    void createTaskFailed();

    void deleteTaskSuccess(long taskId, long proId);
//    void deleteTaskFailed();

    void modifyProjectSuccess(long proId, String proName, int proColor, long lastModifyTime);
//    void modifyProjectFailed();

    void modifyTaskSuccess(long taskId, long oldProId, long newProId, String todo, long time, int level, boolean isFinished, long lastModifyTime);
//    void modifyTaskFailed();

    void operateFailed(int failedOperate, int failedType);
}
