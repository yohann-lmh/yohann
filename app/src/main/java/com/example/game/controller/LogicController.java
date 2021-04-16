package com.example.game.controller;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.game.GameActivity;
import com.example.game.R;
import com.example.game.RankingListActivity;
import com.example.game.database.DataStorage;
import com.example.game.database.MyDBHelper;
import com.example.game.database.RankingList;
import com.example.game.model.BoxsModel;
import com.example.game.model.MapModel;
import com.example.game.model.ScoreModel;
import com.example.game.util.Config;
import com.example.game.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/*
* 游戏逻辑控制器
* */
public class LogicController {

    //初始化计时
    public int seconds = 0;

    public Handler handler;

    private RankingListActivity rankingListActivity;

    //当前分数
    public  int score;

    //方块大小
    Integer boxSize;
    //下一块方块大小
    public Integer boxNextSize;

    //加分模块
    public ScoreModel scoreModel;
    //地图模型
    public MapModel mapModel;
    //方块模型
    public BoxsModel boxsModel;
    //自动下落线程
    public Thread  downThread;
    //线程是否结束
    public boolean isExit;

    //暂停状态
    public boolean isPause;

    public TextView mTvTimer;

    //游戏结束状态
    public boolean isOver;

    //游戏是否开始状态
    public  boolean isStart = false;


    public static final String mtableName = "rankinglist";//数据库排行榜表名称

    public List<RankingList> lists;

    public Context context;


    public LogicController(Handler handler,Context context,boolean isStart,DataStorage dataStorage,TextView mTvTimer){
        this.handler = handler;
        initData(context.getApplicationContext(),dataStorage);
        this.context = context.getApplicationContext();
        this.isStart = isStart;
        this.mTvTimer = mTvTimer;
        Log.d("st1122",""+isStart);
    }

    public LogicController(){

    }

    /*初始化数据*/
    private void initData(Context context, DataStorage dataStorage){
        //获得屏幕宽度
        int width = getScreeWidth(context);
        //设置屏幕宽度 = 屏幕宽度的2/3
        Config.mWidth = width *2/3;
        //设置高度= 高度的两倍
        Config.mHeight = (width *2/3) * 2;
        //初始化间距 = 屏幕宽度的1/30
        Config.Padding = width/30;
        /*//初始化地图10*20  二维数组表示
        maps = new boolean[10][20];*/
        //初始化方块大小 = 游戏区域宽度/10
        boxSize = Config.mWidth/ Config.mapX;
        //初始化下一块方块的大小
        boxNextSize = (Config.mWidth/2)/6;
        if (dataStorage != null ){
            //实例化地图模型
            mapModel = new MapModel(boxSize, Config.mWidth,Config.mHeight,context,dataStorage.getMaps());
            //实例化方块
            boxsModel = new BoxsModel(boxSize,mapModel,boxNextSize,dataStorage);
            //实例化加分
            scoreModel = new ScoreModel(dataStorage.getScore());
            seconds = dataStorage.getSeconds();
        }else {
            mapModel = new MapModel(boxSize, Config.mWidth,Config.mHeight,context,null);
            boxsModel = new BoxsModel(boxSize,mapModel,boxNextSize,null);
            //实例化加分
            scoreModel = new ScoreModel(0);
        }
    }

