package com.yyyf.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.yyyf.tictactoe.core.net.Client;
import com.yyyf.tictactoe.core.net.Tool;
import com.yyyf.tictactoe.core.util.Vector2;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/*
      首先判断玩家是通过创建来到的这里，还是加入来到的这里
      如果是创建，那么提示玩家等待,当有玩家加入，房主来决定是先手后手，然后游戏开始，直到游戏结束，双方结束连接
      如果是加入，                       等待房主...
 */
public class InternetPlayActivity extends LocalPlayActivity {
    Client client;                  //用于连接服务器的socket
    Data.ClientJoinType type;       //玩家加入的类型
    boolean isGameStart = false;    //游戏是否开始
    boolean isGameFinish = false;   //游戏是否结束

    String ownName;             //自己的名字
    String remoteName;            //对方的名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        //初始化
        super.init();
        findViewById(R.id.localAc_inc_multiplayerInfo).setVisibility(View.VISIBLE);//显示需要等待玩家的信息
        findViewById(R.id.winMenu_iv_restart).setVisibility(View.INVISIBLE);//技术太渣，还是让玩家们退出去再开把
        findViewById(R.id.winMenu_iv_returnToHome).setOnClickListener(v -> quitGame());

        client   = Data.client;
        type     = (Data.ClientJoinType)getIntent().getExtras().get("JoinType");

