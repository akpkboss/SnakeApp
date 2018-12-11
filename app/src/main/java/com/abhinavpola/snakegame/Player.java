package com.abhinavpola.snakegame;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
/*
We used the below link as a guide for some components of this application.
http://gamecodeschool.com/android/coding-a-snake-game-for-android/
*/

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
