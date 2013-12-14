package com.cschlisner.cc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    private TextContainer easyButton, mediumButton, hardButton, optionsButton;
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
        private Paint titlePaint = new Paint(), scorePaint = new Paint();
        public int screenWidth, screenHeight, titleX = 20, easyScore, medScore, hardScore;
        private String title = "Coin Collector";
        private Bitmap logo;
        private Matrix matrix = new Matrix();
        private float lx, ly, lscale=0.3f;
        public TitleScreenView(Context context){
            super(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
            logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            easyButton = new TextContainer(context, "easy mode", 60, (screenWidth/2)-(screenWidth/20),
                    (screenHeight/2)-(screenHeight/6));
            mediumButton = new TextContainer(context, "medium mode", 60, (screenWidth/2), screenHeight/2);
            hardButton = new TextContainer(context, "hard mode", 60, (screenWidth/2)+(screenWidth/20),
                    (screenHeight/2)+(screenHeight/6));
            optionsButton = new TextContainer(context, "options", 60, (screenWidth/2)+(screenWidth/10),
                    (screenHeight/2)+(screenHeight/3));
            scorePaint.setTextSize(50);
            scorePaint.setColor(Color.GRAY);
            titlePaint.setTextSize(120);
            titlePaint.setColor(Color.WHITE);
            SharedPreferences scores = context.getSharedPreferences("highScore", Context.MODE_PRIVATE);
            easyScore = scores.getInt("easy", 0);
            medScore = scores.getInt("medium", 0);
            hardScore = scores.getInt("hard", 0);
            Globals.hse = easyScore;
            Globals.hsm = medScore;
            Globals.hsh = hardScore;
            SharedPreferences settings = context.getSharedPreferences("options", Context.MODE_PRIVATE);
            Globals.controlSize = settings.getInt("ctrlSize", 1);
            Globals.controlsRight = settings.getBoolean("ctrlSide", true);
            titlePaint.setTypeface(Typeface.createFromAsset(getAssets(), "robotolight.ttf"));
            lx = 100;
            ly = ((screenHeight/2)+(screenHeight/6))-(logo.getHeight()*lscale)+100;
            matrix.reset();
            matrix.setTranslate(lx, ly);
            matrix.preScale(lscale, lscale);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            easyButton.draw(canvas);
            canvas.drawText(String.format("%d", easyScore), ((screenWidth/2)-(screenWidth/20))+easyButton.bounds.width(),
                    (screenHeight/2)-(screenHeight/6)+easyButton.textBounds.height(), scorePaint);
            mediumButton.draw(canvas);
            canvas.drawText(String.format("%d", medScore), (screenWidth/2)+mediumButton.bounds.width(),
                    (screenHeight/2)+mediumButton.textBounds.height(), scorePaint);
            hardButton.draw(canvas);
            canvas.drawText(String.format("%d", hardScore), ((screenWidth/2)+(screenWidth/20))+hardButton.bounds.width(),
                    (screenHeight/2)+(screenHeight/6)+hardButton.textBounds.height(), scorePaint);
            optionsButton.draw(canvas);
            canvas.drawText(title, titleX, 120, titlePaint);
            canvas.drawBitmap(logo, matrix, null);
            if (easyButton.pressed || mediumButton.pressed || hardButton.pressed || optionsButton.pressed){
                scorePaint.setAlpha(0);
                update();
                matrix.postRotate(12, lx+((logo.getWidth()*lscale)/2), ly+((logo.getHeight()*lscale)/2));
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
                optionsButton.paint.setAlpha(0);
                mediumButton.paint.setAlpha(0);
                hardButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (mediumButton.bounds.contains((int)x, (int)y)){
                mediumButton.pressed = true;
                optionsButton.paint.setAlpha(0);
                easyButton.paint.setAlpha(0);
                hardButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (hardButton.bounds.contains((int)x, (int)y)){
                hardButton.pressed = true;
                optionsButton.paint.setAlpha(0);
                mediumButton.paint.setAlpha(0);
                easyButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (optionsButton.bounds.contains((int)x, (int)y)){
                optionsButton.pressed = true;
                hardButton.paint.setAlpha(0);
                mediumButton.paint.setAlpha(0);
                easyButton.paint.setAlpha(0);
                postInvalidate();
            }
            return true;
        }

        private void update(){
            if (easyButton.pressed){
                if (easyButton.bounds.left <= screenWidth || titleX >= 0-titlePaint.measureText(title)){
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
                if (mediumButton.bounds.left <= screenWidth || titleX >= 0-titlePaint.measureText(title)){
                    mediumButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity) {
                    startedNewActivity = true;
                    Context context = getContext();
                    Intent i = new Intent(context, NextLevelActivity.class);
                    i.putExtra("DIFFICULTY", "medium");
                    i.putExtra("LEVEL", 1);
                    context.startActivity(i);
                    finish();
                }
            }
            else if (hardButton.pressed){
                if (hardButton.bounds.left <= screenWidth || titleX >= 0-titlePaint.measureText(title)){
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
            else if (optionsButton.pressed){
                if (optionsButton.bounds.left <= screenWidth || titleX >= 0-titlePaint.measureText(title)){
                    optionsButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startedNewActivity) {
                    startedNewActivity = true;
                    Context context = getContext();
                    Intent i = new Intent(context, OptionsActivity.class);
                    i.putExtra("CALLER", "TitleScreenActivity");
                    context.startActivity(i);
                    finish();
                }
            }
        }
    }
}
