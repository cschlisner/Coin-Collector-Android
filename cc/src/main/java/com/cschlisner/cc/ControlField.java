package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by cole on 11/29/13.
 */
public class ControlField {
    public Bitmap Ri, Le, Up, Do, Nu, imgR, imgL, imgU, imgD, imgN;
    public Rect bounds, holder;
    public GameActivity.Direction direction = GameActivity.Direction.none;
    private Paint paint;
    private int posX, posY;
    
    public ControlField(Context context, int screenW, int screenH){
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
        Ri = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadr);
        Le = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadl);
        Up = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadu);
        Do = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadd);
        Nu = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadn);
        imgR = Bitmap.createScaledBitmap(Ri, screenW/8, screenW/8, true);
        imgL = Bitmap.createScaledBitmap(Le, screenW/8, screenW/8, true);
        imgU = Bitmap.createScaledBitmap(Up, screenW/8, screenW/8, true);
        imgD = Bitmap.createScaledBitmap(Do, screenW/8, screenW/8, true);
        imgN = Bitmap.createScaledBitmap(Nu, screenW/8, screenW/8, true);
        holder = new Rect();
        bounds = new Rect();
        holder.set(screenW-(screenW/6), 0, screenW, screenH);
        posX = holder.left+(holder.width()/5);
        posY = holder.height()/2;
        bounds.set(posX, posY, posX+imgN.getWidth(), posY+imgN.getHeight());
    }
    
    public void setDirection(int x, int y){
        int top = bounds.top, right = bounds.right, left = bounds.left, bott = bounds.bottom;
        if ((bott-y) > (right-x) && (bott-y) > (x-left)){
            direction = GameActivity.Direction.up;
        }
        else if ((x-left) > (bott-y) && (x-left) > (y-top)){
            direction = GameActivity.Direction.right;
        }
        else if ((y-top) > (right-x) && (y-top) > (x-left)){
            direction = GameActivity.Direction.down;
        }
        else if ((right-x) > (bott-y) && (right-x) > (y-top)){
            direction = GameActivity.Direction.left;
        }
    }

    public void draw(Canvas canvas){
        paint.setColor(Color.BLACK);
        canvas.drawRect(holder, paint);
        switch (direction){
            case up:
                canvas.drawBitmap(imgU, posX, posY, paint);
                break;
            case right:
                canvas.drawBitmap(imgR, posX, posY, paint);
                break;
            case left:
                canvas.drawBitmap(imgL, posX, posY, paint);
                break;
            case down:
                canvas.drawBitmap(imgD, posX, posY, paint);
                break;
            case none:
                canvas.drawBitmap(imgN, posX, posY, paint);
                break;
        }
    }
}