    //获得屏幕宽度的方法
    public static  int getScreeWidth(Context context){
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrice = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrice);
        return  outMetrice.widthPixels;
    }

    //控制游戏区域绘制
    public void draw(Canvas canvas){
        //堆积地图绘制
        mapModel.drawMap(canvas);
        //方块绘制
        boxsModel.drawBoxs(canvas);
        //地图辅助线
        mapModel.drawLines(canvas);
        //地图游戏状态
        mapModel.drawIsPause(canvas,isPause,isOver);
        mapModel.drawIsOver(canvas,isOver,scoreModel.score);
    }

    //绘制下一块预览区域
    public void drawNext(Canvas canvas) {
        boxsModel.drawNext(canvas);
    }

    /*开始游戏*/
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startGame(){
        if (isStart)
            return;
        //生成新的方块
        boxsModel.newBoxs();
        handler.post(timer);
        if (downThread == null){
            downThread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    while (isExit){
                        try {
                            isStart = true;
                            Log.d("start",""+isStart);
                            //休眠时间1000毫秒
                            sleep(1200-(scoreModel.score/50)*100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //判断游戏是否处于结束状态
                        //判断是否处于暂停状态
                        if (isPause || isOver)
                            //继续循环
                            continue;
                        //执行一次下落
                        moveBottom();
                        //通知主线程刷新View
                        Message msg = new Message();
                        msg.obj = "invalidate";
                        handler.sendMessage(msg);
                    }
                }
            };
            downThread.start();
        }
    }

   public Runnable timer = new Runnable() {
        @Override
        public void run() {
            int hours = seconds/ 3600;
            int minutes = ( seconds% 3600)/ 60;
            int secs = seconds% 60;
            String time = String.format( "%02d:%02d:%02d",hours,minutes,secs);
            mTvTimer.setText(time);
            if(isStart && !isPause && !isOver){
                seconds++;
            }
            if(isStart){
                Log.d("run","运行计时器");
                handler.postDelayed( this, 1000);
            }
        }
    };

    /*下落*/
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean moveBottom(){
        //1.执行移动成功不做处理
        if (boxsModel.move(0,1))
            return true;
        //2.移动失败，堆积处理
        for (int i = 0;i < boxsModel.boxs.length; i++){
            mapModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y] = true;
        }
        //消行处理
        int line = mapModel.reMove();
        //消行之后加分 规则为 1,1 2,3 3,5 4,7.......
        scoreModel.addScore(line);
        //3.生成新的方块
        boxsModel.newBoxs();
        //4.游戏结束判断
        isOver = checkOver();
        if (isOver){
            handler.removeCallbacks(timer);
        }
        return false;

    }

    /*游戏结束的判断*/
    public boolean checkOver(){
        for (int i = 0;i < boxsModel.boxs.length; i++){
            if (mapModel.maps[boxsModel.boxs[i].x][boxsModel.boxs[i].y]){
                return true;
            }
        }
        return false;
    }

    /*重新开始*/
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void reStart(){
        handler.removeCallbacks(timer);
        //清楚地图
        for (int x = 0; x < mapModel.maps.length;x++){
            for (int y = 0;y < mapModel.maps[x].length; y++){
                mapModel.maps[x][y] = false;
            }
        }
        //重新开始的时候要把暂停和游戏结束状态改为false,并把计时器清零
        boxsModel.boxsNext = null;
        boxsModel.newBoxs();
        isPause = false;
        isOver = false;
        isStart = false;
        seconds = 0;
        isExit = true;
        //分数清零
        scoreModel.removeScore();
        startGame();
    }

    /*暂停游戏*/
    public void setPause(){
        if (!isStart || isOver){
            return;
        }
        if (isPause){
            isPause = false;
            Message msg = new Message();
            msg.obj = "Pause";
            handler.sendMessage(msg);
        }else {
            isPause =true;
            Message msg = new Message();
            msg.obj = "Continue";
            handler.sendMessage(msg);
        }
    }

    /*后台暂停游戏*/
    public void setBackgroundPause(){
        if (!isStart || isOver || isPause){
            return;
        }
            isPause =true;
            Message msg = new Message();
            msg.obj = "Continue";
            handler.sendMessage(msg);

    }

    /*后台返回游戏*/
    public void setBackgroundResume(){
        if (!isStart || isOver || !isPause){
            return;
        }
        isPause =false;
        Message msg = new Message();
        msg.obj = "Pause";
        handler.sendMessage(msg);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onClick(int id) {
        switch (id){
            //左
            case R.id.btn_left:
                if (isPause || isOver || !isStart)
                    return;
                boxsModel.move(-1,0);
                break;
            //旋转
            case R.id.btn_top:
                if (isPause || isOver || !isStart)
                    return;
                boxsModel.rotate();
                break;
            //右
            case R.id.btn_right:
                if (isPause || isOver || !isStart)
                    return;
                boxsModel.move(1,0);;
                break;
            //下落
            case R.id.btn_bottom:
                if (isPause || isOver || !isStart)
                    return;
                //按下键时执行快速下落
                while (true){
                    //如果下落失败，则跳出循环
                    if (!moveBottom())
                        break;
                }
                break;
            //下
            case R.id.btn_down:
                if (isPause || isOver || !isStart)
                return;
                  boxsModel.move(0,1);
                  break;
            //排行榜
            case R.id.btn_list:
                Intent intent = new Intent(context,RankingListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity(intent);
                break;
            //重新开始
            case R.id.btn_restart:
                if (!isStart)
                    return;
                reStart();
                break;
            //暂停
            case R.id.btn_pause:
                setPause();
                break;
        }
    }

    //查询排名的数据信息
    public List<RankingList> search(MyDBHelper myDBHelper,Context context){
        SQLiteDatabase db = null;
        db = myDBHelper.getReadableDatabase();
        lists = new ArrayList<>();
        //降序查询排行榜
        Cursor cursor = db.query(mtableName,null,null,null,null,null,"score desc","5");
        if (cursor.getCount()==0){
            ToastUtil.showMag(context,"当前未有人上榜");
            cursor.close();
            db.close();
            return null;
        }else {
            while (cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Integer score = cursor.getInt(cursor.getColumnIndex("score"));
                Integer id = cursor.getInt(cursor.getColumnIndex("id"));
                RankingList rankingList = new RankingList(name,score,id);
                lists.add(rankingList);
            }
            cursor.close();
            db.close();
            return lists;
        }
    }

    //查询排名的数据信息
    public List<RankingList> searchTopScore(MyDBHelper myDBHelper,Context context){
        SQLiteDatabase db = null;
        db = myDBHelper.getReadableDatabase();
        lists = new ArrayList<>();
        //降序查询排行榜
        Cursor cursor = db.query(mtableName,null,null,null,null,null,"score desc","1");
        if (cursor.getCount()==0){
            ToastUtil.showMag(context,"无最高分记录");
            cursor.close();
            db.close();
            return null;
        }else {
            while (cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Integer score = cursor.getInt(cursor.getColumnIndex("score"));
                Integer id = cursor.getInt(cursor.getColumnIndex("id"));
                RankingList rankingList = new RankingList(name,score,id);
                lists.add(rankingList);
            }
            cursor.close();
            db.close();
            return lists;
        }
    }

}
