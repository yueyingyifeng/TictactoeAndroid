package com.yyyf.tictactoe.core.net;

import com.yyyf.tictactoe.core.util.Vector2;

public class Tool {
     //转换数据，例如 112将分析出 vector2(1,2)
     public static Vector2 analyzeCodeToVector2(String code) throws Exception {
        code = code.substring(1);
        if(code.length() < 2){
            throw new Exception("To convert code to vector2,it must be complete and correct");
        }
        return new Vector2(
                Integer.parseInt(code.charAt(0) + ""),
                Integer.parseInt(code.charAt(1) + "")
        );
    }

    public enum CodeType{
         vector2,leave,error,
        hostFirst,hostLast,
        gameStared
    }
}
