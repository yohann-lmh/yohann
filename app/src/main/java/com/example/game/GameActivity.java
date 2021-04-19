package com.example.game;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.game.controller.LogicController;
import com.example.game.database.DataStorage;
import com.example.game.database.MyDBHelper;
import com.example.game.database.RankingList;
import com.example.game.util.Config;
import com.example.game.util.DataFileUtil;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class GameActivity extends Activity implements View.OnClickListener {

    private boolean isStart;

    //view里面没有变量，没有数据
    //声明一个游戏区域控件
    private View view;

    //下一块预览控件
    public View nextPreview;

    public DataFileUtil dataFileUtil;


    public Button pause;

    //声明逻辑控制器
     public LogicController logicController;

     //当前分数的控件
    public TextView mTvCurrentScore;

    //最高分数的控件
    public TextView mTvTopScore ,mTvtimer;

    private List<RankingList> lists;

    //与数据库操作相关的成员变量
    public MyDBHelper myDBHelper;//数据库帮助工具类

    public MyHandler handler = new MyHandler(Looper.myLooper(),this);//获取Looper并传递

        //新的handler类要声明成静态类
     static class MyHandler extends Handler{
            WeakReference<GameActivity> gactivity;

            //构造函数，传来的是外部类的this
            public MyHandler(@NonNull Looper looper,GameActivity activity){
                super(looper);//调用父类的显式指明的构造函数
                gactivity = new WeakReference<GameActivity>(activity);
            }

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                GameActivity activity=gactivity.get();
                if(activity == null)
                    return ;
                String type = (String) msg.obj;
                if (type == null)
                    return;
                if (type.equals("invalidate")){
                    //刷新重绘地图
                    activity.view.invalidate();
                    activity.nextPreview.invalidate();
                    //刷新分数
                    activity.mTvCurrentScore.setText(""+activity.logicController.scoreModel.score+"");
                }else if (type.equals("Pause")){
                    activity.pause.setText(R.string.game_pause);
                }else if (type.equals("Continue")){
                    activity.pause.setText(R.string.game_continue);
                }
            }
        }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        mTvtimer = findViewById(R.id.tv_timer);
        pause = findViewById(R.id.btn_pause);
        myDBHelper = new MyDBHelper(this, "rankinglist", null, 1);
        dataFileUtil = new DataFileUtil();
        Bundle bundle = getIntent().getExtras();
        isStart = false;
        if (bundle.getString("message").equals("continue")){
            String data = dataFileUtil.readDataFile("data.txt",this);
            Gson gson = new Gson();
            DataStorage dataStorage = gson.fromJson(data,DataStorage.class);
            Log.d("data",data+"");
            logicController = new LogicController(handler,this,isStart,dataStorage,mTvtimer);
        }else {
            logicController = new LogicController(handler,this,isStart,null,mTvtimer);
        }
        //调用控制器的数据查询方法
        lists = new ArrayList<>();
        lists = logicController.searchTopScore(myDBHelper,this);
        /*初始化视图*/
        initView();
        //获取排行榜信息，得到最高分
        if (lists != null){
            RankingList list = lists.get(0);
            mTvTopScore.setText(list.getScore().toString());
        }else {
            mTvTopScore.setText(0+"");
        }

        /*初始化监听*/
        initListener();
        logicController.isExit = true;
        logicController.startGame();
    }


    /*初始化视图*/
    private void initView(){
        //当前分数
        mTvCurrentScore = findViewById(R.id.tv_current_score);
        //最高分数
        mTvTopScore = findViewById(R.id.tv_max_number);

        //1.得到游戏区域的父容器
        FrameLayout layout = findViewById(R.id.layout_game);
        //2.实例化游戏区域
        view =new View(this){
            //重写游戏区域绘制
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                //绘制
                logicController.draw(canvas);
            }
        };
        //3.设置游戏区域大小
        view.setLayoutParams(new FrameLayout.LayoutParams(Config.mWidth,Config.mHeight));
        //设置背景颜色
        view.setBackgroundColor(0x1000000);
        //动态加载区域间距
        //4.添加到父容器
        layout.addView(view);
        layout.setPadding(Config.Padding,Config.Padding,Config.Padding,Config.Padding);

        //设置信息区域间距
        LinearLayout next = findViewById(R.id.next_preview);
        next.setPadding(0,Config.Padding,Config.Padding,Config.Padding);


        //实例化下一块预览区域
        nextPreview = new View(this){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                logicController.drawNext(canvas);
            }
        };
        //设置预览大小
        nextPreview.setLayoutParams((new FrameLayout.LayoutParams(Config.mWidth/2,Config.mWidth/2)));
        nextPreview.setBackgroundColor(0x20000000);
        //添加的下一块预览父容器
        FrameLayout previewLayout = findViewById(R.id.layout_preview);
        previewLayout.addView(nextPreview);
    }

    /*初始化监听*/
    private void initListener(){
        /*顺序为 左 上 右 下 排行榜 暂停 重新开始*/
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_top).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.btn_bottom).setOnClickListener(this);
        findViewById(R.id.btn_list).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_restart).setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
    }


    /*捕捉点击事件*/
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(View v) {
       logicController.onClick(v.getId());
        //调用重新绘编
        view.invalidate();
        nextPreview.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //后台运行时，将游戏处于暂停状态
        logicController.setBackgroundPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新进入游戏时，游戏继续运行
        logicController.setBackgroundResume();
    }

    @Override
    protected void onDestroy() {
        logicController.isExit = false;
        handler.removeCallbacksAndMessages(null);
        logicController.isStart = false;
        handler.removeCallbacks(logicController.timer);
        super.onDestroy();
    }

    //当突然意外退出或直接退出时，将保存游戏进度，再次进入可继续游戏
    @Override
    protected void onStop() {
        if (!logicController.isOver){
            DataStorage dataStorage = new DataStorage(logicController.mapModel.maps,
                    logicController.boxsModel.boxsNext,
                    logicController.boxsModel.boxs,
                    logicController.scoreModel.score,
                    logicController.seconds,
                    logicController.boxsModel.boxType,
                    logicController.boxsModel.boxPaint.getColor(),
                    logicController.boxsModel.boxNextType);
            Gson gson = new Gson();
            String data = gson.toJson(dataStorage);
            dataFileUtil.saveDataFile(data,"data.txt",this);
        }
        super.onStop();
    }
}
