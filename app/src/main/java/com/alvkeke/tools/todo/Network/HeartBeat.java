package com.alvkeke.tools.todo.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.alvkeke.tools.todo.Network.Constants.*;

public class HeartBeat {

    public static final int HEART_BEAT_DEFAULT_BREAK_TIME = 30000;

    private HeartBeatCallback callback;
    private int netkey;

    private InetAddress address;
    private int port;

    private boolean keepBeat;

    private Thread heartBeatThread;

    private int heartBreakTime;

    private DatagramSocket socket;


    public HeartBeat(HeartBeatCallback callback, int netkey, int heartBreakTime){

        this.callback = callback;
        this.netkey = netkey;
        keepBeat = true;
        this.heartBreakTime = heartBreakTime;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void setAddress(String ip, int port){

        this.port = port;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public void start(){

        new Thread(new HeartBeatFeedbackRunnable()).start();
        heartBeatThread = new Thread(new HeartBeatRunnable());
        heartBeatThread.start();
    }

    public void stop(){
        keepBeat = false;
        socket.close();
        heartBeatThread.stop();
    }

    public boolean isThreadAlive(){
        return heartBeatThread.isAlive();
    }

    class HeartBeatRunnable implements Runnable {

        @Override
        public void run() {

            String sSend = COMMAND_HEART_BEAT + String.valueOf(netkey);
            DatagramPacket packet = new DatagramPacket(sSend.getBytes(), sSend.getBytes().length, address, port);

            while (keepBeat){

                //try block in the loop, can avoid the thread suddenly close while error occur
                //发送数据的try代码块应放在循环内,这样可以避免线程在发生错误是突然中断
                try {
                    socket.send(packet);
                    Thread.sleep(heartBreakTime);    //time break : 80s, server should check the client online each 100s
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            socket.close();



        }
    }

    class HeartBeatFeedbackRunnable implements Runnable{

        @Override
        public void run() {

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (true) {
                Arrays.fill(buf, (byte)0);
                packet.setData(buf);

                try {
                    socket.receive(packet);

                    if(packet.getData()[0] == COMMAND_YOU_ARE_OFFLINE){
                        stop();
                        callback.offline();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
