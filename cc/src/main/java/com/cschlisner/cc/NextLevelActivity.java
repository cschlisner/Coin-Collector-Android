package com.cschlisner.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class NextLevelActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        NextLevelView nextLevelView = new NextLevelView(this);
        setContentView(nextLevelView);
    }

    public class NextLevelView extends View {
        private Paint paint = new Paint();
        private String mode;
        private int level, timer = 3, updates, lives, coins, score, fireSpeed,
                fireCount, playerSpeed, playerSpeedGained, screenWidth, screenHeight;
        private Bitmap livesBMP, coinBMP, fireBMP;
        public NextLevelView(Context context){
            super(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            livesBMP = BitmapFactory.decodeResource(getResources(), R.drawable.lives);
            coinBMP = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
            fireBMP = BitmapFactory.decodeResource(getResources(), R.drawable.fireball);

            Intent intent = getIntent();
            mode = intent.getStringExtra("DIFFICULTY");
            level = intent.getIntExtra("LEVEL", 1);
            playerSpeedGained = intent.getIntExtra("SPEED", 0);
            score = intent.getIntExtra("SCORE", 0);
            if (mode.equals("easy")){
                lives = 4 + (level/3);
                coins = 10 + level;
                fireSpeed = 8;
                fireCount = 9 + level;
                playerSpeed = 4 + playerSpeedGained;
            }
            else if (mode.equals("med")){
                lives = 5 + level;
                coins = 11 + level;
                fireSpeed = 9;
                fireCount = 10 + level;
                playerSpeed = 5 + playerSpeedGained;
            }
            else if (mode.equals("hard")){
                lives = 6 + level;
                coins = 12 + level;
                fireSpeed = 10;
                fireCount = 11 + level;
                playerSpeed = 6 + playerSpeedGained;
            }
            paint.setTypeface(Typeface.DEFAULT);
            paint.setColor(Color.WHITE);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            paint.setTextSize(40);
            canvas.drawText(mode+" - "+Integer.toString(level), 40, 40, paint);
            paint.setTextSize(60);
            canvas.drawBitmap(livesBMP, screenWidth / 4, screenHeight / 2, null);
            canvas.drawText(Integer.toString(lives), (screenWidth/4), screenHeight/2 + screenHeight/4, paint);
            canvas.drawBitmap(coinBMP, screenWidth / 2, screenHeight / 2, null);
            canvas.drawText(Integer.toString(coins), (screenWidth / 2), screenHeight / 2 + screenHeight / 4, paint);
            canvas.drawBitmap(fireBMP, screenWidth / 2 + screenWidth / 4, screenHeight / 2, null);
            canvas.drawText(Integer.toString(fireCount), screenWidth/2+screenWidth/4, screenHeight/2 + screenHeight/4, paint);

            paint.setTextSize(100);
            canvas.drawText(String.format("%d", timer),  screenWidth/2, screenHeight/2-screenHeight/4, paint);
            update();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) { }
            invalidate();
        }

        private void update(){
            ++updates;
            if (updates >= 35){
                --timer;
                updates = 0;
            }
            if (timer == 0) {
                Intent i = new Intent(getContext(), GameActivity.class);
                i.putExtra("LEVEL", level);
                i.putExtra("LIVES", lives);
                i.putExtra("COINS", coins);
                i.putExtra("FIRESPEED", fireSpeed);
                i.putExtra("FIRECOUNT", fireCount);
                i.putExtra("PLAYERSPEED", playerSpeed);
                i.putExtra("DIFFICULTY", mode);
                i.putExtra("SCORE", score);
                getContext().startActivity(i);
                finish();
            }
        }
    }

}