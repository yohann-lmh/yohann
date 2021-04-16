package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.example.game.adapter.RankingListAdapter;
import com.example.game.controller.LogicController;
import com.example.game.database.MyDBHelper;
import com.example.game.database.RankingList;
import com.example.game.util.ToastUtil;

import java.util.List;

public class RankingListActivity extends AppCompatActivity {

    private ListView mListView;

    private LogicController mLogicController;

    //与数据库操作相关的成员变量
    public MyDBHelper myDBHelper;//数据库帮助工具类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_list);

        mListView = findViewById(R.id.lv_list);
        mLogicController = new LogicController();
        myDBHelper = new MyDBHelper(this, "rankinglist", null, 1);
        List<RankingList> lists = mLogicController.search(myDBHelper,this);
        if (lists != null){
            mListView.setAdapter(new RankingListAdapter(this,lists));
        }else {
            ToastUtil.showMag(this,"当前未有人上榜");
        }

    }

}