        ownName  = getIntent().getStringExtra("ownName");
        Log.i(TAG, "init: 自己的名字:" + ownName);
        if(type == Data.ClientJoinType.Join){
            remoteName = getIntent().getExtras().getString("hostName");
        }
        //第一次接收服务端消息：游戏开始 一般是2
        recvCodeFromServer();
        //--------------判断传过来的Client--------------//
        try{
            if (client == null){
                throw new Exception("This socket already corrupted");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    //接收一次消息从服务端,消息会传入给handler
    private void recvCodeFromServer(){
        new Thread(()->{
            try {
                String result = client.recv();
                Message msg = new Message();
                msg.obj = result;
                handlerRecv.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    //开启游戏
    private void startTheGame(){
        Log.i(TAG, "handleMessage: 游戏开始!!");
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "handleMessage: 现在等远端");
                if(tictactoe.isFinish || count > 9) {
                    timer.cancel();
                }
                String result = null;
                try {
                    if(!isFirst){
                        result = client.recv();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(result != null){
                    Message msg = new Message();
                    msg.obj = result;
                    handlerRecv.sendMessage(msg);
                }
            }
        };
        timer.schedule(timerTask,500,500);//每500ms就尝试从服务器获取数据

        isGameStart = true;
    }
    //处理来自服务端的消息
    Handler handlerRecv = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Vector2 playerBPlace = null;
            String code = (String)msg.obj;
            Log.i(TAG, "handleMessage: 接收到服务端消息: " + code);

            switch (analyzeCodeType(code)){
                case vector2:
                    try {
                        playerBPlace = Tool.analyzeCodeToVector2(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    internetPlayerPlaceChess(playerBPlace);
                    break;
                case gameStared:
                    Log.i(TAG, "handleMessage: 游戏开始,但要等待房主选择谁先谁后");
                    findViewById(R.id.localAc_inc_multiplayerInfo).setVisibility(View.INVISIBLE);//隐藏需要等待玩家的信息
                    if(type == Data.ClientJoinType.Create){
                        Log.i(TAG, "handleMessage: 等待房主选择");
                        choseWhoFirst();
                    }
                    else{//客户端需要等待房主选择谁先谁后
                        Log.i(TAG, "handleMessage: 等待远端房主选择");
                        recvCodeFromServer();
                    }
                    Log.i(TAG, "handleMessage: 监听远端");

                    break;
                case hostFirst:
                    Toast.makeText(InternetPlayActivity.this,"房主先",Toast.LENGTH_LONG).show();
                    isFirst = false;
                    break;
                case hostLast:
                    Log.i(TAG, "handleMessage: 游戏正式开始,本玩家先");
                    Toast.makeText(InternetPlayActivity.this,"房主决定我先",Toast.LENGTH_LONG).show();
                    isFirst = true;
                    break;
                case leave:         //离开游戏
                        quitGame();
                        isGameFinish = true;
                        Toast.makeText(InternetPlayActivity.this,"对方离开了游戏",Toast.LENGTH_LONG).show();

                    finish();
                    break;
                default:
                case error:
                    Toast.makeText(InternetPlayActivity.this,"连接服务器出现异常，游戏退出",Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    };
    //分析code代码类型
    private Tool.CodeType analyzeCodeType(String code) {
        switch (code.charAt(0)){
            case '1'://坐标
                return Tool.CodeType.vector2;
            case '2': //游戏开始,但要等待房主选择谁先谁后
                remoteName = code.substring(1);
                Log.i(TAG, "analyzeCodeType: 对方名字" + remoteName);
                return Tool.CodeType.gameStared;
            case '4':
                startTheGame();//加入方游戏开始
                switch (code.charAt(1)){
                    case '1':           //房主先
                        Log.i(TAG, "analyzeCodeType: 加入方后");
                        return  Tool.CodeType.hostFirst;
                    case '0':           //房主后
                        Log.i(TAG, "analyzeCodeType: 加入方先");
                        return  Tool.CodeType.hostLast;
                    default:
                        return Tool.CodeType.error;
                }
            case '9'://退出
                return Tool.CodeType.leave;
            default:
                return Tool.CodeType.error;
        }
    }
    //网络玩家放棋子
    private void internetPlayerPlaceChess(Vector2 playerBPlace){
        try {
            if( playerB.putAChess(playerBPlace) ){
                int i = decideImgByVector2(playerBPlace);
                ImageView view = findViewById(Data.allP[i]);
                view.setImageResource(Data.lastImg);

                background.setBackgroundResource(Data.firstBackground);
                isFirst = true;
                count++;
            }
            tictactoe.Check();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "playB: x: " + playerBPlace.x + ",y: " + playerBPlace.y);
        Log.i(TAG, "playB: isWin: " + playerB.isWin);
    }
    //房主决定谁先谁后
    private void choseWhoFirst(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("你先还是 " + remoteName + " 先呢?")
        .setMessage("你来选择吧")
        .setCancelable(false)
        .setPositiveButton("我先", (dialog, which) -> {
            new Thread(()->{
                try {
                    Log.i(TAG, "choseWhoFirst: 房主先");
                    client.send("41");//发送房主先的命令
                    startTheGame();
                    isFirst = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        })
        .setNegativeButton("他先", (dialog, which) -> {
            new Thread(()->{
                try {
                    Log.i(TAG, "choseWhoFirst: 房主后");
                    client.send("40");//发送房主后的命令
                    startTheGame();
                    isFirst = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }).show();
    }

    //通过坐标判断放的是第几个图片
    private int decideImgByVector2(Vector2 v){
        return v.y*3 + v.x;
    }

    @Override
    protected void play(ImageView i, int finalX, int finalY) {
        try{
            if(!tictactoe.isFinish & count < 9 & isGameStart){
                if(isFirst){
                    if(playerA.putAChess(new Vector2(finalX, finalY))){
                        i.setImageResource(Data.firstImg);
                        background.setBackgroundResource(Data.lastBackground);
                        isFirst = false;
                        count++;
                    }
                    Log.i(TAG, "playA: x: " + finalX + ",y: " + finalY);
                    Log.i(TAG, "playA: isWin: " + playerA.isWin);
                    //发送坐标，让服务端转发
                    new Thread(()->{
                        try {
                            client.send("1"+finalX+""+finalY);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                else{
//                    recvCodeFromServer(); //由于游戏设计，导致了此函数必须点击才会进行一回合，或者说必须点击一次才能接收服务器消息，然后才渲染到页面
                    //所以此函数接收函数运行于一个定时任务即可，此定时任务拥有和此if一样的判断（避免出啥稀奇古怪的乱子），运行于startTheGame函数
                }
                tictactoe.show();
                tictactoe.Check();
                if(playerA.isWin){
                    whoWin(ownName + "获胜");
                }
                if(playerB.isWin){
                    whoWin(remoteName + "获胜");
                }
            }
            else if(!(count < 9)){
                whoWin("平局啦");
            }
            else if(!isGameStart){
                Toast.makeText(this,"游戏还未开始",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"游戏结束",Toast.LENGTH_LONG).show();
                quitGame();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //退出游戏
    private void quitGame(){
        new Thread(()->{
            try {
                if(client !=null){
                    client.send("9");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed: 断开连接");
        quitGame();
        isGameFinish = true;
        finish();
    }

}