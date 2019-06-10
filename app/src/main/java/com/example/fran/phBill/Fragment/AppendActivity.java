package com.example.fran.phBill.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fran.phBill.MainActivity;
import com.example.fran.phBill.R;
import com.example.fran.phBill.network.GetHttpConnectionData;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class AppendActivity extends AppCompatActivity {

    private static final int BAIDU_READ_PHONE_STATE =100;

    private int kind = 0;
    private int year = new Date().getYear() + 1900;
    private int month = new Date().getMonth() + 1;
    private int date = new Date().getDate();
    private String provider;//位置提供器
    private LocationManager locationManager;//位置服务
    private Location location;
    private TextView tv_show;


    private RadioButton kind0, kind1;
    private EditText costText;
    private EditText psText;
    private TextView locText, yeText, moText, daText;

    public void refresh() {
        onCreate(null);
    }

    //刷新控制模块
    private void broadcaster(){
        Intent intent = new Intent("android.intent.action.CART_BROADCAST");
        intent.putExtra("data","refresh");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        sendBroadcast(intent);
    }


    //地址获取模块
    class MyAsyncTask extends AsyncTask {
        String url = null;
        String str = null;
        String address = null;
        public MyAsyncTask(String url){
            this.url = url;
        }
        protected Void doInBackground(Void... params) {
            str = GetHttpConnectionData.getData(url);
            Toast.makeText(AppendActivity.this,str, Toast.LENGTH_SHORT).show();
            return null;
        }
        protected void onPostExecute(Void aVoid) {
            try {
                str = str.replace("renderReverse&&renderReverse","");
                str = str.replace("(","");
                str = str.replace(")","");
                JSONObject jsonObject = new JSONObject(str);
                JSONObject address = jsonObject.getJSONObject("result");
                String city = address.getString("formatted_address");
                String district = address.getString("sematic_description");
                tv_show.setText("当前位置："+city+district);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }
    public void getLocation(Location location) {
        String latitude = location.getLatitude()+"";
        String longitude = location.getLongitude()+"";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=E5Nq0zTLgvpC0V6GijajiE7mglgmFoRS&callback=renderReverse&location="+latitude+","+longitude+"&output=json&pois=0";
        new AppendActivity.MyAsyncTask(url).execute();
    }
    private String judgeProvider(LocationManager locationManager) {
        List prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }else{
            Toast.makeText(AppendActivity.this,"没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    private void permission_add() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(AppendActivity.this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE},
                BAIDU_READ_PHONE_STATE);
    }

    private void LocationWork() {
        Toast.makeText(AppendActivity.this,"开启location", Toast.LENGTH_SHORT).show();
        permission_add();
        provider = judgeProvider(locationManager);
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&                          ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                getLocation(location);
            } else {
                tv_show.setText("暂时无法获得当前位置");
            }
        }
        else tv_show.setText("provider 出现错误");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.pause ++;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_append);
        AndPermission.with(getParent()).permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE)
                .onGranted(
                        new Action() {
                            @Override
                            public void onAction(List<String> permissions) { }})
                .onDenied(
                        new Action() {
                            @Override
                            public void onAction(List<String> permissions) { }});


        kind0 = (RadioButton) findViewById(R.id.kindButton0);
        kind1 = (RadioButton) findViewById(R.id.kindButton1);
        costText = (EditText) findViewById(R.id.cost);
        psText = (EditText) findViewById(R.id.ps);
        tv_show = (TextView) findViewById(R.id.locate);
        yeText = (TextView) findViewById(R.id.year);
        moText = (TextView) findViewById(R.id.month);
        daText = (TextView) findViewById(R.id.date);
        Button confirm = (Button) findViewById(R.id.confirm);
        Button cancel = (Button) findViewById(R.id.cancel);

        LocationWork();

        kind0.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kind = 0;
                        kind1.setChecked(false);
                    }
                }
        );
        kind1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kind = 1;
                        kind0.setChecked(false);
                    }
                }
        );

        confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        float cost;
                        try {
                            cost = Float.valueOf(costText.getText().toString());
                            year = Integer.valueOf(yeText.getText().toString());
                            month = Integer.valueOf(moText.getText().toString());
                            date = Integer.valueOf(daText.getText().toString());
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "账单数目格式错误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String ps = psText.getText().toString();
                        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.fran.phBill/databases/charges.db", null);
                        db.execSQL("INSERT INTO charge VALUES(NULL, ?, ?, ?, ?, ?, ?)", new Object[]{kind, cost, year, month, date, ps});
                        MainActivity.pause --;
                        db.close();
                        broadcaster();
                        finish();
                    }
                }
        );

        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.pause --;
                        broadcaster();
                        finish();
                    }
                }
        );
    }
}
