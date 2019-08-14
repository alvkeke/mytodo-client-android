package com.alvkeke.tools.todo;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import static com.alvkeke.tools.todo.Network.Constants.SERVER_IP;
import static com.alvkeke.tools.todo.Network.Constants.SERVER_PORT;

public class PreferenceActivity extends AppCompatActivity {

    private SharedPreferences setting;
    private EditText etIp;
    private EditText etPort;
    private Switch switchNetworkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        setTitle("首选项");

        etIp = findViewById(R.id.et_ip_address);
        etPort = findViewById(R.id.et_port);
        switchNetworkMode = findViewById(R.id.switch_networkMode);

        setting = getSharedPreferences("preLogin", 0);
        boolean networkMode = setting.getBoolean("networkMode", false);
        String serverIP = setting.getString("serverIP", SERVER_IP);
        int serverPort = setting.getInt("serverPort", SERVER_PORT);

        etIp.setText(serverIP);
        etPort.setText(String.valueOf(serverPort));
        if(!networkMode){
            etIp.setEnabled(false);
            etPort.setEnabled(false);
        }
        switchNetworkMode.setChecked(networkMode);

        switchNetworkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etIp.setEnabled(isChecked);
                etPort.setEnabled(isChecked);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preference_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.preference_menu_item_ok:
                String newIp = etIp.getText().toString();
                int newPort = Integer.parseInt(etPort.getText().toString());
                boolean networkMode = switchNetworkMode.isChecked();
                SharedPreferences.Editor editor = setting.edit();
                editor.putBoolean("networkMode", networkMode);
                editor.putString("serverIP", newIp);
                editor.putInt("serverPort", newPort);
                editor.apply();

                Toast.makeText(getApplicationContext(), "下次启动生效", Toast.LENGTH_SHORT).show();
                PreferenceActivity.this.finish();
                break;
            case R.id.preference_menu_item_cancel:
                PreferenceActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
