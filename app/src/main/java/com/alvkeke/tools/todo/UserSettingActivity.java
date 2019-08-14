package com.alvkeke.tools.todo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        setTitle("用户设置");
    }
}
