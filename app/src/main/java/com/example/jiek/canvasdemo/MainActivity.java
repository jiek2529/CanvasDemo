package com.example.jiek.canvasdemo;

import android.app.Activity;
import android.os.Bundle;

import com.example.jiek.canvasdemo.view.DrawingBoardView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawingBoardView(this));//显示画板
//        setContentView(new CanvasView(this, DRAW_CLOCK));//显示时钟
//        setContentView(new MagicCube(this));//显示魔方
    }
}

