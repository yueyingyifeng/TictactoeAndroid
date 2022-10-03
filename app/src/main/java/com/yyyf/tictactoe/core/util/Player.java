package com.yyyf.tictactoe.core.util;

import com.yyyf.tictactoe.core.util.Board;
import com.yyyf.tictactoe.core.util.Vector2;

import java.util.Random;

public class Player {
    //棋子的编号
    int id;
    public boolean isWin = false;
    Board board;
    public Player(int id, Board board) {
        this.board = board;
        try{
            if(id == 0){
                throw new RuntimeException("player id cannot be zero, it has been random!");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Random random =new Random();
            id =random.nextInt(1024);
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }
    //如果放置成功，则返回真
    public boolean putAChess(Vector2 v) throws Exception{
        if(board.get(v) == 0){//检查要放的地方是不是已经放了
            board.Put(v,this.id);
            return true;
        }
        else {
            return false;
        }
    }

    private void positionCheck(Vector2 v) throws Exception {
        throw new Exception("Vector2：" + v.toString() + " position already has chess, id: " + board.get(v));
    }

}
