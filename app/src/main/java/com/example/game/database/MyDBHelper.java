package com.example.game.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    public static  final String createTable = "create table rankinglist ( " +
            "id integer primary key autoincrement," +
            "name varchar(150)," +
            "score integer)";

    public MyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        //对数据库进行命名
        super(context, name, factory, version);
    }

    //只在创建数据库时创建一次表
    @Override
    public void onCreate(SQLiteDatabase db) {
        //姓名和排行榜分数
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
