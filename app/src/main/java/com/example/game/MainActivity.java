package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnStart,mBtnList,mBtnLanguage,mBtnContinue;


    private PopupWindow mPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = findViewById(R.id.btn_start_game);

        mBtnList = findViewById(R.id.btn_list);

        mBtnLanguage = findViewById(R.id.btn_change_language);

        mBtnContinue = findViewById(R.id.btn_continue);

        mBtnContinue.setOnClickListener(this);

        mBtnLanguage.setOnClickListener(this);

        mBtnStart.setOnClickListener(this);

        mBtnList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle bundle;
      switch (v.getId()){
          case R.id.btn_start_game:
               intent = new Intent(MainActivity.this,GameActivity.class);
              bundle = new Bundle();
              bundle.putString("message","start");
              intent.putExtras(bundle);
              startActivity(intent);
              break;
          case R.id.btn_continue:
              intent = new Intent(MainActivity.this,GameActivity.class);
              bundle = new Bundle();
              bundle.putString("message","continue");
              intent.putExtras(bundle);
              startActivity(intent);
              break;
          case R.id.btn_list:
              intent = new Intent(MainActivity.this,RankingListActivity.class);
              startActivity(intent);
              break;
          case R.id.btn_change_language:
              View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_language,null);
              Button mBtnEn = view.findViewById(R.id.btn_en);
              Button mBtnZh = view.findViewById(R.id.btn_zh);
              mPop = new PopupWindow(view,mBtnLanguage.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
              mPop.setOutsideTouchable(true);//点击空白处可以消失掉
              mPop.setFocusable(true);//实现点击出现以及消失效果
              mPop.showAsDropDown(mBtnLanguage,0,-8);
              mBtnEn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mPop.dismiss();
                          Locale.setDefault(Locale.ENGLISH);
                          Configuration configuration = getBaseContext().getResources().getConfiguration();
                          configuration.locale = Locale.ENGLISH;
                          getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
                          recreate();
                  }
              });

              mBtnZh.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      mPop.dismiss();
                          Locale.setDefault(Locale.CHINESE);
                          Configuration configuration = getBaseContext().getResources().getConfiguration();
                          configuration.locale = Locale.CHINESE;
                          getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
                          recreate();
                  }
              });
              break;
      }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
