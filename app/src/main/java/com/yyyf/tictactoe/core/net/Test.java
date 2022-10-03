package com.yyyf.tictactoe.core.net;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {
    public static void main(String[] args) {
        System.out.println("---客户端启动---");
        try{


            Socket socket = new Socket("120.24.177.154",26119);
            //使用PrintWriter和BufferedReader进行读写数据
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //发送数据
            pw.println("hello, this's come from java socket");
            pw.flush();
//          //接收数据
            String line = is.readLine();
            System.out.println("received from server " + line);
            //关闭资源
            pw.close();
            is.close();
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("---客户端关闭---");
    }
}
