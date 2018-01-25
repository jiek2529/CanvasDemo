package com.example.jiek.canvasdemo;

import android.app.Activity;
import android.os.Bundle;

import com.example.jiek.canvasdemo.view.CanvasView;

import static com.example.jiek.canvasdemo.view.CanvasView.DrawType.DRAW_COMPASS;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CanvasView(this, DRAW_COMPASS));
    }
}

