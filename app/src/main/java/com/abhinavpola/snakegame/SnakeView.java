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
    public String name;
    private Thread Thread = null;
    private volatile boolean Playing;
    private Canvas Canvas;
    private SurfaceHolder Holder;
    private Paint Paint;
    private Context context;
    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }
    private Direction direction = Direction.RIGHT;
    private int ScreenWidth;
    private int ScreenHeight;
    private long NextFrameTime;
    private final long FPS = 10;
    private final long MILLIS_IN_A_SECOND = 1000;
    public int Score;
    private int[] SnakeX;
    private int[] SnakeY;
    private int SnakeLength;
    private int MouseX;
    private int MouseY;
    // size of a snake segment
    private int BlockSize;
    // The size of the playable area
    private final int NUBLOCKS_WIDE = 40;
    private int NumBlocksHigh; // determined dynamically

    
    public SnakeView(Context context, Point size) {
        super(context);

        ScreenWidth = size.x;
        ScreenHeight = size.y;

        BlockSize = ScreenWidth / NUBLOCKS_WIDE;

        NumBlocksHigh = ScreenHeight / BlockSize;

        Holder = getHolder();
        Paint = new Paint();

        SnakeX = new int[200];
        SnakeY = new int[200];

        startGame();
    }
    public void drawGame() {
        if (Holder.getSurface().isValid()) {
            Canvas = Holder.lockCanvas();
            // Clear the screen with black
            Canvas.drawColor(Color.argb(255, 29, 29, 29));
            // Set the color of the paint to draw the snake and mouse with
            Paint.setColor(Color.argb(255, 250, 128, 114));
            // Choose how big the score will be
            Paint.setTextSize(50);
            Canvas.drawText("Score:" + Score, 50, 50, Paint);


            //Draw the snake
            for (int i = 0; i < SnakeLength; i++) {
                Canvas.drawRect(SnakeX[i] * BlockSize,
                        (SnakeY[i] * BlockSize),
                        (SnakeX[i] * BlockSize) + BlockSize,
                        (SnakeY[i] * BlockSize) + BlockSize,
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

    public void startGame() {
        SnakeLength = 1;
        SnakeX[0] = NUBLOCKS_WIDE / 2;
        SnakeY[0] = NumBlocksHigh / 2;
        spawnMouse();
        Score = 0;
        NextFrameTime = System.currentTimeMillis();
    }

    public void updateGame() {
        // check if head of the snake touches the mouse
        if (SnakeY[0] == MouseY && SnakeX[0] == MouseX) {
            eatMouse();
        }
        moveSnake();
        if (isDead() == true) {
            if (MainMenu.highScore < Score) {
                MainMenu.highScore = Score;
            }
            name = MainActivity.account.getDisplayName();

            if (MainMenu.currentScore == null || Score > MainMenu.currentScore) {
                MainMenu.root.child(name).setValue(Score);
            }
            startGame();
        }
    }
    public boolean checkForUpdate() {
        if(NextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed
            // Setup when the next update will be triggered
            NextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;
            return true;
        }
        return false;
    }

    private void moveSnake() {
        // Move the body
        for (int i = SnakeLength; i > 0; i--) {
            // Start at the back and move each position to the position of part in front of it
            // not including the head of the snake
            SnakeX[i] = SnakeX[i - 1];
            SnakeY[i] = SnakeY[i - 1];

        }

        // Move the head
        switch (direction) {
            case UP:
                SnakeY[0]--;
                break;

            case RIGHT:
                SnakeX[0]++;
                break;

            case DOWN:
                SnakeY[0]++;
                break;

            case LEFT:
                SnakeX[0]--;
                break;
        }
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
    }


    public void run() {
        while (Playing == true) {
            if (checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }

    private boolean isDead() {
        boolean dead = false;

        // Hit a wall
        if (SnakeX[0] == -1) dead = true;
        if (SnakeX[0] >= NUBLOCKS_WIDE) dead = true;
        if (SnakeY[0] == -1) dead = true;
        if (SnakeY[0] == NumBlocksHigh) dead = true;

        // run into itself
        for (int i = SnakeLength - 1; i > 0; i--) {
            if ((i > 4) && (SnakeX[0] == SnakeX[i])) {
                if (SnakeY[0] == SnakeY[i]) {
                    dead = true;
                }
            }
        }
        return dead;
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

