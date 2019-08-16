package com.alvkeke.tools.todo.Noticication;

public class CheckRunnable implements Runnable{

    RunnableCallback callback;

    CheckRunnable(RunnableCallback callback){
        this.callback = callback;

    }

    @Override
    public void run() {
        while (true){
            try {
                callback.doAction();
                Thread.sleep(3600000);  //每隔一个小时执行一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
