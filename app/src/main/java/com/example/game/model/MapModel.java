package com.example.game.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.game.GameActivity;
import com.example.game.R;
import com.example.game.controller.LogicController;
import com.example.game.database.MyDBHelper;
import com.example.game.util.Config;
import com.example.game.util.ToastUtil;

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

    //与数据库操作相关的成员变量
    public MyDBHelper myDBHelper;//数据库帮助工具类

    /*public SQLiteDatabase db;//数据库类*/

    public ContentValues values;//数据表的一些操作参数

    public static final String mtableName = "rankinglist";//数据库排行榜表名称

    public ScoreModel scoreModel;



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
                if (score>=1){
                    myDBHelper = new MyDBHelper(context, "rankinglist", null, 1);
                    scoreModel = new ScoreModel();
                    final AlertDialog builder = new AlertDialog.Builder(context).create();
                    View view = LayoutInflater.from(context).inflate(R.layout.layout_add_data,null);
                    final EditText mEtUsername = view.findViewById(R.id.et_username);
                    TextView mTvTips = view.findViewById(R.id.tv_tips_score);
                    Button mBtnCancel = view.findViewById(R.id.btn_cancel);
                    Button mBtnRemaining = view.findViewById(R.id.btn_remaining);
                    String str = context.getResources().getString(R.string.add_congratulations);
                    String text = String.format(str,""+score+"");
                    mTvTips.setText(text);
                    builder.setView(view);
                    builder.show();
                    mBtnRemaining.setOnClickListener(new View.OnClickListener() {
                        SQLiteDatabase db = null;
                        @Override
                        public void onClick(View v) {
                            //SQLiteDatabase对象可以对数据库进行读写操作
                            String name = mEtUsername.getText().toString();
                            if (name.equals("")){
                                ToastUtil.showMag(context,"请输入名称之后添加");
                            }else {
                                db = myDBHelper.getWritableDatabase();
                                values = new ContentValues();
                                values.put("name",name);
                                values.put("score",score);
                                db.insert(mtableName,null,values);
                                values.clear();
                                db.close();
                                scoreModel.removeScore();
                                builder.dismiss();
                            }
                        }
                    });
                    mBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
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
