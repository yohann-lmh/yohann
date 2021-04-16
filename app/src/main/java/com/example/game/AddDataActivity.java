package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.game.database.MyDBHelper;
import com.example.game.model.ScoreModel;

public class AddDataActivity extends AppCompatActivity {

    public TextView mTvTips;

    public EditText mEtUsername;

    public Button mBtnCancel,mBtnRemaining;

    //与数据库操作相关的成员变量
    public MyDBHelper myDBHelper;//数据库帮助工具类

    /*public SQLiteDatabase db;//数据库类*/

    public ContentValues values;//数据表的一些操作参数

    public static final String mtableName = "rankinglist";//数据库排行榜表名称

    public ScoreModel scoreModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        mTvTips = findViewById(R.id.tv_tips_score);
        mEtUsername = findViewById(R.id.et_username);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnRemaining = findViewById(R.id.btn_remaining);
        myDBHelper = new MyDBHelper(this, "rankinglist", null, 1);
        scoreModel = new ScoreModel();

        final Bundle bundle = getIntent().getExtras();
        final int score = bundle.getInt("score");
        String str = getResources().getString(R.string.add_congratulations);
        String text = String.format(str,""+score+"");
        mTvTips.setText(text);

        mBtnRemaining.setOnClickListener(new View.OnClickListener() {
            SQLiteDatabase db = null;
            @Override
            public void onClick(View v) {
                //SQLiteDatabase对象可以对数据库进行读写操作
                db = myDBHelper.getWritableDatabase();
                values = new ContentValues();
                values.put("name",mEtUsername.getText().toString());
                values.put("score",score);
                db.insert(mtableName,null,values);
                values.clear();
                db.close();
                scoreModel.removeScore();
                finish();//完成 当前页面关闭掉
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//完成 当前页面关闭掉
            }
        });

    }

}
