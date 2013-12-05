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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends ActionBarActivity{
    public static Context gamectx;
    private boolean inPause, startedActivity;
    public enum Direction {up, down, left, right, none}
    private MainThread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gamectx = GameActivity.this;
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }
    @Override
    public void onBackPressed() {
        onPause();
    }

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
        startedActivity = false;
        thread.setRunning(true);
        super.onResume();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return false;
    }

    // Thread
    public class MainThread extends Thread {

        // desired fps
        private final static int 	MAX_FPS = 50;
        private final static int	MAX_FRAME_SKIPS = 5;
        private final static int	FRAME_PERIOD = 1000 / MAX_FPS;

        private SurfaceHolder surfaceHolder;
        private GameView gamePanel;

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

            long beginTime;
            long timeDiff;
            int sleepTime;
            int framesSkipped;

            while (running) {
                canvas = null;
                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        beginTime = System.currentTimeMillis();
                        framesSkipped = 0;
                        this.gamePanel.update();
                        this.gamePanel.render(canvas);
                        timeDiff = System.currentTimeMillis() - beginTime;
                        sleepTime = (int)(FRAME_PERIOD - timeDiff);

                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {}
                        }

                        while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                            this.gamePanel.update();
                            sleepTime += FRAME_PERIOD;
                            framesSkipped++;
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////


    // SurfaceView
    public class GameView extends SurfaceView implements
            SurfaceHolder.Callback {

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
            getHolder().addCallback(this);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;

            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
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

            // DELETE ME
            Globals.controlSize = 2;
            // DELETE ME

            float cSize = 0;
            if (Globals.controlSize == 1) cSize = 12.5f;
            else if (Globals.controlSize == 2) cSize = 16.67f;
            else if (Globals.controlSize == 3) cSize = 25f;
            controls = new ControlField(context, screenWidth, screenHeight, cSize);
            screenWidth -= controls.holder.width();
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

            thread = new MainThread(getHolder(), this);

            setFocusable(true);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (thread.getState() == Thread.State.TERMINATED) {
                thread = new MainThread(getHolder(), this);
                thread.setRunning(true);
                thread.start();
            }
            else {
                thread.setRunning(true);
                thread.start();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            while (retry) {
                try {
                    thread.setRunning(false);
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retry = false;
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
                if (controls.holder.contains(x,y)){
                    controls.setDirection(x,y);
                }
            }
            else if (action == MotionEvent.ACTION_UP){
                controls.direction = Direction.none;
            }
            else {
                if (controls.holder.contains(x,y)){
                    controls.setDirection(x,y);
                }
            }
            return true;
        }

        public void render(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            canvas.drawColor(bgColor);
            player.draw(canvas);
            for (int i=0; i<fireCount; ++i)
                fireBall[i].draw(canvas);
            for (int i=0; i<coinCount; ++i)
                coin[i].draw(canvas);
            if (potLevel)
                potion.draw(canvas);
            canvas.drawText(Integer.toString(coinsCollected), player.posX-5, player.posY-10, paint);
            player.msg = "";
            controls.draw(canvas);
            statusBar.draw(canvas);
        }
        public void update() {
            if (statusBar.pauseButton.pressed) {
                statusBar.pauseButton.pressed = false;
                thread.setRunning(false);
                if (!inPause){
                    startedActivity = true;
                    inPause = true;
                    startActivity(new Intent(getContext(), PauseActivity.class));
                }
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
            if (Globals.fireCollision){
                player.msg = "!";
                Globals.fireCollision = false;
                --lives;
                if (lives == 0) checkWin();
                for (int i=0; i<fireCount; ++i){
                    fireBall[i].generate();
                    try {Thread.sleep(500/fireCount);}
                    catch (InterruptedException e) { }
                }
                player.posX = screenWidth/2;
                player.posY = screenHeight/2+statusBar.height;
            }
            if (Globals.coinCollisions > 0){
                score += 100*Globals.coinCollisions;
                coinsCollected += Globals.coinCollisions;
                checkWin();
                Globals.coinCollisions = 0;
            }
            if (Globals.potionCollision){
                score += 200;
                ++playerSpeed;
                Globals.potionCollision = false;
            }
        }
        private void checkWin(){
            if (!startedActivity){
                if (coinsCollected >= coinCount){
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
                    i.putExtra("SCORE", score);
                    getContext().startActivity(i);
                    thread.setRunning(false);
                    finish();
                }
            }
        }

    }
}
