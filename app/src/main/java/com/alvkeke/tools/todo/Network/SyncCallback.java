package com.alvkeke.tools.todo.Network;

import com.alvkeke.tools.todo.MainFeatures.Project;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public interface SyncCallback {
    void pushDataFailed(int FailedType);
    void pushDataSuccess();

    void pullDataFailed(int FailedType);
    void pullDataSuccess(ArrayList<Project> projects);

}
