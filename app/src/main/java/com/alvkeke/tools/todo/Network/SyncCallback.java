package com.alvkeke.tools.todo.Network;

import com.alvkeke.tools.todo.MainFeatures.Project;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public interface SyncCallback {
    void syncDataFailed(int FailedType);

//    void pullDataFailed(int FailedType);

    void pushDataSuccess();
    void syncDataSuccess(ArrayList<Project> projects);

}
