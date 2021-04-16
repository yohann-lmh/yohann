package com.example.game.model;

public class ScoreModel {

    //当前分数
    public  int score = 0;

    //最高分数
    public  int maxScore;

    public ScoreModel(int score){
            this.score = score;
        }public ScoreModel(){};


    //根据消行数加分方法
    public void addScore(int line) {
        if (line == 0){
            return;
        }
         score += 2*line-1;
    }

    //分数归零
    public void removeScore(){
        score = 0;
    }

}
