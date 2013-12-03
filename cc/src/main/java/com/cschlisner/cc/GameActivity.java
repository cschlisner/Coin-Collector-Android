package com.cschlisner.cc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameActivity extends ActionBarActivity{
    private boolean inPause, startedActivity;
    public enum Direction {up, down, left, right, none}
    private MainThread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }
    @Override
    public void onBackPressed() {}

    @Override
    protected void onPause() {
        if (!inPause && !startedActivity){
            startActivity(new Intent(this, PauseActivity.class));
            inPause = true;
        }
        thread.setRunning(false);
        super.onPause();
    }

    @Override
    protected void onResume() {
        inPause = false;
        super.onResume();
    }


    // Thread
    public class MainThread extends Thread {

        // desired fps
        private final static int 	MAX_FPS = 50;
        // maximum number of frames to be skipped
        private final static int	MAX_FRAME_SKIPS = 5;
        // the frame period
        private final static int	FRAME_PERIOD = 1000 / MAX_FPS;

        // Surface holder that can access the physical surface
        private SurfaceHolder surfaceHolder;
        // The actual view that handles inputs
        // and draws to the surface
        private GameView gamePanel;

        // flag to hold game state 
        private boolean running;
        public void setRunning(boolean running) {
            this.running = running;
        }

        public MainThread(SurfaceHolder surfaceHolder, GameView gamePanel) {
            super();
            this.surfaceHolder = surfaceHolder;
            this.gamePanel = gamePanel;
        }

        @Override
        public void run() {
            Canvas canvas;

            long beginTime;		// the time when the cycle begun
            long timeDiff;		// the time it took for the cycle to execute
            int sleepTime;		// ms to sleep (<0 if we're behind)
            int framesSkipped;	// number of frames being skipped 

            sleepTime = 0;

            while (running) {
                canvas = null;
                // try locking the canvas for exclusive pixel editing
                // in the surface
                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        beginTime = System.currentTimeMillis();
                        framesSkipped = 0;	// resetting the frames skipped
                        // update game state 
                        this.gamePanel.update();
                        // render state to the screen
                        // draws the canvas on the panel
                        this.gamePanel.render(canvas);
                        // calculate how long did the cycle take
                        timeDiff = System.currentTimeMillis() - beginTime;
                        // calculate sleep time
                        sleepTime = (int)(FRAME_PERIOD - timeDiff);

                        if (sleepTime > 0) {
                            // if sleepTime > 0 we're OK
                            try {
                                // send the thread to sleep for a short period
                                // very useful for battery saving
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {}
                        }

                        while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                            // we need to catch up
                            this.gamePanel.update(); // update without rendering
                            sleepTime += FRAME_PERIOD;	// add frame period to check if in next frame
                            framesSkipped++;
                        }
                    }
                } finally {
                    // in case of an exception the surface is not left in 
                    // an inconsistent state
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }	// end finally
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////


    // SurfaceView
    public class GameView extends SurfaceView implements
            SurfaceHolder.Callback {

        public boolean starting;
        private int level, lives, score, coinCount, coinsCollected, fireSpeed,
                    fireCount, playerSpeed, screenWidth, screenHeight, bgColor;
        private boolean potLevel;
        private String difficulty;
        private StatusBar statusBar;
        private ControlField controls;
        private Player player;
        private Paint paint;
        private FireBall[] fireBall;
        private Coin[] coin;
        private Potion potion;
        public GameView(Context context) {
            super(context);
            // adding the callback (this) to the surface holder to intercept events
            getHolder().addCallback(this);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;

            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(40);
            Intent intent = getIntent();
            difficulty = intent.getStringExtra("DIFFICULTY");
            level = intent.getIntExtra("LEVEL", 0);
            score = intent.getIntExtra("SCORE", 0);
            lives = intent.getIntExtra("LIVES", 0);
            coinCount = intent.getIntExtra("COINS", 0);
            fireSpeed = intent.getIntExtra("FIRESPEED", 0);
            fireCount = intent.getIntExtra("FIRECOUNT", 0);
            playerSpeed = intent.getIntExtra("PLAYERSPEED", 0);
            if (level%2==0) potLevel = true;
            int levelHue = 3*level;
            if (difficulty.equals("easy")) bgColor = Color.argb(255, 44-levelHue, 145-levelHue, 29-levelHue);
            else if (difficulty.equals("med")) bgColor = Color.argb(255, 145-levelHue, 141-levelHue, 29-levelHue);
            else if (difficulty.equals("hard")) bgColor = Color.argb(255, 129-levelHue, 14-levelHue, 14-levelHue);
            statusBar = new StatusBar(context, screenWidth, screenHeight);
            controls = new ControlField(screenWidth, screenHeight);
            player = new Player(context);
            player.posX = screenWidth/2;
            player.posY = screenHeight/2+statusBar.height;
            fireBall = new FireBall[fireCount];
            coin = new Coin[coinCount];
            for (int i=0; i<fireCount; ++i){
                fireBall[i] = new FireBall(context, fireSpeed, screenWidth, screenHeight, statusBar.height);
            }
            for (int i=0; i<coinCount; ++i){
                coin[i] = new Coin(context, screenWidth, screenHeight, statusBar.height);
            }
            potion = new Potion(context, screenHeight, screenWidth, statusBar.height);

            // create the game loop thread
            thread = new MainThread(getHolder(), this);

            // make the GamePanel focusable so it can handle events
            setFocusable(true);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // at this point the surface is created and
            // we can safely start the game loop
            thread.setRunning(true);
            thread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // tell the thread to shut down and wait for it to finish
            // this is a clean shutdown
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // try again shutting down the thread
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int)event.getX();
            int y = (int)event.getY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN){
                if (statusBar.pauseButton.bounds.contains(x,y) && !statusBar.pauseButton.pressed){
                    statusBar.pauseButton.pressed = true;
                }
                else {
                    controls.positionField(x,y);
                    controls.drawField = true;
                    controls.direction = Direction.none;
                }
            }
            else if (action == MotionEvent.ACTION_UP){
                controls.drawField = false;
                controls.direction = Direction.none;
            }
            else {
                controls.setDirection(x,y);
            }
            return true;
        }

        public void render(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            canvas.drawColor(bgColor);
            controls.draw(canvas);
            player.draw(canvas);
            for (int i=0; i<fireCount; ++i)
                fireBall[i].draw(canvas);
            for (int i=0; i<coinCount; ++i)
                coin[i].draw(canvas);
            if (potLevel)
                potion.draw(canvas);
            canvas.drawText(player.msg, player.posX, player.posY-10, paint);
            statusBar.draw(canvas);
        }
        public void update() {
            if (statusBar.pauseButton.pressed) {
                statusBar.pauseButton.pressed = false;
                thread.setRunning(false);
                startActivity(new Intent(getContext(), PauseActivity.class));
                inPause = true;
            }
            statusBar.update(lives, score);
            checkWin();

            // move player according to state of control field
            switch (controls.direction){
                case up:
                    if (player.posY-5 >= statusBar.height) player.posY -= playerSpeed;
                    player.direction = Direction.up;
                    player.moving = true;
                    break;
                case right:
                    if (player.posX+40 <= screenWidth) player.posX += playerSpeed;
                    player.direction = Direction.right;
                    player.moving = true;
                    break;
                case left:
                    if (player.posX-6 >= 0) player.posX -= playerSpeed;
                    player.direction = Direction.left;
                    player.moving = true;
                    break;
                case down:
                    if (player.posY+51 <= screenHeight) player.posY += playerSpeed;
                    player.direction = Direction.down;
                    player.moving = true;
                    break;
                case none:
                    player.moving = false;
                    break;
            }

            // Handle Collison Events
            for (int i=0; i<fireCount; ++i)
                fireBall[i].update(player.playerRect);
            for (int i=0; i<coinCount; ++i)
                coin[i].update(player.playerRect);
            if (potLevel)
                potion.update(player.playerRect);
            player.msg = "";
            if (Collisions.fireCollision && !Collisions.devMode){
                player.msg = "DARN";
                Collisions.fireCollision = false;
                --lives;
                if (lives == 0) checkWin();
                for (int i=0; i<fireCount; ++i){
                    fireBall[i].generate();
                    try {Thread.sleep(500/fireCount);} //wait for each fireball to generate different coordinates
                    catch (InterruptedException e) { }
                }
                player.posX = screenWidth/2;
                player.posY = screenHeight/2+statusBar.height;
            }
            if (Collisions.coinCollision){
                player.msg = "SWEET!";
                score += 100;
                ++coinsCollected;
                Collisions.coinCollision = false;
            }
            if (Collisions.potionCollision){
                player.msg = "AWWWWWW YISSSSS!";
                score += 200;
                ++playerSpeed;
                Collisions.potionCollision = false;
            }
        }
        private void checkWin(){
            if (!startedActivity){
                if (coinsCollected == coinCount){
                    startedActivity = true;
                    Intent i = new Intent(getContext(), NextLevelActivity.class);
                    i.putExtra("DIFFICULTY", difficulty);
                    i.putExtra("LEVEL", level+1);
                    i.putExtra("SPEED", playerSpeed-7);
                    i.putExtra("SCORE", score);
                    getContext().startActivity(i);
                    thread.setRunning(false);
                    finish();
                }
                else if (lives <= 0){
                    startedActivity = true;
                    Intent i = new Intent(getContext(), GameOverActivity.class);
                    getContext().startActivity(i);
                    thread.setRunning(false);
                    finish();
                }
            }
        }

    }
}
