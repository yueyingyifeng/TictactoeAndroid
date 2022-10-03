package com.yyyf.tictactoe.core;

import android.util.Log;

import com.yyyf.tictactoe.core.util.Board;
import com.yyyf.tictactoe.core.util.Player;
import com.yyyf.tictactoe.core.util.Vector2;

/*
    井字棋
    两名玩家，一个棋盘
    连成一条线即可获胜
 */
public class Tictactoe {
    private Player playerA,playerB;
    private Board board;
    
    public boolean isFinish = false;   //游戏是否结束

    public Tictactoe(Player playerA, Player playerB, Board board) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.board = board;

    }

    public void showSys(){
        System.out.println();
        for(int i = 0;i < 3;i++){
            for(int j = 0; j < 3; j++){
                System.out.print(board.getBoard()[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void show(){
//        for(int i = 0;i < 3;i++){
//            for(int j = 0; j < 3; j++){
//                Log.i("play",board.getBoard()[i][j] + " ");
//            }
//        }

        for(int i = 0;i < 3; i ++){
            Log.i("play",board.getBoard()[i][0] + " " + board.getBoard()[i][1] + " " + board.getBoard()[i][2] + " ");
        }

    }

    public void Check() throws Exception {
        int playerACount = 0,playerBCount = 0;
        //判断横着的情况
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board.get(new Vector2(i,j)) == playerA.getId()) {
                    playerACount++;
                    if(playerACount == 3){
                        playerA.isWin = true;
                        System.out.println("玩家A横着获胜" + playerA.isWin);

                        return;
                    }
                }
                else if(board.get(new Vector2(i,j)) == playerB.getId()){
                    playerBCount++;
                    if(playerBCount == 3){
                        System.out.println("玩家B横着获胜");
                        playerB.isWin = true;
                        return;
                    }
                }
            }
            playerACount = 0;
            playerBCount = 0;
        }

        //判断竖着的情况
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board.get(new Vector2(j,i)) == playerA.getId()) {
                    playerACount++;
                    if(playerACount == 3){
                        System.out.println("玩家A竖着获胜");
                        playerA.isWin = true;
                        return;
                    }
                }
                else if(board.get(new Vector2(j,i)) == playerB.getId()){
                    playerBCount++;
                    if(playerBCount == 3){
                        System.out.println("玩家B竖着获胜");
                        playerB.isWin = true;
                        return;
                    }
                }
            }
            playerACount = 0;
            playerBCount = 0;
        }
        //反斜着 \
        for(int u = 0;u < 3;u++){
            if(board.get(new Vector2(u,u)) == playerA.getId()) {
                playerACount++;
                if(playerACount == 3){
                    System.out.println("玩家A反斜着获胜");

                    playerA.isWin = true;
                    return;
                }
            }
            else if(board.get(new Vector2(u,u)) == playerB.getId()){
                playerBCount++;
                if(playerBCount == 3){
                    System.out.println("玩家B反斜着获胜");
                    playerB.isWin = true;
                    return;
                }
            }
        }
        playerACount = 0;
        playerBCount = 0;
        //斜着 /
        for(int u = 0;u < 3;u++){
            if(board.get(new Vector2(u,2 - u)) == playerA.getId()) {
                playerACount++;
                if(playerACount == 3){
                    System.out.println("玩家A斜着获胜");
                    playerA.isWin = true;
                    return;
                }
            }
            else if(board.get(new Vector2(u,2 - u)) == playerB.getId()){
                playerBCount++;
                if(playerBCount == 3){
                    System.out.println("玩家B斜着获胜");
                    playerB.isWin = true;
                    return;
                }
            }
        }


    }

    public static void main(String[] args) {
        Board board = new Board(3,3);

        Player playerA = new Player(0,board);
        Player playerB = new Player(-1,board);
        Tictactoe tictactoe = new Tictactoe(playerA,playerB,board);

        try {
            playerA.putAChess(new Vector2(0,2));
            tictactoe.Check();

            playerA.putAChess(new Vector2(1,1));

            tictactoe.Check();

//            playerB.putAChess(new Vector2(1,1));
//
//            tictactoe.Check();

            playerA.putAChess(new Vector2(2,0));

            tictactoe.Check();

            if(playerA.isWin){
                System.out.println("玩家A获胜");
            }
            else if(playerB.isWin){
                System.out.println("玩家B获胜");
            }
            else{
                System.out.println("平局");
            }
            tictactoe.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
