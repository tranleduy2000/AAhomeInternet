package com.duy.aahomeinternet;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.duy.aahomeinternet.utils.Protocol;

public class DoorControlActivity extends AppCompatActivity {

    private boolean connected = false;
    private Button btnClose, btnOpen;
    private SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_control);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cửa chính");
        getData();
        initView();
    }

    private void initView() {
        btnOpen = (Button) findViewById(R.id.btnOpenDoor);
        btnClose = (Button) findViewById(R.id.btnCloseDoor);
        switchCompat = (SwitchCompat) findViewById(R.id.swAuto);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnClose.setEnabled(isChecked);
                btnOpen.setEnabled(isChecked);
            }
        });
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    boolean isOn = switchCompat.isChecked();
                    String cmd = Protocol.POST + Protocol.SET_AUTO_DOOR + (isOn ? "1" : "0");
              //      socket.sendMessenge(cmd);
                } else {
                    Toast.makeText(DoorControlActivity.this, "Không có kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    String cmd = Protocol.POST + Protocol.OPEN_DOOR;
              //      socket.sendMessenge(cmd);
                } else {
                    Toast.makeText(DoorControlActivity.this, "Không có kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    String cmd = Protocol.POST + Protocol.CLOSE_DOOR;
              //      socket.sendMessenge(cmd);
                } else {
                    Toast.makeText(DoorControlActivity.this, "Không có kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("com/example/tranleduy/aahome/com.duy.aahomeinternet.data");
        String ip = bundle.getString("ip");
        int port = bundle.getInt("port");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
