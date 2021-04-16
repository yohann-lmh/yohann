package com.example.game.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import com.example.game.AddDataActivity;
import com.example.game.GameActivity;
import com.example.game.R;
import com.example.game.controller.LogicController;
import com.example.game.util.Config;

public class MapModel {

    //初始化游戏区域的高宽
    Integer mWidth,mHeight;

    //地图画笔
    public Paint mapPaint;
    //初始化辅助线画笔
    public Paint linePaint;
    //状态画笔
    public Paint statePaint;

    //地图
    public boolean[][] maps;
    //方块大小
    public Integer boxSize;

    private LogicController logicController;

    public Context context;

    private GameActivity gameActivity;

    //是否执行添加排行榜
    private boolean isAdd = false;



    public MapModel(Integer boxSize, Integer mWidth, Integer mHeight, Context context,boolean[][] maps){
        //地图辅助线画笔
        mapPaint = new Paint();
        mapPaint.setColor(0x50000000);
        //一般都会抗锯齿打开
        mapPaint.setAntiAlias(true);
        if (maps == null){
            this.maps = new boolean[Config.mapX][Config.mapY];
        }else {
            this.maps = maps;
        }
        //初始化辅助线画笔
        linePaint = new Paint();
        linePaint.setColor(0xff666666);
        //一般都会抗锯齿打开
        linePaint.setAntiAlias(true);

        //状态辅助线画笔
        statePaint = new Paint();
        statePaint.setColor(0xffFF0033);
        //一般都会抗锯齿打开
        statePaint.setAntiAlias(true);
        statePaint.setTextSize(mWidth/6);
        this.boxSize = boxSize;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.context = context;
    }

    //绘制地图
    public void drawMap(Canvas canvas){
        //绘制堆积地图
        for (int x = 0; x < maps.length;x++){
            for (int y = 0;y < maps[x].length; y++){
                if (maps[x][y]==true){
                    canvas.drawRect(x*boxSize,y*boxSize,x*boxSize+boxSize,y*boxSize+boxSize,mapPaint);
                }
            }
        }
    }

    //地图辅助线
    public void drawLines(Canvas canvas){
        //画地图辅助线 垂直竖线
        for (int x = 0;x < maps.length;x++){
            canvas.drawLine(x*boxSize,0 ,x*boxSize ,mHeight,linePaint);
        }
        //水平线
        for (int y = 0;y < maps[0].length;y++){
            canvas.drawLine(0,y*boxSize, mHeight,y*boxSize,linePaint);
        }
    }

    //绘制游戏状态
    public void drawIsPause(Canvas canvas,boolean isPause,boolean isOver){
        //画暂停状态
        if (isPause && !isOver){
            canvas.drawText((String) context.getText(R.string.game_pause),mWidth/2-statePaint.measureText((String) context.getText(R.string.game_pause))/2,mHeight/2,statePaint);
        }
    }

    public void drawIsOver(Canvas canvas, boolean isOver, final Integer score){
        //画游戏结束状态
        if (isOver){
            canvas.drawText((String) context.getText(R.string.game_over),mWidth/2-statePaint.measureText((String) context.getText(R.string.game_over))/2,mHeight/2,statePaint);
            //游戏结束之后判断分数是否到达进入排行榜的标准并添加到数据库
            //如果未执行则执行，反之则不执行（限制只可执行一次）
            if (!isAdd){
                if (score>=50){
                    Intent intent = new Intent(context, AddDataActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("score",score);
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity(intent);
                }
                isAdd = true;
            }
        }else {
            isAdd = false;
        }

    }

    /*消行处理*/
    public int reMove(){
        //消行的数量
        int line =0;
        //从下往上遍历
        for (int y = maps[0].length-1; y > 0;y--){
            //消行判断
            if (checkLine(y)){
                //执行消行
                deleteLine(y);
                //从消掉的那一行开始重新遍历
                y++;
                line++;
            }
        }
        return line;
    }

    /*执行消行*/
    public void deleteLine(int dy){
        for(;dy>0; dy--){
            for(int x=0;x<maps.length;x++){
                maps[x][dy]=maps[x][dy-1];
            }
        }
    }

    /*消行判断*/
    private boolean checkLine(int y){
        for (int x = 0;x < maps.length; x++){
            //如果有一个不为true则该行不能被消除
            if (!maps[x][y])
                return false;
        }
        //一行每一个都等于true才进行消行处理
        return true;
    }
}
