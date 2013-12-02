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
        GameOverView gameOverView = new GameOverView(getApplicationContext());
        setContentView(gameOverView);
    }
    public class GameOverView extends View {
        private Paint paint = new Paint();
        private String title = "Paused";
        private TextContainer resumeButton, menuButton, devButton;
        private int titleX, screenWidth, screenHeight;
        public GameOverView(Context context){
            super(context);
            titleX = 20;
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            resumeButton = new TextContainer("resume", 60, (screenWidth/2)-(screenWidth/20),
                    (screenHeight/2)-(screenHeight/6));
            menuButton = new TextContainer("main menu", 60, (screenWidth/2), screenHeight/2);
            devButton = new TextContainer("dev: "+Boolean.toString(Collisions.devMode), 60, (screenWidth/2)+(screenWidth/20),
                    (screenHeight/2)+(screenHeight/6));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            resumeButton.draw(canvas);
            menuButton.draw(canvas);
            devButton.draw(canvas);
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

            if (resumeButton.bounds.contains((int)x, (int)y)){
                resumeButton.pressed = true;
                menuButton.paint.setAlpha(0);
                devButton.paint.setAlpha(0);
            }
            else if (menuButton.bounds.contains((int)x, (int)y)){
                menuButton.pressed = true;
                resumeButton.paint.setAlpha(0);
                devButton.paint.setAlpha(0);
            }
            else if (devButton.bounds.contains((int)x, (int)y)){
                devButton.pressed = true;
                Collisions.devMode = !Collisions.devMode;
                menuButton.paint.setAlpha(0);
                resumeButton.paint.setAlpha(0);
            }
            return true;
        }

        private void update(){
            if (resumeButton.pressed){
                if (resumeButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    resumeButton.bounds.left += 30;
                    titleX -= 30;
                }
                else {
                    pressBack();
                    finish();
                }
            }
            else if (menuButton.pressed){
                if (menuButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    menuButton.bounds.left += 30;
                    titleX -= 30;
                }
                else {
                    startMenu();
                    finish();
                }
            }
            if (devButton.pressed){
                if (devButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    devButton.bounds.left += 30;
                    titleX -= 30;
                }
                else {
                    pressBack();
                    finish();
                }
            }
        }
    }

    public void startMenu(){
        Intent i = new Intent(this, TitleScreenActivity.class);
        startActivity(i);
    }
    public void pressBack() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {}
}
