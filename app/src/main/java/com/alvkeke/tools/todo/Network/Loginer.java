package com.alvkeke.tools.todo.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

import static com.alvkeke.tools.todo.Network.Constants.*;


public class Loginer {

    public final static int LOGIN_FAILED_SERVER_DENIED = 1;
    public final static int LOGIN_FAILED_SERVER_TIMEOUT = 2;

    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress address;
    private int serPort;

    private String username;
    private String password;

    private LoginCallback callback;

    public Loginer(LoginCallback callback, String username, String password){

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.username = username;
        this.password = password;
        this.callback = callback;
    }

    public void setAddress(String ip, int port){
        try {
            address = InetAddress.getByName(ip);
            serPort = port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void login(){

        if(address == null || socket == null) {
            return ;
        }


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strSend = COMMAND_LOGIN + username + "|" + password;
                    byte[] data = strSend.getBytes();

                    packet = new DatagramPacket(data, data.length, address, serPort);
                    socket.send(packet);

//                    data = new byte[1024];
//                    packet.setData(data);
                    Arrays.fill(data, (byte)0);

                    socket.setSoTimeout(5000);
                    socket.receive(packet);

                    byte cmd = packet.getData()[0];

                    if(cmd == COMMAND_LOGIN_SUCCESS){
                        String sId = new String(packet.getData()).substring(1).trim();
                        callback.loginSuccess(Integer.valueOf(sId));
                    }else if(packet.getData()[0] == COMMAND_LOGIN_FAILED){
                        callback.loginFailed(LOGIN_FAILED_SERVER_DENIED);
                    }
                    socket.close();
                } catch (SocketTimeoutException e){
                    callback.loginFailed(LOGIN_FAILED_SERVER_TIMEOUT);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

    }
}
