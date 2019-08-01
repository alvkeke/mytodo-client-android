package com.alvkeke.tools.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alvkeke.tools.todo.Network.LoginCallback;
import com.alvkeke.tools.todo.Network.Loginer;

import static com.alvkeke.tools.todo.Network.Constants.SERVER_PORT;

public class LoginActivity extends AppCompatActivity implements LoginCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Intent intent = getIntent();
        String serverIP = intent.getStringExtra("serverIP");
        int port = intent.getIntExtra("serverPort", SERVER_PORT);

    }

    @Override
    public void loginSuccess(int key) {

    }

    @Override
    public void loginFailed(int failedType) {

    }
}
