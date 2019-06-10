package com.example.fran.phBill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    private EditText account;
    private EditText password1;
    private EditText password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndPermission.with(getParent()).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(
                        new Action() {
                            @Override
                            public void onAction(List<String> permissions) { }})
                .onDenied(
                        new Action() {
                            @Override
                            public void onAction(List<String> permissions) { }}
                );

        setTitle(R.string.signup);
        setContentView(R.layout.activity_signup);
        account = (EditText)findViewById(R.id.account);
        password1 = (EditText)findViewById(R.id.password);
        password2 = (EditText)findViewById(R.id.password_check);
        Button signup = (Button)findViewById(R.id.signup);
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(account.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
                        else if(password1.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                        else if(!password1.getText().toString().equals(password2.getText().toString()))
                            Toast.makeText(getApplicationContext(), "密码不一致", Toast.LENGTH_SHORT).show();
                        else{
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences setting = getSharedPreferences("settings", MODE_PRIVATE);
                            setting.edit().putString("account", account.getText().toString()).commit();
                            setting.edit().putString("password", password1.getText().toString()).commit();
                            setting.edit().putString("pwd", password2.getText().toString()).commit();
                            setting.edit().putBoolean("autologin", false).commit();
                            //数据仅用于展示
                            File dir=new File("/data/data/com.example.fran.phBill/databases/");
                            if(!dir.exists())  dir.mkdirs();
                            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.fran.phBill/databases/charges.db", null);
                            db.execSQL("CREATE TABLE IF NOT EXISTS charge(id INTEGER PRIMARY KEY AUTOINCREMENT, kind INTEGER, cost REAL,year INTEGER, month INTEGER, date INTEGER, ps VARCHAR(1000))");
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                }
        );
    }
}
