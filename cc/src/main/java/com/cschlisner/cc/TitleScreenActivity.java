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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class TitleScreenActivity extends ActionBarActivity {
    private TextContainer easyButton, mediumButton, hardButton;
    private boolean startedNewActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View titleScreen = new TitleScreenView(this);
        setContentView(titleScreen);
    }
    public class TitleScreenView extends View {
        private Paint paint = new Paint();
        public int screenWidth, screenHeight, titleX = 20;
        private String title = "Coin Collector";
        public TitleScreenView(Context context){
            super(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            easyButton = new TextContainer(context, "easy mode", 60, (screenWidth/2)-(screenWidth/20),
                    (screenHeight/2)-(screenHeight/6));
            mediumButton = new TextContainer(context, "medium mode", 60, (screenWidth/2), screenHeight/2);
            hardButton = new TextContainer(context, "hard mode", 60, (screenWidth/2)+(screenWidth/20),
                    (screenHeight/2)+(screenHeight/6));

        }

        @Override
        protected void onDraw(Canvas canvas) {
            easyButton.draw(canvas);
            mediumButton.draw(canvas);
            hardButton.draw(canvas);
            paint.setTypeface(Typeface.createFromAsset(getAssets(), "robotolight.ttf"));
            paint.setTextSize(120);
            paint.setColor(Color.WHITE);
            canvas.drawText(title, titleX, 120, paint);
            if (easyButton.pressed || mediumButton.pressed || hardButton.pressed){
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

            if (easyButton.bounds.contains((int)x, (int)y)){
                easyButton.pressed = true;
                mediumButton.paint.setAlpha(0);
                hardButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (mediumButton.bounds.contains((int)x, (int)y)){
                mediumButton.pressed = true;
                easyButton.paint.setAlpha(0);
                hardButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (hardButton.bounds.contains((int)x, (int)y)){
                hardButton.pressed = true;
                mediumButton.paint.setAlpha(0);
                easyButton.paint.setAlpha(0);
                postInvalidate();
            }
            return true;
        }

        private void update(){
            if (easyButton.pressed){
                if (easyButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    easyButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity){
                    startedNewActivity = true;
                    Context context = getContext();
                    Intent i = new Intent(context, NextLevelActivity.class);
                    i.putExtra("DIFFICULTY", "easy");
                    i.putExtra("LEVEL", 1);
                    context.startActivity(i);
                    Log.d("TitleScreenActivity", "started NextLevelActivity");
                    finish();
                }
            }
            else if (mediumButton.pressed){
                if (mediumButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    mediumButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity) {
                    startedNewActivity = true;
                    Context context = getContext();
                    Intent i = new Intent(context, NextLevelActivity.class);
                    i.putExtra("DIFFICULTY", "med");
                    i.putExtra("LEVEL", 1);
                    context.startActivity(i);
                    finish();
                }
            }
            else if (hardButton.pressed){
                if (hardButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    hardButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity) {
                    startedNewActivity = true;
                    Context context = getContext();
                    Intent i = new Intent(context, NextLevelActivity.class);
                    i.putExtra("DIFFICULTY", "hard");
                    i.putExtra("LEVEL", 1);
                    context.startActivity(i);
                    finish();
                }
            }
        }
    }
}
