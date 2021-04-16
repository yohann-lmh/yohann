package com.example.game.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.game.database.DataStorage;
import com.example.game.util.Config;

import java.util.Random;

//方块模型
public class BoxsModel {

    private Integer point = Config.mapX/2;

    //方块
    public Point[] boxs;
    //方块的种类 7种俄罗斯方块
    final Integer TYPE = 7;
    //方块的类型
    public Integer boxType;
    //方块大小
    public Integer boxSize;
    //声明方块的画笔
    public Paint boxPaint;
    //声明下一块方块的画笔
    public Paint boxNextPaint;
    //下一块方块
    public Point[] boxsNext;
    //下一个方块的类型
    public Integer boxNextType;
    //下一块方块大小
    public Integer boxNextSize;

    public DataStorage dataStorage;

    //地图视图
    private MapModel mapModel;

    public BoxsModel(Integer boxSize, MapModel mapModel, Integer boxNextSize, DataStorage dataStorage){
        //方块辅助线画笔
        boxPaint = new Paint();
        //一般都会抗锯齿打开
        boxPaint.setAntiAlias(true);
        boxPaint.setStrokeJoin(Paint.Join.BEVEL);
        Log.d("bbb",boxPaint.getColor()+"");
        this.dataStorage = dataStorage;
        boxNextPaint = new Paint();
        boxNextPaint.setAntiAlias(true);
        boxNextPaint.setStrokeJoin(Paint.Join.BEVEL);
        boxNextPaint.setColor(0xff000000);
        this.boxSize = boxSize;
        this.mapModel = mapModel;
        this.boxNextSize = boxNextSize;
    }

    /*新的方块*/
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void newBoxs(){
        if (dataStorage == null){
            boxPaint();
            Log.d("color",""+boxPaint.getColor());
            //如果下一块等于空
            if (boxNextType==null){
                nextPreView();
            }
            //当前方块 = 下一块
            boxs = boxsNext;
            //当前方块类型 = 下一块方块类型
            boxType = boxNextType;
            //生成下一个方块
            nextPreView();
            Log.d("方块","生成方块"+boxsNext+boxs);
        }else {
            if (boxNextType==null){
                nextPreView();
            }
            boxs = dataStorage.getBoxs();
            boxType = dataStorage.getBoxType();
            boxsNext = dataStorage.getBoxsNext();
            boxPaint.setColor(dataStorage.getColor());
            boxNextType = dataStorage.getBoxNextType();
            dataStorage = null;
        }
    }
    //生成下一块
    public void nextPreView(){
        //生成随机数 作为方块的类型区分
        Random random = new Random();
        boxNextType = random.nextInt(TYPE);
        //当前方块的类型
        switch (boxNextType){
            //田字型
            case 0:
                boxsNext = new Point[]{new Point(point-1,0),new Point(point,0),new Point(point-1,1),new Point(point,1)};
                break;
            //L型
            case 1:
                boxsNext = new Point[]{new Point(point-1,1),new Point(point,0),
                        new Point(point-2,1),new Point(point,1)};
                break;
            //反L
            case 2:
                boxsNext = new Point[]{new Point(point-1,1),new Point(point-2,0),
                        new Point(point-2,1),new Point(point,1)};
                break;
            //N型
            case 3:
                boxsNext = new Point[]{new Point(point-1,1),new Point(point,2),
                        new Point(point-1,0),new Point(point,1)};
                break;
            //反N型
            case 4:
                boxsNext = new Point[]{new Point(point,1),new Point(point-1,2),
                        new Point(point,0),new Point(point-1,1)};
                break;
            //一型
            case 5:
                boxsNext = new Point[]{new Point(4,0),new Point(3,0),
                        new Point(5,0),new Point(6,0)};
                break;
            //T字型
            case 6:
                boxsNext = new Point[]{new Point(point-1,1),new Point(point-1,0),
                        new Point(point-2,1),new Point(point,1)};
                break;
        }
    }

    public void boxPaint(){
        //生成随机数 作为方块的颜色
        Random random = new Random();
        int type = random.nextInt(TYPE);
        switch (type){
            case 0:
                //红
                boxPaint.setColor(0x80FF0000);
                break;
            case 1:
                //橙
                boxPaint.setColor(0x80FF9900);
                break;
            case 2:
                //黄
                boxPaint.setColor(0x80FFFF00);
                break;
            case 3:
                //绿
                boxPaint.setColor(0x8000FF00);
                break;
            case 4:
                //蓝
                boxPaint.setColor(0x8000FFFF);
                break;
            case 5:
                //靛
                boxPaint.setColor(0x809900FF);
                break;
            case 6:
                //紫
                boxPaint.setColor(0x80CC00FF);
                break;
        }
    }

    public void drawBoxs(Canvas canvas){
        Log.d("box","绘制当前方块"+boxs);
        if (boxs!=null){

            //方块绘制
            for (int i = 0;i < boxs.length;i++){
                Log.d("bocpoint",boxPaint.getColor()+"");
                //矩形绘制
                canvas.drawRect(
                        boxs[i].x*boxSize,
                        boxs[i].y*boxSize,
                        boxs[i].x*boxSize+boxSize,
                        boxs[i].y*boxSize+boxSize,boxPaint);
            }
        }
    }

    /*方块移动*/
    public boolean move(int x,int y){
        for (int i =0 ;i<boxs.length;i++){
            //先把方块预移动后的点遍历传入出界判断中
            if (checkBoundary(boxs[i].x + x,boxs[i].y + y)) {
                //false则是出界
                return false;
            }
        }
        for (int i = 0; i < boxs.length; i++) {
            boxs[i].x += x;
            boxs[i].y += y;
        }
        return true;
    }

    /*方块旋转*/
    public boolean rotate(){
        //如果当前方块是田字形，不能旋转
        if (boxType!=null){
            if (boxType == 0){
                return false;
            }
        }

        for(int i=0;i<boxs.length;i++) {
            //将预旋转后的点，先遍历到出界判断中 判断是否出界
            int checkX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int checkY = boxs[i].x - boxs[0].x + boxs[0].y;
            if (checkBoundary(checkX,checkY)){
                //false则是出界
                return false;
            }
        }
        //遍历方块数组，每一个都绕着中心点顺时针旋转90度
        for(int i=0;i<boxs.length;i++) {
            //旋转算法（笛卡尔公式）顺时针旋转90度
            int checkX = -boxs[i].y + boxs[0].y + boxs[0].x;
            int checkY = boxs[i].x - boxs[0].x + boxs[0].y;
            boxs[i].x=checkX;
            boxs[i].y=checkY;
        }
        return true;
    }

    /*边界判断*/
    /*
     * 出界判断
     * 传入x y去判断是否在边界处
     * @param x 方块x坐标
     * @param y 方块y坐标
     * @return true出界 false未出界
     * */
    public boolean checkBoundary(int x,int y){

        return (x < 0 || y < 0 || x > mapModel.maps.length-1 || y > mapModel.maps[0].length-1 || mapModel.maps[x][y]);
    }

    //绘制下一块方块预览
    public void drawNext(Canvas canvas) {
        Log.d("next","绘制下一个预览"+boxsNext);
        if (boxsNext!=null){
            for (int i = 0;i<boxsNext.length;i++){
                //矩形绘制
                canvas.drawRect(
                        (boxsNext[i].x-3)*boxNextSize,
                        (boxsNext[i].y+2)*boxNextSize,
                        (boxsNext[i].x-3)*boxNextSize+boxNextSize,
                        (boxsNext[i].y+2)*boxNextSize+boxNextSize,boxNextPaint);

            }
        }
    }
}
