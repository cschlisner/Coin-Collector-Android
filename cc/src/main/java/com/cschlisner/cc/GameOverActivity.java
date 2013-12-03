package com.cschlisner.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
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
        private TextContainer menuButton;
        private int titleX, screenWidth, screenHeight;
        public GameOverView(Context context){
            super(context);
            titleX = 20;
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            menuButton = new TextContainer("main menu", 60, (screenWidth/2), screenHeight/2);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            menuButton.draw(canvas);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(120);
            paint.setColor(Color.WHITE);
            canvas.drawText(title, titleX, 120, paint);
            update();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) { }
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent e){
            float x = e.getX();
            float y = e.getY();

            if (menuButton.bounds.contains((int)x, (int)y)){
                menuButton.pressed = true;
            }
            return true;
        }

        private void update(){
            if (menuButton.pressed){
                if (menuButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    menuButton.bounds.left += 30;
                    titleX -= 30;
                }
                else {
                    startMenu();
                    finish();
                }
            }
        }
    }

    public void startMenu(){
        Intent i = new Intent(this, TitleScreenActivity.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {}
}
