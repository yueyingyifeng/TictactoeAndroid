package com.yyyf.tictactoe;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yyyf.tictactoe.adapters.RoomAdapter;
import com.yyyf.tictactoe.core.net.Client;
import com.yyyf.tictactoe.core.net.RoomInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "room";
    ListView roomListView;
    RoomAdapter roomAdapter;

    Button createRoom;
    Client client;

    ProgressBar progressBar;
    EditText inputName;

    String name;//玩家名字

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_room_list);

        init();
    }

    private void init() {
        progressBar = findViewById(R.id.roomList_progress_listLoading);
        roomListView = findViewById(R.id.roomList_listv_list);
        createRoom = findViewById(R.id.roomList_btn_createRoom);
        createRoom.setOnClickListener(this);
        name = "";
        inputName = new EditText(this);

        inputPlayerName();

        new Thread(()->{
            Log.i(TAG, "init: 线程启动");
            try {
                client = new Client();
                Data.client = client;
                String d = client.recv();
                Log.i(TAG, "init: 接收到的数据(应该是房间列表) " + d);
                if(d.charAt(0) == '0'){
                    Log.i(TAG, "init: 连接成功");
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    Log.i(TAG, "init: 连接失败");
                }
                List<RoomInfo> roomInfoList = analyzeCode(d);
                //将内容发送给主线程
                Message msg = new Message();
                msg.obj = roomInfoList;
                handler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void inputPlayerName(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("请输入你的昵称");
        alert.setView(inputName);//将输入框放入alertDialog里面
        alert.setPositiveButton("好了", (dialog, which) -> {
            name = inputName.getText().toString();

            if (name.equals("")){
                Toast.makeText(RoomListActivity.this,"你的名字似乎昵称，要不返回再写一下？",Toast.LENGTH_LONG).show();
            }
            else{
                if(Data.client == null){//确保socket有用
                    Toast.makeText(RoomListActivity.this,"找不到服务器。。。",Toast.LENGTH_LONG).show();
                }
                else{
                    Log.i(TAG, "inputPlayerName: 名称输入完毕，传达给服务端 " + name);
                    new Thread(() -> {
                        try {
                            Data.client.send("0"+name);  //告知服务器玩家的名字
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    Toast.makeText(RoomListActivity.this,"你好鸭，"+name,Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("我点错地方啦",(dialog,which)->{
            finish();
        });
        alert.setCancelable(false);
        alert.show();
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            List<RoomInfo> roomInfoList = (List<RoomInfo>) msg.obj;
            roomAdapter = new RoomAdapter(roomInfoList,RoomListActivity.this);
            roomListView.setAdapter(roomAdapter);
            progressBar.setVisibility(View.INVISIBLE);
            //---------设置list监听事件-跳转到游戏画面---------//
            roomListView.setOnItemClickListener((parent, view, position, id) -> {
                if(!name.equals("")){
                    Toast.makeText(RoomListActivity.this,"你加入的房间是 : " + roomInfoList.get(position).getName(),Toast.LENGTH_LONG).show();
                    new Thread(()->{
                        try {
                            client.send("2"+roomInfoList.get(position).getSocket());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    Intent intent = new Intent(RoomListActivity.this,InternetPlayActivity.class);
                    intent.putExtra("JoinType", Data.ClientJoinType.Join);
                    intent.putExtra("ownName",name);
                    intent.putExtra("hostName",roomInfoList.get(position).getName());
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(RoomListActivity.this,"他不知道你的昵称，因为你还没写.",Toast.LENGTH_LONG).show();

                }

            });
        }
    };


    /*
        解析服务端传回的内容，通常是:
        0yyyf.123,abcd.21,efg.32
        然后 yyyf.123是一项，其中yyyf是名称 123是sock . 来分割
        PS:这个算法有问题。。。
        我用了个蠢方法解决了不过 （:小事）
     */
    private List<RoomInfo> analyzeCode(String code) throws Exception {
        code = code.substring(1);
        if(code.charAt(code.length()-1) != ','){
            code +=",";//这个就是蠢方法 :D
        }
        int len = code.length();

        List<RoomInfo> list = new ArrayList<>();
        String name = "err";
        String sock = "-1";
        for(
                int j = 0,i = 0,c = 0;
                j < len;
                j++
        ){

            if(code.charAt(j) == '.'){//sock分隔符号
                c++;
                name = code.substring(i,j);
                i = j+1;
            }
            if(code.charAt(j) == ','){//项的分割符号
                c++;
                sock = code.substring(i,j);
                i = j+1;
            }
            if(c == 2){
                c = 0;
                if(name.equals("") || sock.equals("")){
                    throw new Exception("Analyze code fail,code is incomplete or somewhere wrong");
                }
                list.add(new RoomInfo(i/2,name,Integer.parseInt(sock)));
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.roomList_btn_createRoom:
                if(client != null && !name.equals("")){
                    new Thread(()->{
                        try {
                            client.send("3");//告诉服务器我要创建房间了
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    //然后等待别人加入
                    Intent intent = new Intent(RoomListActivity.this,InternetPlayActivity.class);
                    intent.putExtra("JoinType", Data.ClientJoinType.Create);
                    startActivity(intent);
                    finish();
                }
                else if(name.equals("")){
                    Toast.makeText(RoomListActivity.this,"你得先写昵称鸭",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RoomListActivity.this,"似乎。。。我连不上服务器",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "onBackPressed: 断开连接");
        new Thread(()->{
            try {
                if(client !=null){
                    client.send("9");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        finish();
    }
}