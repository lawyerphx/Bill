package com.example.fran.phBill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private TextView account;
    private EditText password;
    private SharedPreferences setting;
    private String account_s, password_s, pwd;

    public static final String EXTRA_MESSAGE = "com.example.fran.phBill.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 测试外部连接
        AndPermission.with(getParent()).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) { /***System.out.println("------- grant Get -------");**/
                            } })
                .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) { System.out.println("------- Wanning : Permission Error -------"); } });
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        setting = getSharedPreferences("settings", MODE_PRIVATE);
        pwd = setting.getString("pwd", null);
        if(pwd == null) Signup();
        else System.out.println("-------- Error:pwd is not null, check the thread --------");
        account_s = setting.getString("account", "");
        password_s = setting.getString("password", "");
        pwd = setting.getString("pwd", "");
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        if(setting.getBoolean("autologin", false)) Login();
        Button login = (Button) findViewById(R.id.login);
        account.setText(account_s);
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        account_s = account.getText().toString();
                        password_s = password.getText().toString();
                        Login();
                    }
                }
        );
    }

    //尝试比对密码，进行登陆
    public void Login() {
        if(password_s.equals(pwd)) {
            setting.edit().putString("account", account_s).commit();
            setting.edit().putString("password", password_s).commit();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
    }
    //注册
    public void Signup() {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }
}
