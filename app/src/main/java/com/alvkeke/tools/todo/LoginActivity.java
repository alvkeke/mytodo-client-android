package com.alvkeke.tools.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;

    private Button btnLogin;

    private boolean isReLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.login_edit_username);
        etPassword = findViewById(R.id.login_edit_password);
        btnLogin = findViewById(R.id.login_btn_login);

        isReLogin = getIntent().getBooleanExtra("isReLogin", false);

        btnLogin.setOnClickListener(new onBtnLogin());
    }

    @Override
    public void onBackPressed() {
        if(isReLogin){
            Intent intent = new Intent(LoginActivity.this, PreLaunchActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    class onBtnLogin implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(manager != null){
                manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            if(etPassword.getText().toString().length() < 6){
                Toast.makeText(getApplicationContext(), "密码字符个数不得少于6", Toast.LENGTH_SHORT).show();
            }else{
                SharedPreferences setting = getSharedPreferences("preLogin", 0);
                SharedPreferences.Editor editor = setting.edit();

                editor.putString("username", etUsername.getText().toString());
                editor.putString("password", etPassword.getText().toString());
                editor.putBoolean("networkMode", true);

                editor.apply();

                Intent intentLogin = new Intent(LoginActivity.this, PreLaunchActivity.class);
                intentLogin.putExtra("firstLogin", true);
                startActivity(intentLogin);

                setResult(0, intentLogin);
                finish();
            }
        }
    }

}
