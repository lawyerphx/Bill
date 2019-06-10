package com.example.fran.phBill.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fran.phBill.LoginActivity;
import com.example.fran.phBill.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;

/**
 * Created by Fran on 2018/1/3.
 */

public class SetFragment extends MyFragment {

    private SharedPreferences setting;

    private EditText account;
    private EditText pwd1;
    private EditText pwd2;
    private Switch autologin;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);

        AndPermission.with(getActivity()).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(
                        new Action() {
                            @Override
                            public void onAction(List<String> permissions) {

                            }
                        }
                )
                .onDenied(
                        new Action() {
                            @Override
                            public void onAction(List<String> permissions) {

                            }
                        }
                );

        setting = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        Button edit0 = (Button)view.findViewById(R.id.edit0);
        Button edit1 = (Button)view.findViewById(R.id.edit1);
        Button upload = (Button)view.findViewById(R.id.upload);
        Button download = (Button)view.findViewById(R.id.download);
        account = (EditText)view.findViewById(R.id.account);
        account.setText(setting.getString("account", ""));
        pwd1 = (EditText)view.findViewById(R.id.pwd);
        pwd2 = (EditText)view.findViewById(R.id.pwd2);
        autologin = (Switch)view.findViewById(R.id.autologin);
        autologin.setChecked(setting.getBoolean("autologin", false));
        Button clear = (Button)view.findViewById(R.id.clear);

        edit0.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        setting.edit().putString("account", account.getText().toString()).commit();
                        Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        edit1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pwd1.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                        else if(!pwd1.getText().toString().equals(pwd2.getText().toString()))
                            Toast.makeText(getActivity(), "密码不一致", Toast.LENGTH_SHORT).show();
                        else{
                            setting.edit().putString("pwd", pwd1.getText().toString()).commit();
                            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        upload.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "上传数据成功", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        download.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "同步数据成功", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        autologin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setting.edit().putBoolean("autologin", autologin.isChecked()).commit();
                    }
                }
        );
        clear.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setIcon(R.mipmap.ic_warning);
                        dialog.setTitle("警告");
                        dialog.setMessage("清空数据不可找回");
                        dialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setting.edit().clear().commit();
                                        File db = new File("/data/data/com.example.fran.phBill/databases/charges.db");
                                        db.delete();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        getActivity().finish();
                                    }
                                });
                        dialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        dialog.show();
                    }
                }
        );
        return view;
    }
}
