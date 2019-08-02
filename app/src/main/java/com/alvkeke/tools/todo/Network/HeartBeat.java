package com.alvkeke.tools.todo.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.alvkeke.tools.todo.Network.Constants.*;

public class HeartBeat {

    public static final int HEART_BEAT_DEFAULT_BREAK_TIME = 30000;

    int netkey;

    InetAddress address;
    int port;

    boolean keepBeat;

    Thread heartBeatThread;

    int heartBreaktime;

    public HeartBeat(int netkey, int heartBreaktime){

        this.netkey = netkey;
        keepBeat = true;
        this.heartBreaktime = heartBreaktime;
    }

    public boolean setAddress(String ip, int port){

        this.port = port;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void start(){

        heartBeatThread = new Thread(new HeartBeatRunnable());
        heartBeatThread.start();
    }

    public void stop(){
        keepBeat = false;
    }

    public boolean isThreadAlive(){
        return heartBeatThread.isAlive();
    }

    class HeartBeatRunnable implements Runnable {

        @Override
        public void run() {

            try {
                DatagramSocket socket = new DatagramSocket();
                String sSend = COMMAND_HEART_BEAT + String.valueOf(netkey);
                DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

                while (keepBeat){

                    //try block in the loop, can avoid the thread suddenly close while error occur
                    //发送数据的try代码块应放在循环内,这样可以避免线程在发生错误是突然中断
                    try {
                        socket.send(packet);
                        Thread.sleep(heartBreaktime);    //time break : 80s, server should check the client online each 100s
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                socket.close();
            } catch (SocketException e) {
                e.printStackTrace();
            }


        }
    }

}
