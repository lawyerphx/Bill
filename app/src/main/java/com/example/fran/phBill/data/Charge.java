package com.example.fran.phBill.data;


public class Charge {
    public int id;  //id，数据库主键
    public int kind;    //入 or 出分类
    public float cost;  //花费
    public int year;    //年份
    public int month;   //月份
    public int date;    //日
    public String ps;   //附加信息
    public String locate;   //具体地址
    public String img;  //附加图片信息
}
