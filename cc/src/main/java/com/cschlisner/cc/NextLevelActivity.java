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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class NextLevelActivity extends Activity {
    private String mode;
    private int level, timer = 3, updates, lives, coins, score, fireSpeed,
            fireCount, playerSpeed, playerSpeedGained, screenWidth, screenHeight, highscore;
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
        private Bitmap livesBMP, coinBMP, fireBMP1, fireBMP2;
        private boolean startedActivity, fireSwitch;
        private int draws, animate, switchImage, imgW, imgH;
        private Rect src, dst;
        public NextLevelView(Context context){
            super(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            livesBMP = BitmapFactory.decodeResource(getResources(), R.drawable.lives);
            coinBMP = BitmapFactory.decodeResource(getResources(), R.drawable.coinsheet);
            fireBMP1 = BitmapFactory.decodeResource(getResources(), R.drawable.fire1);
            fireBMP2 = BitmapFactory.decodeResource(getResources(), R.drawable.fire2);
            imgW = coinBMP.getWidth()/8;
            imgH = coinBMP.getHeight();
            Intent intent = getIntent();
            mode = intent.getStringExtra("DIFFICULTY");
            Globals.mode = mode;
            level = intent.getIntExtra("LEVEL", 1);
            playerSpeedGained = intent.getIntExtra("SPEED", 0);
            score = intent.getIntExtra("SCORE", 0);
            if (mode.equals("easy")){
                lives = 4 + (level/3);
                coins = 10 + 2*level;
                fireSpeed = 8;
                fireCount = 30 + level*3;
                playerSpeed = 7 + playerSpeedGained;
                highscore = Globals.hse;
            }
            else if (mode.equals("medium")){
                lives = 5+ (level/3);
                coins = 15 + 2*level;
                fireSpeed = 9;
                fireCount = 35 + level*3;
                playerSpeed = 7 + playerSpeedGained;
                highscore = Globals.hsm;
            }
            else if (mode.equals("hard")){
                lives = 6 + (level/3);
                coins = 20 + 2*level;
                fireSpeed = 10;
                fireCount = 40 + level*3;
                playerSpeed = 7 + playerSpeedGained;
                highscore = Globals.hsh;
            }
            paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
            paint.setColor(Color.WHITE);
            src = new Rect(imgW*switchImage, 0, (imgW*switchImage)+imgW, imgH);
            dst = new Rect(screenWidth / 2, screenHeight / 2,  (screenWidth / 2)+imgW, (screenHeight / 2)+imgH);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            paint.setTextSize(40);
            canvas.drawText(mode+" - "+Integer.toString(level), 40, 40, paint);
            paint.setTextSize(60);
            canvas.drawBitmap(livesBMP, screenWidth / 4, screenHeight / 2, null);
            canvas.drawText(Integer.toString(lives), (screenWidth/4), screenHeight/2 + screenHeight/4, paint);
            canvas.drawBitmap(coinBMP, src, dst, null);
            canvas.drawText(Integer.toString(coins), (screenWidth / 2), screenHeight / 2 + screenHeight / 4, paint);
            if (fireSwitch) canvas.drawBitmap(fireBMP1, screenWidth / 2 + screenWidth / 4, screenHeight / 2, null);
            else canvas.drawBitmap(fireBMP2, screenWidth / 2 + screenWidth / 4, screenHeight / 2, null);
            canvas.drawText(Integer.toString(fireCount), screenWidth/2+screenWidth/4, screenHeight/2 + screenHeight/4, paint);

            paint.setTextSize(100);
            canvas.drawText(String.format("%d", timer),  screenWidth/2, screenHeight/2-screenHeight/4, paint);
            ++draws;
            ++animate;
            if (animate == 5){
                fireSwitch = !fireSwitch;
                if (switchImage < 7) ++switchImage;
                else switchImage = 0;
                src.set(imgW*switchImage, 0, (imgW*switchImage)+imgW, imgH);
                dst.set(screenWidth / 2, screenHeight / 2,  (screenWidth / 2)+imgW, (screenHeight / 2)+imgH);
                animate = 0;
            }
            if (draws >= ((timer == 0)?0:50)){
                --timer;
                draws = 0;
            }
            if (timer < 0){
                if (!startedActivity){
                    startedActivity = true;
                    Intent i = new Intent(getContext(), GameActivity.class);
                    i.putExtra("LEVEL", level);
                    i.putExtra("LIVES", lives);
                    i.putExtra("COINS", coins);
                    i.putExtra("FIRESPEED", fireSpeed);
                    i.putExtra("FIRECOUNT", fireCount);
                    i.putExtra("PLAYERSPEED", playerSpeed);
                    i.putExtra("DIFFICULTY", mode);
                    i.putExtra("SCORE", score);
                    i.putExtra("HSCORE", highscore);
                    startActivity(i);
                    finish();
                }
            }
            else invalidate();
        }
    }
}
