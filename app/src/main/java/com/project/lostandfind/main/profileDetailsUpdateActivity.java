package com.project.lostandfind.main;

import androidx.appcompat.app.AppCompatActivity;
import com.project.lostandfind.R;
import com.project.lostandfind.Utils.Database;
import com.project.lostandfind.Utils.User;
import com.project.lostandfind.interfaces.AuthCallBack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class profileDetailsUpdateActivity extends AppCompatActivity {
    private Database db;
    private TextInputLayout editAccount_TIL_fullName, editAccount_TIL_phone;
    private MaterialButton editAccount_BTN_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details_update);
        findViews();
        initVars();
    }

    private void initVars() {
        db = new Database();
        db.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onCreateAccountComplete(boolean status, String msg) {

            }

            @Override
            public void updateUserInfoComplete(boolean status, String msg) {
                if(status){
                    Toast.makeText(profileDetailsUpdateActivity.this, "Update success", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(profileDetailsUpdateActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLoginComplete(boolean status, String msg) {

            }

            @Override
            public void fetchUserInfoComplete(User user) {

            }
        });

        Intent intent = getIntent();
        User user = (User) intent.getExtras().get("user");

        editAccount_TIL_phone.getEditText().setText(user.getPhone());
        editAccount_TIL_fullName.getEditText().setText(user.getName());

        editAccount_BTN_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setName(editAccount_TIL_fullName.getEditText().getText().toString());
                user.setPhone(editAccount_TIL_phone.getEditText().getText().toString());
                db.updateUserInfo(user);
            }
        });
    }

    private void findViews() {
        editAccount_TIL_fullName = findViewById(R.id.editAccount_TIL_fullName);
        editAccount_TIL_phone = findViewById(R.id.editAccount_TIL_phone);
        editAccount_BTN_update = findViewById(R.id.editAccount_BTN_update);
    }


}