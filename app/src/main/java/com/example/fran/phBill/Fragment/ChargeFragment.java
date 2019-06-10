package com.example.fran.phBill.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.fran.phBill.controler.ChargeAdapter;
import com.example.fran.phBill.data.Charge;
import com.example.fran.phBill.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.LinkedList;
import java.util.List;


public class ChargeFragment extends MyFragment {

    private LinkedList<Charge> datas = new LinkedList<Charge>();

    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private SQLiteDatabase db;

    private ChargeAdapter chargeAdapter = new ChargeAdapter(R.layout.item_charge, datas);
    private Handler handler;

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void refresh() {
        new RefreshData().execute(new ParamsPack((RefreshLayout) smartRefreshLayout, datas, handler, db));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charge, container, false);

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

        smartRefreshLayout = view.findViewById(R.id.refreshLayout_charge);
        recyclerView = view.findViewById(R.id.recyclerView_charge);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);

        smartRefreshLayout.setEnableLoadmoreWhenContentNotFull(true);
        smartRefreshLayout.setEnableAutoLoadmore(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(chargeAdapter);
        chargeAdapter.openLoadAnimation();
        chargeAdapter.isFirstOnly(false);

        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this.getActivity()).size(16).build());

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        chargeAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        chargeAdapter.setOnItemLongClickListener(
                new BaseQuickAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                        String[] ops = {"删除", "取消"};
                        AlertDialog.Builder listDialog = new AlertDialog.Builder(getActivity());
                        listDialog.setItems(ops,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.fran.phBill/databases/charges.db", null);
                                                db.execSQL("DELETE FROM charge WHERE id=?", new Object[]{chargeAdapter.getItem(position).id});
                                                chargeAdapter.remove(position);
                                                chargeAdapter.notifyDataSetChanged();
                                                break;
                                            case 1:
                                                break;
                                        }
                                    }
                                });
                        listDialog.show();
                        return false;
                    }
                }
        );

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

        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy > 0)
                            floatingActionButton.hide();
                        else if (dy < 0)
                            floatingActionButton.show();
                    }
                }
        );

        floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), AppendActivity.class));
                        refresh();
                    }
                }
        );

        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.fran.phBill/databases/charges.db", null);
        refresh();

        return view;
    }
}

class ParamsPack {

    private RefreshLayout refreshLayout;
    private LinkedList<Charge> datas;
    private Handler handler;
    private SQLiteDatabase db;

    public ParamsPack(RefreshLayout refreshLayout, LinkedList<Charge> datas, Handler handler, SQLiteDatabase db) {
        this.refreshLayout = refreshLayout;
        this.datas = datas;
        this.handler = handler;
        this.db = db;
    }

    public RefreshLayout getRefreshLayout() {
        return this.refreshLayout;
    }

    public LinkedList<Charge> getDatas() {
        return this.datas;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public SQLiteDatabase getDb() {
        return this.db;
    }
}

class RefreshData extends AsyncTask<ParamsPack, Void, Void> {
    @Override
    protected Void doInBackground(ParamsPack... params) {
        RefreshLayout refreshLayout = params[0].getRefreshLayout();
        LinkedList<Charge> datas = params[0].getDatas();
        Handler handler = params[0].getHandler();
        SQLiteDatabase db = params[0].getDb();

        datas.clear();
        db.execSQL("CREATE TABLE IF NOT EXISTS charge(id INTEGER PRIMARY KEY AUTOINCREMENT, kind INTEGER, cost REAL,year INTEGER, month INTEGER, date INTEGER, ps VARCHAR(1000))");
        Cursor cursor = db.query("charge", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = cursor.getCount(); i > 0; i--) {
                Charge temp = new Charge();
                temp.id = cursor.getInt(0);
                temp.kind = cursor.getInt(1);
                temp.cost = cursor.getFloat(2);
                temp.year = cursor.getInt(3);
                temp.month = cursor.getInt(4);
                temp.date = cursor.getInt(5);
                temp.ps = cursor.getString(6);
                datas.addFirst(temp);
                cursor.moveToNext();
            }
        }

        refreshLayout.finishRefresh();
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);

        return null;
    }
}

class LoadData extends AsyncTask<ParamsPack, Void, Void> {
    @Override
    protected Void doInBackground(ParamsPack... params) {
        RefreshLayout refreshLayout = params[0].getRefreshLayout();
        LinkedList<Charge> datas = params[0].getDatas();
        Handler handler = params[0].getHandler();

        refreshLayout.finishLoadmore();
        Message message = new Message();
        message.what = 0;
        handler.sendMessage(message);

        return null;
    }
}
