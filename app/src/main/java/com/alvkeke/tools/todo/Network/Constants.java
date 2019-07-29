package com.alvkeke.tools.todo.Network;

public class Constants {
    public final static String SERVER_IP = "192.168.1.13";
    public final static int SERVER_PORT = 9999;
    public final static int LOCAL_PORT = 8888;

    public final static char COMMAND_LOGIN = 0;
    //COMMAND_LOGIN + username + ":" + password
    public final static char COMMAND_LOGIN_SUCCESS = 1;
    //COMMAND_LOGIN_SUCCESS + netkey
    public final static char COMMAND_LOGIN_FAILED = 2;
    //COMMAND_LOGIN_FAILED

    public final static char COMMAND_LOGOUT = 3;
    //COMMAND_LOGOUT + netkey
    public final static char COMMAND_LOGOUT_SUCCESS = 4;
    //COMMAND_LOGOUT_SUCCESS + netkey

    public final static char COMMAND_SEND_DATA_BEGIN = 5;
    //COMMAND_SEND_DATA_BEGIN + netkey
    public final static char COMMAND_CONFIRM_SEND_BEGIN = 11;
    public final static char COMMAND_SEND_DATA_PROJS = 6;
    public final static char COMMAND_SEND_DATA_TASKS = 7;
    public final static char COMMAND_CONFIRM_DATA = 12;
    //COMMAND_SEND_DATA_PROJS/TASKS + netkey + data(split with :)
    public final static char COMMAND_SEND_DATA_END = 8;
    public final static char COMMAND_CONFIRM_SEND_END = 13;
    //COMMAND_SEND_DATA_END + netkey

    public final static char COMMAND_GET_DATA = 9;
    //COMMAND_GET_DATA + netkey

    public final static char COMMAND_HEART_BEAT = 10;
    //COMMAND_HEART_BEAT + netkey


    public final static int LOGIN_FAILED_SERVER_DENIED = 1;
    public final static int LOGIN_FAILED_SERVER_TIMEOUT = 2;
}
