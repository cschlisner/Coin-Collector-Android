package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by cole on 11/30/13.
 */
public class StatusBar {
    private Rect bounds = new Rect(), textBounds = new Rect();
    private int lives, score, level, imageWidth, imageHeight;
    private Paint paint = new Paint();
    private Bitmap livesBmp;
    private String livesString = "Lives: ", scoreString = "Score: %d";
    public int height;
    public StatusBar(Context context, int level, int sw, int sh){
        livesBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.lives);
        imageWidth = livesBmp.getWidth()+5;
        imageHeight = livesBmp.getHeight();
        paint.setTextSize(40);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
        paint.getTextBounds(livesString, 0, livesString.length(), textBounds);
        bounds.set(0, 0, sw, textBounds.height() + 10);
        height = bounds.height();
        this.level = level;
    }

    public void update(int lifeCount, int points){
        lives = lifeCount;
        score = points;
    }

    public void draw(Canvas canvas){
        paint.setColor(Color.argb(255,12,10,10));
        canvas.drawRect(bounds, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(livesString, 10, bounds.bottom-5, paint);
        for (int i = 0; i<lives; ++i)
            canvas.drawBitmap(livesBmp, paint.measureText(livesString, 0, 7)+10+(i*imageWidth),
                    (bounds.bottom-imageHeight)-5, paint);
        canvas.drawText(String.format(scoreString, score),
                bounds.centerX()-50, bounds.bottom-5, paint);
        canvas.drawText(String.format(Globals.mode+": %d", level),
                bounds.width()-paint.measureText(Globals.mode, 0, Globals.mode.length())-100, bounds.bottom-7, paint);
    }
}
