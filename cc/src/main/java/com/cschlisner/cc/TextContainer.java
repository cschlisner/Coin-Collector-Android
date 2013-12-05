package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;


/**
 * Created by cole on 11/27/13.
 */
public class TextContainer {
    public Rect bounds = new Rect();
    public Paint paint = new Paint();
    public boolean pressed;
    public Rect textBounds = new Rect();
    private String text;
    public TextContainer(Context context, String Text, int Size, int x, int y){
        paint.setTextSize(Size);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
        paint.setColor(Color.WHITE);
        text = Text;
        paint.getTextBounds(text, 0, text.length(), textBounds);
        bounds.set(x-10, y-10, (x+textBounds.right)+10, (y+textBounds.height())+10);
    }

    public void draw(Canvas canvas){
        canvas.drawText(text, bounds.left+10, bounds.top+textBounds.height()+10, paint);
    }
}
