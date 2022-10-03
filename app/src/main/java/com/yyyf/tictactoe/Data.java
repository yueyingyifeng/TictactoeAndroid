package com.yyyf.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yyyf.tictactoe.core.net.Client;

public class Data {
    final static int[] allP = {
            R.id.p1,
            R.id.p2,
            R.id.p3,
            R.id.p4,
            R.id.p5,
            R.id.p6,
            R.id.p7,
            R.id.p8,
            R.id.p9,
    };

    final static int firstImg = R.drawable.circle;
    final static int lastImg = R.drawable.cross;

    final static int firstBackground = R.drawable.blue;
    final static int lastBackground = R.drawable.red;

    final static String chosePlayerOrComputerMsg = "与冷酷的电脑还是和身边的好朋友呢?";

    static void Restart(Activity activity,Intent intent){
        activity.startActivity(intent);
        activity.finish();
    }

    static void ReturnToHome(Activity activity){
        activity.finish();
    }

    public enum ClientJoinType {
        Join,Create
    }

    public static Client client;

}
