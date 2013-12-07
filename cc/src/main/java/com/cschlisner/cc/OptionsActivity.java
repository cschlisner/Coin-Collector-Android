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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class OptionsActivity extends Activity {
    private String lastActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PauseView pauseView = new PauseView(this);
        setContentView(pauseView);
    }
    public class PauseView extends View {
        private Paint paint = new Paint();
        private String title = "Options", cSizeText, cSideText;
        private TextContainer controlsButton, aboutButton, controlSideButton, controlSizeButton;
        private int titleX, screenWidth, screenHeight, controlSize, controlSide;
        private boolean startNewActivity, controlOptions = true;
        public PauseView(Context context){
            super(context);
            lastActivity = getIntent().getStringExtra("CALLER");
            titleX = 20;
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            controlsButton = new TextContainer(context, "controls", 60, titleX,
                    (screenHeight/2)-(screenHeight/6));
            aboutButton = new TextContainer(context, "about", 60, titleX, screenHeight/2);
            cSideText = (Globals.controlsRight)?"side: right":"side: left";
            controlSideButton = new TextContainer(context, cSideText, 60, (screenWidth/2)-(screenWidth/20),
                    (screenHeight/2)-(screenHeight/6));
            cSizeText = String.format("control size: %d", Globals.controlSize+1);
            controlSizeButton = new TextContainer(context, cSizeText, 60, (screenWidth/2), (screenHeight/2));
            paint.setTypeface(Typeface.createFromAsset(getAssets(), "robotolight.ttf"));
            paint.setTextSize(120);
            paint.setColor(Color.WHITE);
            aboutButton.paint.setAlpha(130);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            controlsButton.draw(canvas);
            aboutButton.draw(canvas);
            if (controlOptions){
                controlSizeButton.draw(canvas);
                controlSideButton.draw(canvas);
            }
            canvas.drawText(title, titleX, 120, paint);
            if (controlSizeButton.pressed || controlSideButton.pressed){
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

            if (controlsButton.bounds.contains((int)x, (int)y)){
                controlsButton.paint.setAlpha(255);
                controlOptions = true;
                aboutButton.paint.setAlpha(130);
                postInvalidate();
            }
            else if (aboutButton.bounds.contains((int)x, (int)y)){
                aboutButton.paint.setAlpha(255);
                controlOptions = false;
                controlsButton.paint.setAlpha(130);
                postInvalidate();
            }
            if (controlOptions){
                if (controlSizeButton.bounds.contains((int)x, (int)y)){
                    controlSizeButton.pressed = true;
                    postInvalidate();
                }
                else if (controlSideButton.bounds.contains((int)x, (int)y)){
                    controlSideButton.pressed = true;
                    postInvalidate();
                }
            }
            return true;
        }

        private void update(){
            if (controlSizeButton.pressed){
                if (controlSizeButton.bounds.left <= screenWidth){
                    controlSizeButton.bounds.left += 30;
                }
                else {
                    if (Globals.controlSize < 2) ++Globals.controlSize;
                    else Globals.controlSize = 0;
                    cSizeText = String.format("control size: %d", Globals.controlSize+1);
                    controlSizeButton.reset(cSizeText, 60, (screenWidth / 2), (screenHeight / 2));
                }
            }
            else if (controlSideButton.pressed){
                if (controlSideButton.bounds.left <= screenWidth){
                    controlSideButton.bounds.left += 30;
                }
                else{
                    Globals.controlsRight = !Globals.controlsRight;
                    cSideText = (Globals.controlsRight)?"side: right":"side: left";
                    controlSideButton.reset(cSideText, 60, (screenWidth/2)-(screenWidth/20),
                            (screenHeight/2)-(screenHeight/6));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (lastActivity.equals("TitleScreenActivity")){
            Intent i = new Intent(this, TitleScreenActivity.class);
            startActivity(i);
            finish();
        }
        else {
        super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onPause() {
        SharedPreferences prefs = getSharedPreferences("options", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("ctrlSize", Globals.controlSize);
        edit.putBoolean("ctrlSide", Globals.controlsRight);
        edit.commit();
        super.onPause();
    }
}
