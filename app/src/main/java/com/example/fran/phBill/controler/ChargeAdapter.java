package com.example.fran.phBill.controler;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.fran.phBill.data.Charge;
import com.example.fran.phBill.R;

import java.util.List;

public class ChargeAdapter extends BaseQuickAdapter<Charge, BaseViewHolder> {

    public ChargeAdapter(@LayoutRes int layoutResId, @Nullable List<Charge> datas) {
        super(layoutResId, datas);
    }

    @Override
    protected void convert(BaseViewHolder helper, Charge item) {
        switch (item.kind) {
            case 0:
                helper.setText(R.id.kind, "支出");
                helper.setTextColor(R.id.kind, mContext.getResources().getColor(R.color.red));
                helper.setTextColor(R.id.cost, mContext.getResources().getColor(R.color.red));
                break;
            case 1:
                helper.setText(R.id.kind, "收入");
                helper.setTextColor(R.id.kind, mContext.getResources().getColor(R.color.green));
                helper.setTextColor(R.id.cost, mContext.getResources().getColor(R.color.green));
                break;
        }
        helper.setText(R.id.cost, ""+item.cost);
        helper.setText(R.id.ps, item.ps);
        helper.setText(R.id.time, String.format("%d-%d-%d", item.year, item.month, item.date));
        helper.setText(R.id.locate, item.locate);
    }
}
