package com.gsyong.ny.nyapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2017-11-23.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="ny.db";//数据库名称 git 成功~
    private static final int SCHEMA_VERSION=1;//版本号,则是升级之后的,升级方法请看onUpgrade方法里面的判断

    public DBHelper(Context context) {//构造函数,接收上下文作为参数,直接调用的父类的构造函数
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建入库单表
        db.execSQL("CREATE TABLE dt_ruku_list (id INTEGER PRIMARY KEY,uid TEXT, addtime datetime default (datetime('now', 'localtime')) , bianhao TEXT, qiyeid INTEGER, "+
                "shuliang INTEGER, qystaff TEXT,ylint1 INTEGER,ylint2 INTEGER,ylint3 INTEGER,ylint4 INTEGER,ylstr1 TEXT,ylstr2 TEXT,ylstr3 TEXT,ylstr4 TEXT,isserver INTEGER);");
        Log.d("创建入库单表","OK");
        //创建入库单商品信息表
        db.execSQL("CREATE TABLE dt_ruku_cp (uid TEXT, listUid TEXT, daima TEXT, cpuid TEXT,ylint1 INTEGER,ylint2 INTEGER,ylint3 INTEGER,ylstr1 TEXT,ylstr2 TEXT,ylstr3 TEXT,"+
                "isserver INTEGER);");
        Log.d("创建入库单商品信息表","OK");
        //创建商品信息表
        db.execSQL("CREATE TABLE dt_chanpin (uid TEXT, cpmingzi TEXT, cpdaima TEXT, cpchangjia TEXT,"+
                "ylint1 INTEGER,ylint2 INTEGER,ylint3 INTEGER,ylstr1 TEXT,ylstr2 TEXT,ylstr3 TEXT);");
        Log.d("创建商品信息表","OK");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        if (oldVersion==1 && newVersion==2) {
//            //升级判断,如果再升级就要再加两个判断,从1到3,从2到3
//            db.execSQL("ALTER TABLE restaurants ADD phone TEXT;");
//        }
    }
}
