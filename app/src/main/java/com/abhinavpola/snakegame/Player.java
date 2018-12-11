package com.abhinavpola.snakegame;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class Player extends Activity {

    SnakeView snakeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);


        snakeView = new SnakeView(this, size);

        setContentView(snakeView);
    }
}
