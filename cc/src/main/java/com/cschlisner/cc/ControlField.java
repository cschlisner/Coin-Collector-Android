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
    public Bitmap Ri, Le, Up, Do, Nu, imgR, imgL, imgU, imgD, imgN, gl, glow;
    public Rect bounds, holder, center;
    public GameActivity.Direction direction = GameActivity.Direction.none;
    private Paint paint;
    private boolean rightSide;
    private int screenW, screenH, scnPct, dPadX, dPadY;
    private int top, left, right, bott;
    public TextContainer pauseButton;

    public ControlField(Context context, int screenW, int screenH, float screenPercent, boolean rightSide){
        scnPct = Math.round(100/screenPercent);
        this.screenW = screenW;
        this.screenH = screenH;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "robotolight.ttf"));
        Ri = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadr);
        Le = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadl);
        Up = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadu);
        Do = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadd);
        Nu = BitmapFactory.decodeResource(context.getResources(), R.drawable.dpadn);
        imgR = Bitmap.createScaledBitmap(Ri, screenW / scnPct, screenW / scnPct, true);
        imgL = Bitmap.createScaledBitmap(Le, screenW/scnPct, screenW/scnPct, true);
        imgU = Bitmap.createScaledBitmap(Up, screenW/scnPct, screenW/scnPct, true);
        imgD = Bitmap.createScaledBitmap(Do, screenW/scnPct, screenW/scnPct, true);
        imgN = Bitmap.createScaledBitmap(Nu, screenW/scnPct, screenW/scnPct, true);
        gl = BitmapFactory.decodeResource(context.getResources(), R.drawable.canvasglow);
        glow = Bitmap.createScaledBitmap(gl, screenW / 80, screenH, true);
        holder = new Rect();
        bounds = new Rect();
        center = new Rect();
        if (rightSide)
            holder.set(screenW-(screenW/scnPct), 0, screenW, screenH);
        else holder.set(0, 0, (screenW/scnPct), screenH);
        dPadX = holder.left+(holder.width() - imgN.getWidth())/2;
        dPadY = holder.height()/2;
        this.rightSide = rightSide;
        bounds.set(dPadX, dPadY, dPadX+imgN.getWidth(), dPadY+imgN.getHeight());
        center.set(bounds.centerX()-(bounds.width()/8), bounds.centerY()-(bounds.height()/8),
                bounds.centerX()+(bounds.width()/8), bounds.centerY()+(bounds.height()/8));
        pauseButton = new TextContainer(context, "ll", 80, holder.centerX()-20, holder.height()/4);
    }
    
    public void setDirection(int x, int y){
        top = bounds.top;
        right = bounds.right;
        left = bounds.left;
        bott = bounds.bottom;
        if (center.contains(x,y))
            direction = GameActivity.Direction.none;
        else if ((bott-y) > (right-x) && (bott-y) > (x-left)){
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

    public void update(int x, int y){
        if (rightSide)
            holder.set(x+(screenW-(screenW/scnPct)), y, x+screenW, y+screenH);
        else holder.set(x, y, x+(screenW/scnPct), y+screenH);
        dPadX = holder.left+(holder.width() - imgN.getWidth())/2;
        dPadY = y+holder.height()/2;
        bounds.set(dPadX, dPadY, dPadX+imgN.getWidth(), dPadY+imgN.getHeight());
        center.set(bounds.centerX()-(bounds.width()/8), bounds.centerY()-(bounds.height()/8),
                bounds.centerX()+(bounds.width()/8), bounds.centerY()+(bounds.height()/8));
        int btnX=holder.centerX()-20, btnY=y+holder.height()/4;
        pauseButton.bounds.set(btnX, btnY, (btnX+pauseButton.textBounds.right)+10, (btnY+pauseButton.textBounds.height())+10);
        bounds.set(dPadX, dPadY, dPadX+imgN.getWidth(), dPadY+imgN.getHeight());
        center.set(bounds.centerX()-(bounds.width()/8), bounds.centerY()-(bounds.height()/8),
                bounds.centerX()+(bounds.width()/8), bounds.centerY()+(bounds.height()/8));
    }

    public void draw(Canvas canvas){
        paint.setColor(Color.BLACK);
        canvas.drawRect(holder, paint);
        canvas.drawBitmap(glow, (rightSide) ? holder.left : holder.right, holder.top, paint);
        pauseButton.draw(canvas);
        switch (direction){
            case up:
                canvas.drawBitmap(imgU, dPadX, dPadY, paint);
                break;
            case right:
                canvas.drawBitmap(imgR, dPadX, dPadY, paint);
                break;
            case left:
                canvas.drawBitmap(imgL, dPadX, dPadY, paint);
                break;
            case down:
                canvas.drawBitmap(imgD, dPadX, dPadY, paint);
                break;
            case none:
                canvas.drawBitmap(imgN, dPadX, dPadY, paint);
                break;
        }
    }
}
