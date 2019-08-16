package com.alvkeke.tools.todo;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.alvkeke.tools.todo.Network.Loginer;
import com.alvkeke.tools.todo.Network.LoginCallback;
import com.alvkeke.tools.todo.Noticication.ReminderService;

import java.util.Objects;

import static com.alvkeke.tools.todo.Network.Constants.*;


public class PreLaunchActivity extends AppCompatActivity implements LoginCallback {

    private SharedPreferences setting;
    private boolean networkMode;
    private String serverIP;
    private int serverPort;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_launch);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String id = "reminder";
            String name = "任务到期提醒";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotifyChannel(id, name, importance);
        }


        //从本地储存中加载用户设置
        setting = getSharedPreferences("preLogin", 0);
        networkMode = setting.getBoolean("networkMode", false);
        serverIP = setting.getString("serverIP", SERVER_IP);
        serverPort = setting.getInt("serverPort", SERVER_PORT);


        boolean appFirstRun = setting.getBoolean("appFirstRun", true);
        if(appFirstRun) {   //建立一个测试用账户,便于调试
            SharedPreferences.Editor editor1 = setting.edit();
            editor1.putString("username", "__user_test__");
            editor1.putString("password", "password");
            editor1.putBoolean("appFirstRun", false);
            editor1.apply();
        }

        //加载登录前设置,判断当前储存模式是否为在线模式
        if(networkMode){    //根据是在线/离线模式加载用户设置
            username = setting.getString("username", "");
            password = setting.getString("password", "");

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
            loginer.setAddress(serverIP, serverPort);
            loginer.login();

        }else{
            username = "localuser";
            Intent MainIntent = new Intent(PreLaunchActivity.this, MainActivity.class);
            MainIntent.putExtra("username", username);
            MainIntent.putExtra("netkey", -1);
            startActivity(MainIntent);
            finish();
        }

    }

    @Override
    public void loginSuccess(int key) {
        Intent MainIntent = new Intent(PreLaunchActivity.this, MainActivity.class);
        MainIntent.putExtra("netkey", key);
        MainIntent.putExtra("username", username);
        MainIntent.putExtra("serverIP", serverIP);
        MainIntent.putExtra("serverPort", serverPort);
        startActivity(MainIntent);

//        Log.e("login", "success");
        finish();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotifyChannel(String channelId, String channelName, int importance){
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    @Override
    public void loginFailed(final int failedType) {

        if(failedType == Loginer.LOGIN_FAILED_SERVER_TIMEOUT && !getIntent().getBooleanExtra("firstLogin", false)){

//            Log.e("login", "error:server timeout.");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "网络连接错误,登录失败", Toast.LENGTH_SHORT).show();
                }
            });
            Intent MainIntent = new Intent(PreLaunchActivity.this, MainActivity.class);
            MainIntent.putExtra("netkey", 0);
            MainIntent.putExtra("username", username);
            MainIntent.putExtra("serverIP", serverIP);
            MainIntent.putExtra("serverPort", serverPort);
            startActivity(MainIntent);

        }else{

            //弹出登录界面

            SharedPreferences.Editor editor = setting.edit();
            editor.putBoolean("networkMode", false);
            editor.apply();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(failedType == Loginer.LOGIN_FAILED_SERVER_DENIED) {
                        Toast.makeText(getApplicationContext(), "账户或密码出错,服务器拒绝登录,请重试", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "服务器未响应,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Intent intent = new Intent(PreLaunchActivity.this, LoginActivity.class);
            intent.putExtra("isReLogin", true);

            startActivity(intent);

        }

        finish();
    }
}
