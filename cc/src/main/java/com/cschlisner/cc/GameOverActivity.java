package com.cschlisner.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameOverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GameOverView gameOverView = new GameOverView(this);
        setContentView(gameOverView);
    }
    public class GameOverView extends View {
        private Paint paint = new Paint();
        private String title = "You Died!";
        private TextContainer restartButton, menuButton;
        private int titleX, screenWidth, screenHeight, gameScore, oldScore;
        private boolean startedNewActivity;
        public GameOverView(Context context){
            super(context);
            gameScore = getIntent().getIntExtra("SCORE", 0);
            titleX = 20;
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            restartButton = new TextContainer(context, "restart", 60, (screenWidth/2)-(screenWidth/20),
                    (screenHeight/2)-(screenHeight/6));
            menuButton = new TextContainer(context, "main menu", 60, (screenWidth/2), screenHeight/2);
            paint.setTypeface(Typeface.createFromAsset(getAssets(), "robotolight.ttf"));
            paint.setTextSize(120);
            paint.setColor(Color.WHITE);
            // high scores
            Globals.highScoreSet = false;
            SharedPreferences prefs = context.getSharedPreferences("highScore", Context.MODE_PRIVATE);
            oldScore = prefs.getInt(Globals.mode, 0);
            if(gameScore > oldScore ){
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt(Globals.mode, gameScore);
                edit.commit();
            }
        }
        @Override
        protected void onDraw(Canvas canvas) {
            restartButton.draw(canvas);
            menuButton.draw(canvas);
            paint.setTextSize(120);
            paint.setColor(Color.WHITE);
            canvas.drawText(title, titleX, 120, paint);
            paint.setTextSize(50);
            paint.setColor(Color.GRAY);
            canvas.drawText(Globals.mode+":", titleX, (screenHeight / 2)-10, paint);
            canvas.drawText(String.format("high score: %d", oldScore), titleX, (screenHeight / 2)+40, paint);
            canvas.drawText(String.format("your score: %d", gameScore), titleX, (screenHeight/2)+90, paint);
            if (restartButton.pressed || menuButton.pressed){
                update();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }
                invalidate();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e){
            float x = e.getX();
            float y = e.getY();

            if (restartButton.bounds.contains((int)x, (int)y)){
                restartButton.pressed = true;
                menuButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (menuButton.bounds.contains((int)x, (int)y)){
                menuButton.pressed = true;
                restartButton.paint.setAlpha(0);
                postInvalidate();
            }
            return true;
        }

        private void update(){
            if (restartButton.pressed){
                if (restartButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    restartButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity){
                    startedNewActivity = true;
                    Context context = getContext();
                    Intent i = new Intent(context, NextLevelActivity.class);
                    i.putExtra("DIFFICULTY", Globals.mode);
                    i.putExtra("LEVEL", 1);
                    context.startActivity(i);
                    finish();
                }
            }
            else if (menuButton.pressed){
                if (menuButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    menuButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity){
                    startedNewActivity = true;
                    Intent i = new Intent(getContext(), TitleScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {}
}
