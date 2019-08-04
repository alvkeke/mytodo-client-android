package com.alvkeke.tools.todo.Network;

import com.alvkeke.tools.todo.MainFeatures.Project;
import com.alvkeke.tools.todo.MainFeatures.TaskItem;

import static com.alvkeke.tools.todo.Network.Constants.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class Synchronizer {

    public final static int FAILED_TYPE_SERVER_TIMEOUT = 1;
    public final static int FAILED_TYPE_SERVER_DENIED = 2;
    public final static int FAILED_TYPE_IO_ERROR = 3;
    public final static int FAILED_TYPE_SOCKET_ERROR = 4;
    public final static int FAILED_TYPE_MISSING_DATA = 5;

    private volatile DatagramSocket socket;
    private int netkey;
    private SyncCallback callback;

    private InetAddress address;
    private int port;

//    private SparseArray<String> confirmMap;

    private ArrayList<Integer> confirmMap;

    /**
     * usage:
     *      Synchronizer syncer = new Synchronizer()
     *      syncer.setAddress(... , ...)
     *      syncer.sync()
     *
     *
     * process:
     *      new
     *      set address
     *      pushData
     *          new socket
     *          new push data thread
     *              push data end:  success(callback, continueSync); failed(callback)
     *              pullData
     *                  new pull data thread
     *                      pull data end: success(callback); failed(callback)
     */

    public Synchronizer(SyncCallback callback, int netkey){
        this.callback = callback;
        this.netkey = netkey;

//        confirmMap = new SparseArray<>();
        confirmMap = new ArrayList<>();

    }

    public void setAddress(String ip, int port){
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        this.port = port;
    }

    public void sync(final ArrayList<Project> projects){
        pushData(projects);
    }

    private void pushData(final ArrayList<Project> projects){

        if(address == null){
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new DatagramSocket();
                    socket.setSoTimeout(SOCKET_SO_TIMEOUT);

                    new Thread(new RecvCheckRunnable()).start();
                    new Thread(new SendDataRunnable(projects)).start();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void pullData(){

        if(address == null || socket == null){
            callback.syncDataFailed(FAILED_TYPE_SOCKET_ERROR);
            return;
        }

        new Thread(new RecvDataRunnable()).start();
        new Thread(new SendPullRequest()).start();

    }

    class RecvDataRunnable implements Runnable{

        @Override
        public void run() {

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            byte cmd;
            String msg, sSend;

            ArrayList<Project> projects = new ArrayList<>();
            ArrayList<TaskItem> taskItems = new ArrayList<>();

            long proId;
            long taskId;
            String proName;
            String taskContent;
            int proColor;
            int taskLevel;
            long taskTime;
            boolean isTaskFinished;

            long lastModifyTime;

            while (!socket.isClosed()){

                Arrays.fill(buf, (byte)0);
                try {
                    packet.setData(buf);
                    socket.receive(packet);
                    cmd = packet.getData()[0];
                    msg = new String(packet.getData()).substring(1).trim();

                    boolean breakLoop = false;

                    switch (cmd) {
                        case COMMAND_SEND_DATA_BEGIN:
                            sSend = COMMAND_CONFIRM_SEND_BEGIN + String.valueOf(netkey);
                            packet.setData(sSend.getBytes());
                            socket.send(packet);
                            break;
                        case COMMAND_SEND_DATA_PROJS:
                            String[] projInfo = msg.split("\\|");
                            if(projInfo.length < 6) break;

                            proId = Long.valueOf(projInfo[2]);
                            proName = projInfo[3];
                            proColor = Integer.valueOf(projInfo[4]);
                            lastModifyTime = Long.valueOf(projInfo[5]);
                            Project p = new Project(proId, proName, proColor);
                            p.setLastModifyTime(lastModifyTime);

                            sSend = COMMAND_CONFIRM_DATA + String.valueOf(netkey) +"|"+ projInfo[1];
                            packet.setData(sSend.getBytes());
                            socket.send(packet);

                            projects.add(p);

                            break;
                        case COMMAND_SEND_DATA_TASKS:
                            String[] taskInfo = msg.split("\\|");
                            if(taskInfo.length < 9) break;

                            taskId = Long.valueOf(taskInfo[2]);
                            proId = Long.valueOf(taskInfo[3]);
                            taskContent = taskInfo[4];
                            taskTime = Long.valueOf(taskInfo[5]);
                            taskLevel = Integer.valueOf(taskInfo[6]);
                            isTaskFinished = Boolean.valueOf(taskInfo[7]);
                            lastModifyTime = Long.valueOf(taskInfo[8]);
                            TaskItem t = new TaskItem(proId, taskId, taskContent, taskTime, taskLevel);
                            t.setLastModifyTime(lastModifyTime);
                            if (isTaskFinished)
                                t.finish();
                            else
                                t.unFinish();

                            sSend = COMMAND_CONFIRM_DATA + String.valueOf(netkey) +"|"+ taskInfo[1];
                            packet.setData(sSend.getBytes());
                            socket.send(packet);

                            taskItems.add(t);

                            break;
                        case COMMAND_SEND_DATA_END:
                            sSend = COMMAND_CONFIRM_SEND_END + String.valueOf(netkey);
                            packet.setData(sSend.getBytes());
                            socket.send(packet);
//                            callback.syncDataSuccess(projects);

                            //等待服务器6s, 判断数据是否完成发送
                            Arrays.fill(buf, (byte)0);
                            socket.receive(packet);
                            if(packet.getData()[0] == COMMAND_RESEND_NEED){
//                                callback.syncDataFailed(FAILED_TYPE_MISSING_DATA);
                                projects.clear();
                                pullData();
                            }else if(packet.getData()[0] == COMMAND_RESEND_NO_NEED){
                                callback.syncDataSuccess(projects, taskItems);
                            }

                            socket.close();
                            breakLoop = true;
                            break;
                    }
                    if(breakLoop){
                        break;
                    }
                } catch (SocketException e){
                    callback.syncDataFailed(FAILED_TYPE_SOCKET_ERROR);
                    e.printStackTrace();
                    break;
                } catch (SocketTimeoutException e){
                    callback.syncDataFailed(FAILED_TYPE_SERVER_TIMEOUT);
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    callback.syncDataFailed(FAILED_TYPE_IO_ERROR);
                    e.printStackTrace();
                    break;
                }


            }

        }
    }

    class RecvCheckRunnable implements Runnable {

        @Override
        public void run() {

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            byte cmd;
            String sid;
            while (!socket.isClosed()){
                Arrays.fill(buf, (byte)0);
                try {
                    packet.setData(buf);
                    socket.receive(packet);
                    cmd = packet.getData()[0];
                    sid = new String(packet.getData()).substring(1).trim();
                    if (cmd == COMMAND_CONFIRM_SEND_END) {
                        checkConfirmMap();
                        break;
                    } else if (cmd == COMMAND_CONFIRM_SEND_BEGIN) {
                        callback.syncDataBegin();
                    } else if (cmd == COMMAND_CONFIRM_DATA) {
                        delConfirmDataId(Integer.valueOf(sid));
                    }
                } catch (SocketTimeoutException e){
                    callback.syncDataFailed(FAILED_TYPE_SERVER_TIMEOUT);
                    e.printStackTrace();
                    break;
                } catch (IOException e) {
                    callback.syncDataFailed(FAILED_TYPE_IO_ERROR);
                    e.printStackTrace();
                    break;
                }

            }
        }
    }

    class SendDataRunnable implements Runnable{

        ArrayList<Project> projects;
        SendDataRunnable(ArrayList<Project> projects){
            this.projects = projects;
        }

        @Override
        public void run() {
            String sSend = COMMAND_SEND_DATA_BEGIN + String.valueOf(netkey);
            DatagramPacket packet;
            try {

                packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length,
                        address, port);

                socket.send(packet);

                int dataId = 0;
                int loopTime = 0;
                for (Project p : projects){

                    System.out.println(loopTime++);
                    sSend = COMMAND_SEND_DATA_PROJS + String.valueOf(netkey) +"|"+ dataId +"|"+ p.getId() +"|"+
                            p.getName() +"|"+ p.getColor() +"|"+ p.getLastModifyTime();
                    packet.setData(sSend.getBytes());

                    socket.send(packet);

                    addConfirmDataId(dataId++);

                    for (TaskItem e : p.getTaskList()){
                        sSend = COMMAND_SEND_DATA_TASKS + String.valueOf(netkey) +"|"+ dataId +"|" + e.getId() +"|"+
                                e.getProId() +"|"+ e.getTaskContent() +"|"+ e.getTime() +"|"+
                                e.getLevel() +"|"+ e.isFinished() +"|"+ e.getLastModifyTime();
                        packet.setData(sSend.getBytes());

                        socket.send(packet);

                        addConfirmDataId(dataId++);

                    }
                }

                sSend = COMMAND_SEND_DATA_END + String.valueOf(netkey);
                packet.setData(sSend.getBytes());
                socket.send(packet);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class SendPullRequest implements Runnable{

        @Override
        public void run() {

            String sSend = COMMAND_GET_DATA + String.valueOf(netkey);
            DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

            try {
                int i;
                for (i = 0; i<2147483647; i++){
                    if(socket != null) break;
                }
                if(i == 2147483647) {
                    callback.syncDataFailed(FAILED_TYPE_SOCKET_ERROR);
                    return;
                }

                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void continueSync(){
        pullData();
    }

    private void addConfirmDataId(int dataId){
        confirmMap.add(dataId);
    }

    private void delConfirmDataId(int dataId) {
        confirmMap.remove(Integer.valueOf(dataId));
    }

    private void checkConfirmMap(){
        if(confirmMap.isEmpty()){
            callback.pushDataSuccess();
            continueSync();
        }else{
            callback.syncDataFailed(FAILED_TYPE_MISSING_DATA);
        }
    }

}
