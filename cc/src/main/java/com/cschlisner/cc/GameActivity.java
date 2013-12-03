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

public class GameActivity extends ActionBarActivity{
    private static final String TAG = GameActivity.class.getSimpleName();
    private boolean inPause;
    public enum Direction {up, down, left, right, none}
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

    public class GameView extends View{
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

        public GameView(Context context){
            super(context);
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
        }

        // set up control field
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
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

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

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
            if (!statusBar.pauseButton.pressed){
                update();
                try {Thread.sleep(15);}
                catch (InterruptedException e) { }
                invalidate();
            }
            else {
                statusBar.pauseButton.pressed = false;
                if (!inPause){
                startActivity(new Intent(getContext(), PauseActivity.class));
                inPause = true;
                Log.d(TAG,"Paused");
                }
            }
        }

        public void update(){
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
                for (int i=0; i<fireCount; ++i){
                    fireBall[i].generate();
                    try {Thread.sleep(500/fireCount);}
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
            if (coinsCollected == coinCount){
                Intent i = new Intent(getContext(), NextLevelActivity.class);
                i.putExtra("DIFFICULTY", difficulty);
                i.putExtra("LEVEL", level+1);
                i.putExtra("SPEED", playerSpeed-7);
                i.putExtra("SCORE", score);
                getContext().startActivity(i);
                finish();
            }
            else if (lives <= 0){
                Intent i = new Intent(getContext(), GameOverActivity.class);
                getContext().startActivity(i);
                finish();
            }
        }

    }

    @Override
    protected void onPause() {
        if (!inPause){
            startActivity(new Intent(this, PauseActivity.class));
            Log.d(TAG, "Paused from onPause");
            inPause = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        inPause = false;
        super.onResume();
    }
}
