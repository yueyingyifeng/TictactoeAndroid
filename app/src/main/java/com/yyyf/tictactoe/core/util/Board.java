package com.yyyf.tictactoe.core.util;

import com.yyyf.tictactoe.core.util.Vector2;

/*
    棋盘，构建 3x3 的井字棋盘
 */
public class Board {
    private int[][] board;
    int maxX,maxY;
    public Board(int maxX,int maxY){
        board = new int[maxX][maxY];
        this.maxX = maxX;
        this.maxY = maxY;
        ResetAll();
    }
    public Board(int[][] board){
        this.board = board;
    }
    //放一个棋子在坐标上，id是特殊的棋子
    public void Put(Vector2 v, int id) throws Exception{
        outOfBoardSizeCheck(v);
        board[v.x][v.y] = id;
    }
    private void outOfBoardSizeCheck(Vector2 v) throws Exception {
        if(v.x > maxX || v.y > maxY || v.x < 0 || v.y < 0){
            throw new Exception("out of board size");
        }
    }
    //将坐标设为0
    public void Reset(Vector2 v){
        board[v.x][v.y] = 0;
    }
    //全部重置
    public void ResetAll(){
        for(int i = 0;i < maxX; i++){
            for(int j = 0; j < maxY; j++){
                board[i][j] = 0;
            }
        }
    }
    //获取坐标中棋盘的内容
    public int get(Vector2 v) throws Exception {
        outOfBoardSizeCheck(v);
        return board[v.x][v.y];
    }

    public int[][] getBoard() {
        return board;
    }
}
