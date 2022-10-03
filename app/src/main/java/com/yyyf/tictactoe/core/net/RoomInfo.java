package com.yyyf.tictactoe.core.net;
//房间类，保存房间的信息，然后显示在ListView中
public class RoomInfo {
    int no;     //在列表中的编号
    String name;//房间名，即对方的名字
    int sock;   //sock
    public RoomInfo(){
        no   = -1;
        name = "err";
        sock = -1;
    }

    public RoomInfo(int no, String name, int sock) {
        this.no   = no;
        this.name = name;
        this.sock = sock;
    }

    public int getSocket() {
        return sock;
    }

    public void setSock(int sock) {
        this.sock = sock;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Room{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", sock=" + sock +
                '}';
    }
}
