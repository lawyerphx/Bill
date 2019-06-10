package com.example.fran.phBill.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fran.phBill.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class CountFragment extends MyFragment {
    private SQLiteDatabase db;
    private SmartRefreshLayout smartRefreshLayout;
    private ColumnChartView columnChartView;
    TextView totalincome;
    TextView totalexpend;

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void refresh() {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.fran.phBill/databases/charges.db", null);
        float[] temp1 = new float[32];
        float[] temp2 = new float[32];
        float sum1 = 0, sum2 = 0;
        Cursor cursor = db.query("charge", new String[]{"kind", "cost", "year", "month", "date",}, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            Date date = new Date();
            for (int i = cursor.getCount(); i > 0; i--) {
                switch (cursor.getInt(0)) {
                    case 1:
                        sum1 += cursor.getFloat(1);
                        if(cursor.getInt(2) == date.getYear()+1900 && cursor.getInt(3) == date.getMonth()+1)
                            temp2[cursor.getInt(4)] += cursor.getFloat(1);
                        break;
                    case 0:
                        sum2 += cursor.getFloat(1);
                        if(cursor.getInt(2) == date.getYear()+1900 && cursor.getInt(3) == date.getMonth()+1)
                            temp1[cursor.getInt(4)] += cursor.getFloat(1);
                        break;
                }
                cursor.moveToNext();
            }
        }
        totalincome.setText(""+sum1);
        totalexpend.setText(""+sum2);

        List<Column> columns = new ArrayList<Column>();
        for(int i = 0; i <= 31; i++) {
            List<SubcolumnValue> values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(1, ChartUtils.COLOR_RED).setTarget(temp1[i]));
            values.add(new SubcolumnValue(1, ChartUtils.COLOR_GREEN).setTarget(temp2[i]));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }
        ColumnChartData columnChartData = new ColumnChartData(columns);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("日期");
        axisY.setName("支出|收入");
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);
        columnChartView.startDataAnimation();
        columnChartView.setColumnChartData(columnChartData);
        db.close();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            return;
        }else {
            refresh();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("data");
                if ("refresh".equals(msg)) {
                    refresh();
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_count, container, false);

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

        totalincome = (TextView)view.findViewById(R.id.totalincome);
        totalexpend = (TextView)view.findViewById(R.id.totalexpend);
        columnChartView = (ColumnChartView)view.findViewById(R.id.chart);
        smartRefreshLayout = view.findViewById(R.id.refreshLayout_charge2);
        smartRefreshLayout.setEnableLoadmoreWhenContentNotFull(true);
        smartRefreshLayout.setEnableAutoLoadmore(false);
        smartRefreshLayout.setOnRefreshListener(
                new OnRefreshListener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshlayout) {
                        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.fran.phBill/databases/charges.db", null);
                        refresh();
                    }
                }
        );
        smartRefreshLayout.setOnLoadmoreListener(
                new OnLoadmoreListener() {
                    @Override
                    public void onLoadmore(RefreshLayout refreshlayout) {
                        refreshlayout.finishLoadmore();
                    }
                }
        );
        refresh();
        return view;
    }
}
