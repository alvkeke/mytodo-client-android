package com.alvkeke.tools.todo.Network;

import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.TaskItem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.alvkeke.tools.todo.Network.Constants.*;

public class NetworkOperator {

    private int netkey;
    private InetAddress address;
    private int port;

    private NetOperatorCallback callback;

    public NetworkOperator(NetOperatorCallback callback, int netkey, String ip, int port){
        this.callback = callback;
        this.netkey = netkey;
        this.port = port;

        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void createProject(final long proId, final String proName, final int proColor){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Project project = new Project(proId, proName, proColor);
                    project.updataLastModifyTime();

                    DatagramSocket socket = new DatagramSocket();
                    String sSend = COMMAND_ADD_PROJECT + String.valueOf(netkey) +"|"+ proId +"|"+
                            proName +"|"+ proColor +"|"+ project.getLastModifyTime();
                    DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

                    socket.send(packet);

                    byte[] buf = new byte[1024];
                    packet.setData(buf);

                    socket.receive(packet);

                    if(packet.getData()[0] == COMMAND_OPERATE_SUCCESS){
                        callback.createProjectSuccess(proId, proName, proColor, project.getLastModifyTime());
                    }else if(packet.getData()[0] == COMMAND_OPERATE_FAILED){
                        callback.createProjectFailed();
                    }

                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteProject(final long proId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    String sSend = COMMAND_DEL_PROJECT + String.valueOf(netkey) +"|"+ proId;
                    DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

                    socket.send(packet);
                    byte[] buf = new byte[1024];
                    packet.setData(buf);

                    socket.receive(packet);

                    if(packet.getData()[0] == COMMAND_OPERATE_SUCCESS){
                        callback.deleteProjectSuccess(proId);
                    }else if(packet.getData()[0] == COMMAND_OPERATE_FAILED){
                        callback.deleteProjectFailed();
                    }

                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void createTask(final long taskId, final long proId, final String todo, final long time, final int level){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TaskItem task = new TaskItem(proId, taskId, todo, time, level);
                    task.unFinish();
                    task.updataLastModifyTime();

                    DatagramSocket socket = new DatagramSocket();
                    String sSend = COMMAND_ADD_TASK + String.valueOf(netkey) +"|"+ proId +"|"+
                            taskId +"|"+ todo +"|"+ time +"|"+
                            level +"|"+ task.isFinished() +"|"+ task.getLastModifyTime();
                    DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);
                    socket.send(packet);

                    byte[] buf = new byte[1024];
                    packet.setData(buf);
                    socket.receive(packet);

                    if(packet.getData()[0] == COMMAND_OPERATE_SUCCESS){
                        callback.createTaskSuccess(taskId, proId, todo, time, level, task.getLastModifyTime());
                    }else if(packet.getData()[0] == COMMAND_OPERATE_FAILED){
                        callback.createTaskFailed();
                    }

                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void deleteTaskList(final ArrayList<TaskItem> tasks){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();

                    for(TaskItem e : tasks) {
                        String sSend = COMMAND_DEL_TASK + String.valueOf(netkey) + "|" + e.getId() + "|" + e.getProId();
                        DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

                        socket.send(packet);
                        byte[] buf = new byte[1024];
                        packet.setData(buf);

                        socket.receive(packet);

                        if (packet.getData()[0] == COMMAND_OPERATE_SUCCESS) {
                            callback.deleteTaskSuccess(e.getId(), e.getProId());
                        } else if (packet.getData()[0] == COMMAND_OPERATE_FAILED) {
                            callback.deleteTaskFailed();
                        }
                    }

                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void deleteTask(final long taskId, final long proId){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    String sSend = COMMAND_DEL_TASK + String.valueOf(netkey) +"|"+ taskId +"|"+ proId;
                    DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

                    socket.send(packet);
                    byte[] buf = new byte[1024];
                    packet.setData(buf);

                    socket.receive(packet);

                    if(packet.getData()[0] == COMMAND_OPERATE_SUCCESS){
                        callback.deleteTaskSuccess(taskId, proId);
                    }else if(packet.getData()[0] == COMMAND_OPERATE_FAILED){
                        callback.deleteTaskFailed();
                    }

                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void modifyProject(final long proId, final String proName, final int proColor){
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    Project project = new Project(proId, proName, proColor);
//                    project.updataLastModifyTime();
//                    //todo:修改此处以及其他类似的位置,保证本地储存的事件和网络储存的时间一致
//                    DatagramSocket socket = new DatagramSocket();
//                    String sSend = COMMAND_EDIT_PROJECT + String.valueOf(netkey) +"|"+ proId +"|"+
//                            proName +"|"+ proColor +"|"+ project.getLastModifyTime();
//                    DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);
//
//                    socket.send(packet);
//
//                    byte[] buf = new byte[1024];
//                    packet.setData(buf);
//
//                    socket.receive(packet);
//
//                    if(packet.getData()[0] == COMMAND_OPERATE_SUCCESS){
//                        callback.createProjectSuccess(proId, proName, proColor, project.getLastModifyTime());
//                    }else if(packet.getData()[0] == COMMAND_OPERATE_FAILED){
//                        callback.createProjectFailed();
//                    }
//
//                    socket.close();
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    public void modifyTask(final long taskId, final long oldProId, final long newProId, final String todo, final long time, final int level, final boolean isFinished){

    }

}
