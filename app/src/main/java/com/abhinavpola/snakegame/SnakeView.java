package com.abhinavpola.snakegame;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

public class SnakeView extends SurfaceView implements Runnable {
    private Thread Thread = null;

    private volatile boolean Playing;

    private Canvas Canvas;

    private SurfaceHolder Holder;

    private Paint Paint;

    private Context context;

    // Sound
    /*
    private SoundPool SoundPool;
    private int get_mouse_sound = -1;
    private int dead_sound = -1;
    */


    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    // Start by heading to the right
    private Direction direction = Direction.RIGHT;

    // What is the screen resolution
    private int ScreenWidth;
    private int ScreenHeight;

    // Correlating to Frames
    private long NextFrameTime;

    private final long FPS = 10;

    private final long MILLIS_IN_A_SECOND = 1000;


    // The current Score
    public int Score;

    // The location in the grid of all the segments
    private int[] SnakeXs;
    private int[] SnakeYs;

    private int SnakeLength;

    // Where is the mouse
    private int MouseX;
    private int MouseY;

    // The size in pixels of a snake segment
    private int BlockSize;

    // The size in segments of the playable area
    private final int NUBLOCKS_WIDE = 40;
    private int NumBlocksHigh; // determined dynamically


    public SnakeView(Context context, Point size) {
        super(context);

        context = context;

        ScreenWidth = size.x;
        ScreenHeight = size.y;


        BlockSize = ScreenWidth / NUBLOCKS_WIDE;


        NumBlocksHigh = ScreenHeight / BlockSize;

        // Set the sound up
        /*
        loadSound();
        */

        // Initialize the drawing objects
        Holder = getHolder();
        Paint = new Paint();

        // If you score 200 you are rewarded with an achievement!
        SnakeXs = new int[200];
        SnakeYs = new int[200];

        startGame();
    }
    public void run() {
        // The check for Playing prevents a crash at the start
        // You could also extend the code to provide a pause feature
        while (Playing) {

            // Update 10 times a second
            if (checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }
    public void pause() {
        Playing = false;
        try {
            Thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        Playing = true;
        Thread = new Thread(this);
        Thread.start();
    }

    public void startGame() {
        // Start with just a head, in the middle of the screen
        SnakeLength = 1;
        SnakeXs[0] = NUBLOCKS_WIDE / 2;
        SnakeYs[0] = NumBlocksHigh / 2;


        spawnMouse();

        // Reset the Score
        Score = 0;

        // Setup NextFrameTime so an update is triggered immediately
        NextFrameTime = System.currentTimeMillis();
    }

    public void spawnMouse() {
        Random random = new Random();
        MouseX = random.nextInt(NUBLOCKS_WIDE - 1) + 1;
        MouseY = random.nextInt(NumBlocksHigh - 1) + 1;
    }

    private void eatMouse() {
        SnakeLength++;
        spawnMouse();
        Score = Score + 1;
        //SoundPool.play(get_mouse_sound, 1, 1, 0, 0, 1);
    }

    private void moveSnake() {
        // Move the body
        for (int i = SnakeLength; i > 0; i--) {
            // Start at the back and move  each position to the position of the segment in front of it
            // not including the head of the snake
            SnakeXs[i] = SnakeXs[i - 1];
            SnakeYs[i] = SnakeYs[i - 1];

        }

        // Move the head
        switch (direction) {
            case UP:
                SnakeYs[0]--;
                break;

            case RIGHT:
                SnakeXs[0]++;
                break;

            case DOWN:
                SnakeYs[0]++;
                break;

            case LEFT:
                SnakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath() {
        // Has the snake died?
        boolean dead = false;

        // Hit a wall?
        if (SnakeXs[0] == -1) dead = true;
        if (SnakeXs[0] >= NUBLOCKS_WIDE) dead = true;
        if (SnakeYs[0] == -1) dead = true;
        if (SnakeYs[0] == NumBlocksHigh) dead = true;

        // Eaten itself?
        for (int i = SnakeLength - 1; i > 0; i--) {
            if ((i > 4) && (SnakeXs[0] == SnakeXs[i]) && (SnakeYs[0] == SnakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void updateGame() {
        // Did the head of the snake touch the mouse?
        if (SnakeXs[0] == MouseX && SnakeYs[0] == MouseY) {
            eatMouse();
        }

        moveSnake();

       if (detectDeath()) {
           //start again
           //SoundPool.play(dead_sound, 1, 1, 0, 0, 1);
           if (Score > MainMenu.highScore ) {
               MainMenu.highScore = Score;
           }
           startGame();
       }
    }

    public void drawGame() {
        // Prepare to draw
        if (Holder.getSurface().isValid()) {
            Canvas = Holder.lockCanvas();

            // Clear the screen with my favorite color
            Canvas.drawColor(Color.argb(255, 120, 197, 87));

            // Set the color of the paint to draw the snake and mouse with
            Paint.setColor(Color.argb(255, 255, 255, 255));

            // Choose how big the score will be
            Paint.setTextSize(50);
            Canvas.drawText("Score:" + Score, 50, 50, Paint);

            //Draw the snake
            for (int i = 0; i < SnakeLength; i++) {
                Canvas.drawRect(SnakeXs[i] * BlockSize,
                        (SnakeYs[i] * BlockSize),
                        (SnakeXs[i] * BlockSize) + BlockSize,
                        (SnakeYs[i] * BlockSize) + BlockSize,
                        Paint);
            }

            //draw the mouse
            Canvas.drawRect(MouseX * BlockSize,
                    (MouseY * BlockSize),
                    (MouseX * BlockSize) + BlockSize,
                    (MouseY * BlockSize) + BlockSize,
                    Paint);

            // Draw the whole frame
            Holder.unlockCanvasAndPost(Canvas);
        }
    }

    public boolean checkForUpdate() {

        // Are we due to update the frame
        if(NextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            NextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;


            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= ScreenWidth / 2) {
                    switch(direction){
                        case UP:
                            direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            direction = Direction.DOWN;
                            break;
                        case DOWN:
                            direction = Direction.LEFT;
                            break;
                        case LEFT:
                            direction = Direction.UP;
                            break;
                    }
                } else {
                    switch(direction){
                        case UP:
                            direction = Direction.LEFT;
                            break;
                        case LEFT:
                            direction = Direction.DOWN;
                            break;
                        case DOWN:
                            direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            direction = Direction.UP;
                            break;
                    }
                }
        }
        return true;
    }


}

