package com.example.game.database;

import android.content.Intent;

public class RankingList {

    private Integer id;

    private String name;

    private Integer score;

    public String getName() {
        return name;
    }

    public Integer getId(){
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }


    public RankingList(String name, Integer score ,Integer id) {
        this.name = name;
        this.score = score;
        this.id = id;
    }
}
