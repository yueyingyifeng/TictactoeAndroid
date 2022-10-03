package com.yyyf.tictactoe.core.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

public class Client{
    private Socket socket;

    private final String ip = "120.24.177.154";
    private final int prot  = 26119;

    public Client() throws IOException {
        socket = new Socket(ip,prot);
    }

    public void send(String msg) throws IOException {
        //初始化发送数据
        Log.i("client", "send: 客户端发送的消息: " + msg);
        PrintWriter printWriter;
        printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(msg);
        printWriter.flush();
    }

    public String recv() throws IOException {
//        //初始化接收数据
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return is.readLine();//readLine必须有结尾，比如\n换行，否则会一直读下去，触发超时
    }
}
