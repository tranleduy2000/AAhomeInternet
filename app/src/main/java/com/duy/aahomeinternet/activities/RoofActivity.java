package com.duy.aahomeinternet.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.duy.aahomeinternet.FirebaseHandler;
import com.duy.aahomeinternet.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RoofActivity extends AppCompatActivity {

    private Button btnOpen, btnClose;
    private FirebaseHandler mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roof);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebase = new FirebaseHandler(this);

        initView();
        addEvent();
    }

    private void addEvent() {
        String url = "users/" + mFirebase.getUid() + "/" + "roof";
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(url);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setValue(true);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.setValue(false);
            }
        });
    }

    private void initView() {
        btnOpen = (Button) findViewById(R.id.btn_open);
        btnClose = (Button) findViewById(R.id.btn_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
