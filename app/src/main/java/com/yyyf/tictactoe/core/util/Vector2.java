package com.yyyf.tictactoe.core.util;

/*
    坐标类 保存二维坐标
 */
public class Vector2 {
    public int x,y;
    public Vector2(){
        x = 0;
        y = 0;
    }
    public Vector2(int x,int y){
        this.x = x;
        this.y = y;
    }
    public Vector2(Vector2 v){
        this.x = v.x;
        this.y = v.y;
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
