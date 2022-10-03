package com.yyyf.tictactoe;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yyyf.tictactoe.core.Tictactoe;
import com.yyyf.tictactoe.core.util.Board;
import com.yyyf.tictactoe.core.util.Player;
import com.yyyf.tictactoe.core.util.Vector2;

import java.util.concurrent.atomic.AtomicBoolean;

public class LocalPlayActivity extends Activity {
    protected static final String TAG = "play";

    protected ImageView[] p;              //获取board中的图片位置
    protected RelativeLayout background;    //背景

    protected boolean isFirst = true;     //是先手还是后手的回合，为真则当前回合是先手
    protected int count = 0;              //记录下的多少步，9步则和局

    protected Tictactoe tictactoe;        //井字棋游戏
    protected Player playerA,playerB;     //两位玩家
    protected Board board;                //一个棋盘



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_local_play);

        init();
    }

    protected void play(ImageView i, int finalX, int finalY) {
//        boolean isCompu = isComputer();

        try{
            if(!tictactoe.isFinish && count < 9){
                if(isFirst){

                    if(playerA.putAChess(new Vector2(finalX, finalY))){
                        i.setImageResource(Data.firstImg);
                        background.setBackgroundResource(Data.lastBackground);
                        isFirst = false;
                        count++;
                    }
                    Log.i(TAG, "playA: x: " + finalX + ",y: " + finalY);
                    Log.i(TAG, "playA: isWin: " + playerA.isWin);

                }
                else{
                    if( playerB.putAChess(new Vector2(finalX,finalY)) ){
                        i.setImageResource(Data.lastImg);
                        background.setBackgroundResource(Data.firstBackground);
                        isFirst = true;
                        count++;
                    }
                    Log.i(TAG, "playB: x: " + finalX + ",y: " + finalY);
                    Log.i(TAG, "playB: isWin: " + playerB.isWin);
                }
                tictactoe.show();
                tictactoe.Check();
                if(playerA.isWin){
                    whoWin("先手获胜");

                }
                if(playerB.isWin){
                    whoWin("后手获胜");
                }
            }
            else if(!(count < 9)){
                whoWin("平局");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void whoWin(String msg){
        tictactoe.isFinish = true;
        TextView tv = findViewById(R.id.winMenu_tv_whoWin);
        tv.setText(msg);
        findViewById(R.id.localAc_inc_winMenu).setVisibility(View.VISIBLE);

        findViewById(R.id.winMenu_iv_restart).setOnClickListener(
                view->Data.Restart(
                        this,
                        new Intent(this, LocalPlayActivity.class)
                )
        );
        findViewById(R.id.winMenu_iv_returnToHome).setOnClickListener(
                view->Data.ReturnToHome(this)
        );
    }

    protected boolean isComputer(){
        AtomicBoolean result = new AtomicBoolean(false);
        AlertDialog.Builder chose = new AlertDialog.Builder(LocalPlayActivity.this);
        chose.setMessage(Data.chosePlayerOrComputerMsg);

        chose.setPositiveButton("与玩家", (dialog, which) -> {
            result.set(false);
        });

        chose.setNegativeButton("与电脑",(dialog,which)->{
            result.set(true);
        });
        chose.setCancelable(false);
        chose.show();
        return result.get();
    }

    protected void init(){
        findViewById(R.id.localAc_inc_multiplayerInfo).setVisibility(View.INVISIBLE);//隐藏多人时的信息
        //-------------------------------------//初始化玩家与棋盘
        board  = new Board(3,3);

        playerA = new Player(1,board);
        playerB = new Player(-1,board);
        tictactoe = new Tictactoe(playerA,playerB,board);
        //-------------------------------------//获取view
        background = findViewById(R.id.localBackground);
        background.setBackgroundResource(Data.firstBackground);

        p = new ImageView[9];
        int n = 0;
        for(int id : Data.allP){
            p[n++] = findViewById(id);
        }
        //-------------------------------------//注册监听时间
        int x = 0,y = 0;

        for(ImageView i : p){
            Log.i(TAG, "init: x: " + x + ",y: " + y);
            int finalX = x++;
            int finalY = y;
            if(x >= 3){
                y++;
                x = 0;
            }
            i.setOnClickListener(view->play(i,finalX,finalY));
        }

    }
}