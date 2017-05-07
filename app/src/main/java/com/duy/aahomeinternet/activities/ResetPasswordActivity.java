package com.duy.aahomeinternet.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duy.aahomeinternet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Duy on 19/7/2016
 */
public class ResetPasswordActivity extends BaseActivity {
    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPass();
            }
        });
    }

    private void resetPass() {
        String email = inputEmail.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog(getString(R.string.loading), false);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgress();
                        if (!task.isComplete()) {
                            showDialog(null, getString(R.string.send_email_fail));
                        } else {
                            showDialog(null, task.getException().getMessage());
                        }
                    }
                });
    }
}
