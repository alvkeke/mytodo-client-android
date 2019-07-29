package com.alvkeke.tools.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alvkeke.tools.todo.Network.Loginer;
import com.alvkeke.tools.todo.Network.LoginCallback;

import java.net.DatagramSocket;
import java.util.Objects;

import static com.alvkeke.tools.todo.Network.Constants.*;


public class PreLaunchActivity extends AppCompatActivity implements LoginCallback {

    DatagramSocket socket;

    SharedPreferences setting;
    boolean networkMode;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_launch);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //从本地储存中加载用户设置
        setting = getSharedPreferences("preLogin", 0);
        networkMode = setting.getBoolean("networkMode", false);

        networkMode = true;
        //加载登录前设置,判断当前储存模式是否为在线模式
        if(networkMode){    //根据是在线/离线模式加载用户设置
            username = setting.getString("username", "");
            String password = setting.getString("password", "");

            //检查储存的用户信息正确性
            if(username == null || username.isEmpty()){
                SharedPreferences.Editor editor = setting.edit();
                editor.putBoolean("networkMode", false);
                editor.apply();
                Toast.makeText(getApplicationContext(), "用户信息出错，请重新登录。", Toast.LENGTH_LONG).show();
                finish();
            }
            //联网验证,通过interface进行回调
            Loginer loginer = new Loginer(PreLaunchActivity.this, username, password);
            loginer.setAddress(SERVER_IP, SERVER_PORT);
            loginer.login();

        }else{
            username = "localuser";
            Intent MainIntent = new Intent(PreLaunchActivity.this, MainActivity.class);
            MainIntent.putExtra("username", username);
            MainIntent.putExtra("netkey", -1);
            startActivity(MainIntent);
        }

    }

    @Override
    public void loginSuccess(int key) {
        Intent MainIntent = new Intent(PreLaunchActivity.this, MainActivity.class);
        MainIntent.putExtra("netkey", key);
        MainIntent.putExtra("username", username);
        startActivity(MainIntent);

        if(socket != null) {
            socket.close();
        }else{
            Log.e("login", "error:cannot create socket");
        }
        Log.e("login", "success");
    }

    @Override
    public void loginFailed(int failedType) {
        if(socket != null){
            socket.close();
        }else{
            Log.e("login", "error:cannot create socket.");
        }
        switch (failedType){
            case LOGIN_FAILED_SERVER_DENIED:
                Log.e("login", "error:server denied.");
                //todo:弹出登录界面
                break;
            case LOGIN_FAILED_SERVER_TIMEOUT:
                Log.e("login", "error:server timeout.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "服务器请求超时,请重试", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
//        finish();
    }
}