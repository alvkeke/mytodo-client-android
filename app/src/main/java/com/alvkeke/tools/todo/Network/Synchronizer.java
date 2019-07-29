package com.alvkeke.tools.todo.Network;

import android.content.Context;
import android.util.SparseArray;

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

public class Synchronizer implements SyncCallback{

    private DatagramSocket socket;
    private int netkey;
    private Context context;

    private SparseArray<String> confirmMap;

    public Synchronizer(Context context, int netkey){
        this.context = context;
        this.netkey = netkey;

        confirmMap = new SparseArray<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void pushData(final ArrayList<Project> projects){
        Thread tRecv = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    byte cmd;
                    String sid;

                    socket.setSoTimeout(5000);  //5秒超时

                    do {
                        socket.receive(packet);
                        cmd = packet.getData()[0];
                        sid = new String(packet.getData()).substring(1).trim();
                        if (cmd == COMMAND_CONFIRM_SEND_END) {
                            break;
                        }
                        confirmDataId(Integer.valueOf(sid));

                    } while (true);
                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        Thread tSend = new Thread(new Runnable() {
            @Override
            public void run() {
                String sSend = COMMAND_SEND_DATA_BEGIN + String.valueOf(netkey);
                DatagramPacket packet;
                try {
                    InetAddress address = InetAddress.getByName(SERVER_IP);
                    packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length,
                            address, SERVER_PORT);

                    socket.send(packet);


                    int dataId = 0;
                    for (Project p : projects){
                        sSend = COMMAND_SEND_DATA_PROJS + netkey + dataId +"|"+ p.getId() +"|"+
                                p.getName() +"|"+ p.getColor();
                        packet.setData(sSend.getBytes());

                        socket.send(packet);

                        confirmMap.append(dataId++, sSend);

                        for (TaskItem e : p.getTaskList()){
                            sSend = COMMAND_SEND_DATA_TASKS + netkey + dataId +"|" + e.getProId() +"|"+
                                    e.getId() +"|"+ e.getTaskContent() +"|"+ e.getTime() +"|"+
                                    e.getLevel() +"|"+ e.isFinished();
                            packet.setData(sSend.getBytes());

                            socket.send(packet);

                            confirmMap.append(dataId++, sSend);
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
        });
        tRecv.start();

        tSend.start();

    }

    public ArrayList<Project> pullData(){

        //todo:完成方法
        return null;
    }

    @Override
    public void confirmDataId(int dataId) {

    }
}
