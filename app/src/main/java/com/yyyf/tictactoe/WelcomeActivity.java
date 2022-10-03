package com.yyyf.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_welcome);

        listener();
    }

    private void listener(){
        findViewById(R.id.localPlay).setOnClickListener(view->{
            Intent intent = new Intent(this, LocalPlayActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.internetPlay).setOnClickListener(view->{
            Intent intent = new Intent(this,RoomListActivity.class);
            startActivity(intent);
        });
    }
}