package com.example.game.database;

import android.graphics.Paint;
import android.graphics.Point;

public class DataStorage {
    //地图
    private boolean[][] maps;

    //下一块方块
    public Point[] boxsNext;

    //方块
    public Point[] boxs;

    //当前分数
    public  int score;

    //初始化计时
    private int seconds;

    //方块的类型
    public Integer boxType;

    //下一个方块的类型
    public Integer boxNextType;

    //声明方块的画笔
    public int color;

    public int getColor(){
        return color;
    }

    public Integer getBoxNextType(){return boxNextType;}

    public Integer getBoxType(){
        return boxType;
    }

    public boolean[][] getMaps() {
        return maps;
    }

    public Point[] getBoxsNext() {
        return boxsNext;
    }

    public Point[] getBoxs() {
        return boxs;
    }

    public int getScore() {
        return score;
    }

    public int getSeconds() {
        return seconds;
    }

    public DataStorage(boolean[][] maps, Point[] boxsNext,
                       Point[] boxs, int score,
                       int seconds,Integer boxType,int color,Integer boxNextType) {
        this.maps = maps;
        this.boxsNext = boxsNext;
        this.boxs = boxs;
        this.score = score;
        this.seconds = seconds;
        this.boxType = boxType;
        this.color = color;
        this.boxNextType = boxNextType;
    }
}
