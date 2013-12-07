package com.cschlisner.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GameActivity extends Activity{
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
        return true;
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
        private int level, lives, score, coinCount, coinsCollected, fireSpeed, tilesX, tilesY,
                    fireCount, playerSpeed, screenWidth, orgSW, screenHeight, bgA, highScore;
        private float offsetX, offsetY, leftCntrlOffset;
        private float[] controlSize = new float[]{12.5f,16.67f,25.0f};
        private RectF mapRect, viewRect;
        private boolean potLevel, bgADir;
        private String difficulty;
        private StatusBar statusBar;
        private ControlField controls;
        private Player player;
        private Paint paint;
        private FireBall[] fireBall;
        private Coin[] coin;
        private Potion potion;
        private Bitmap bgTile, altBgTile;
        public int cntrlSize = Globals.controlSize;
        public boolean cntrlRight = Globals.controlsRight;
        public GameView(Context context) {
            super(context);
            getHolder().addCallback(this);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            orgSW = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            mapRect = new RectF();
            viewRect = new RectF();
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
            paint.setTextSize(40);
            Intent intent = getIntent();
            difficulty = intent.getStringExtra("DIFFICULTY");
            level = intent.getIntExtra("LEVEL", 0);
            score = intent.getIntExtra("SCORE", 0);
            highScore = intent.getIntExtra("HSCORE", 0);
            lives = intent.getIntExtra("LIVES", 0);
            coinCount = intent.getIntExtra("COINS", 0);
            fireSpeed = intent.getIntExtra("FIRESPEED", 0);
            fireCount = intent.getIntExtra("FIRECOUNT", 0);
            playerSpeed = intent.getIntExtra("PLAYERSPEED", 0);
            if (level%2==0) potLevel = true;
            statusBar = new StatusBar(context, level, screenWidth);
            controls = new ControlField(context, screenWidth, screenHeight, controlSize[cntrlSize], cntrlRight);


            if (difficulty.equals("easy")){
                bgTile = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
                mapRect.set(0, 0, 2000, 2000);
            }
            else if (difficulty.equals("medium")){
                bgTile = BitmapFactory.decodeResource(getResources(), R.drawable.stone);
                mapRect.set(0, 0, 2500, 2500);
            }
            else if (difficulty.equals("hard")){
                bgTile = BitmapFactory.decodeResource(getResources(), R.drawable.molten1);
                altBgTile = BitmapFactory.decodeResource(getResources(), R.drawable.molten2);
                mapRect.set(0, 0, 3000, 3000);
            }
            if (cntrlRight) screenWidth -= controls.holder.width();
            else leftCntrlOffset = controls.holder.width();
            viewRect.set(offsetX+leftCntrlOffset, offsetY, offsetX+screenWidth, offsetY+screenHeight);
            tilesX = ((int)mapRect.width()/bgTile.getWidth())+1;
            tilesY = ((int)mapRect.height()/bgTile.getHeight())+1;
            player = new Player(context);
            player.posX = offsetX+viewRect.width()/2;
            player.posY = screenHeight/2+statusBar.height;
            fireBall = new FireBall[fireCount];
            coin = new Coin[coinCount];
            for (int i=0; i<fireCount; ++i){
                fireBall[i] = new FireBall(context, fireSpeed, (int)mapRect.width(), (int)mapRect.height());
            }
            for (int i=0; i<coinCount; ++i){
                coin[i] = new Coin(context, (int)mapRect.width(), (int)mapRect.height());
            }
            potion = new Potion(context, (int)mapRect.width(), (int)mapRect.height());
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
            if (cntrlSize != Globals.controlSize || cntrlRight != Globals.controlsRight){
                leftCntrlOffset = 0;
                screenWidth = orgSW;
                controls = new ControlField(getContext(), screenWidth, screenHeight, controlSize[Globals.controlSize], Globals.controlsRight);
                if (Globals.controlsRight) screenWidth -= controls.holder.width();
                else leftCntrlOffset = controls.holder.width();
                viewRect.set(offsetX+leftCntrlOffset, offsetY, offsetX+screenWidth, offsetY+screenHeight);
                cntrlSize = Globals.controlSize;
                cntrlRight = Globals.controlsRight;
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
            int x = (int)offsetX+(int)event.getX();
            int y = (int)offsetY+(int)event.getY();
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN){
                if (controls.pauseButton.touchRect.contains(x,y) && !controls.pauseButton.pressed){
                    controls.pauseButton.pressed = true;
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
            canvas.translate(-offsetX, -offsetY);
            viewRect.set(offsetX+leftCntrlOffset, offsetY, offsetX+screenWidth, offsetY+screenHeight);
            if (bgA <= 0 || bgA >= 255) bgADir = !bgADir;
            if (bgADir) bgA+=2;
            else bgA-=2;
            for (int y = 0; y<tilesY; ++y)
                for (int x = 0; x < tilesX; ++x){
                    paint.setAlpha(255);
                    canvas.drawBitmap(bgTile, x*bgTile.getWidth(), y*bgTile.getHeight(), paint);
                    paint.setAlpha(bgA);
                    if (altBgTile!=null) canvas.drawBitmap(altBgTile, x*bgTile.getWidth(), y*bgTile.getHeight(), paint);
                }
            paint.setAlpha(255);
            player.draw(canvas);
            for (int i=0; i<fireCount; ++i)
                fireBall[i].draw(canvas);
            for (int i=0; i<coinCount; ++i)
                coin[i].draw(canvas);
            if (potLevel)
                potion.draw(canvas);
            paint.setColor(Color.WHITE);
            canvas.drawText(player.msg, player.posX-5, player.posY-10, paint);
            player.msg = "";
            controls.draw(canvas);
            statusBar.draw(canvas);
        }
        public void update() {
            // make the player invincible right away
            player.invincible = (player.blinks < 6);
            if (controls.pauseButton.pressed) {
                controls.pauseButton.pressed = false;
                thread.setRunning(false);
                if (!inPause){
                    startedActivity = true;
                    inPause = true;
                    startActivity(new Intent(getContext(), PauseActivity.class));
                }
            }
            checkWin();
            int cameraSpeed = playerSpeed - 3;
            // move player according to state of control field
            switch (controls.direction){
                case up:
                    if (player.posY-5 >= statusBar.height+offsetY) player.posY -= playerSpeed;
                    else ++cameraSpeed;
                    player.direction = Direction.up;
                    player.moving = true;
                    break;
                case right:
                    if (player.posX+40 <= offsetX+screenWidth) player.posX += playerSpeed;
                    else ++cameraSpeed;
                    player.direction = Direction.right;
                    player.moving = true;
                    break;
                case left:
                    if (player.posX-6 >= offsetX+leftCntrlOffset) player.posX -= playerSpeed;
                    else ++cameraSpeed;
                    player.direction = Direction.left;
                    player.moving = true;
                    break;
                case down:
                    if (player.posY+51 <= offsetY+screenHeight) player.posY += playerSpeed;
                    else ++cameraSpeed;
                    player.direction = Direction.down;
                    player.moving = true;
                    break;
                case none:
                    player.moving = false;
                    break;
            }
            // camera movement
            int deltaX, deltaY, distX, distY;
            deltaY = ((int)viewRect.centerY() - (int)player.playerRect.centerY());
            deltaX = ((int)viewRect.centerX() - (int)player.playerRect.centerX());
            if (deltaX < 200 && deltaY < 200)
                cameraSpeed = playerSpeed - 2;
            distX = (Math.abs(deltaX) < 10)?0:(deltaX > 0)?cameraSpeed:-cameraSpeed;
            distY = (Math.abs(deltaY) < 10)?0:(deltaY > 0)?cameraSpeed:-cameraSpeed;
            viewRect.set((offsetX-distX)+leftCntrlOffset, offsetY-distY, (offsetX-distX)+screenWidth, (offsetY-distY)+screenHeight);
            if (viewRect.right < mapRect.right && viewRect.left > mapRect.left)
                offsetX -= distX;
            if (viewRect.top > mapRect.top && viewRect.bottom < mapRect.bottom)
                offsetY -= distY;


            // Handle Collison Events
            for (int i=0; i<fireCount; ++i)
                fireBall[i].update(player.playerRect, viewRect);
            for (int i=0; i<coinCount; ++i)
                coin[i].update(player.playerRect, viewRect);
            if (potLevel)
                potion.update(player.playerRect);
            if (Globals.fireCollision){
                Globals.fireCollision = false;
                if (!player.invincible){
                    player.msg = "%#&@!";
                    --lives;
                    if (lives == 0) checkWin();
                    for (int i=0; i<fireCount; ++i){
                        fireBall[i].generate();
                        try {Thread.sleep(500/fireCount);}
                        catch (InterruptedException e) { }
                        player.blinks = 0;
                        player.invincible = (player.blinks < 6);
                    }
                    player.posX = viewRect.centerX();
                    player.posY = viewRect.centerY();
                    player.invincible = (player.blinks < 6);
                }
            }
            if (Globals.coinCollisions > 0){
                player.msg = "+100";
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

            statusBar.update((int)offsetX, (int)offsetY, lives, score);
            controls.update((int)offsetX, (int)offsetY);
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
            if (score > highScore && !Globals.highScoreSet){
                Globals.highScoreSet = true;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toast = Toast.makeText(getContext(), "new highscore!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }

    }
}
