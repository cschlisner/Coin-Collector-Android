package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by cole on 11/30/13.
 */
public class Player {
    private Bitmap ur, ul, us,
                   rr, rl, rs,
                   lr, ll, ls,
                   dr, dl, ds;
    public GameActivity.Direction direction = GameActivity.Direction.right;
    private boolean alphaSwitch = false;
    public int blinks;
    public float posX, posY;
    private Paint paint;
    public RectF playerRect = new RectF();
    public boolean walkSwitch = true, moving = false, invincible;
    public String msg = " ";
    private int walkT, blinkT, imgW, imgH;
    public Player(Context context){
        ur = BitmapFactory.decodeResource(context.getResources(), R.drawable.ur);
        ul = BitmapFactory.decodeResource(context.getResources(), R.drawable.ul);
        us = BitmapFactory.decodeResource(context.getResources(), R.drawable.us);
        rr = BitmapFactory.decodeResource(context.getResources(), R.drawable.rr);
        rl = BitmapFactory.decodeResource(context.getResources(), R.drawable.rl);
        rs = BitmapFactory.decodeResource(context.getResources(), R.drawable.rs);
        lr = BitmapFactory.decodeResource(context.getResources(), R.drawable.lr);
        ll = BitmapFactory.decodeResource(context.getResources(), R.drawable.ll);
        ls = BitmapFactory.decodeResource(context.getResources(), R.drawable.ls);
        dr = BitmapFactory.decodeResource(context.getResources(), R.drawable.dr);
        dl = BitmapFactory.decodeResource(context.getResources(), R.drawable.dl);
        ds = BitmapFactory.decodeResource(context.getResources(), R.drawable.ds);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        imgW = ur.getWidth();
        imgH = ur.getHeight();

    }

    private void walk(Canvas canvas, Bitmap a, Bitmap b, Bitmap c){
        if (moving){
            ++walkT;
            if (walkT >= 7){
                walkSwitch = !walkSwitch;
                walkT = 0;
            }
            if (walkSwitch) canvas.drawBitmap(a, posX, posY, paint);
            else canvas.drawBitmap(b, posX, posY, paint);
        }
        else canvas.drawBitmap(c, posX, posY, paint);
    }

    public void draw(Canvas canvas){
        playerRect.set(posX, posY, posX+imgW, posY+imgH);
        if (invincible){
            ++blinkT;
            if (blinkT > 10){
                alphaSwitch = !alphaSwitch;
                paint.setAlpha((alphaSwitch)?0:255);
                blinkT = 0;
                ++blinks;
            }
        }
        switch (direction){
            case up:
                walk(canvas, ur, ul, us);
                break;
            case right:
                walk(canvas, rr, rl, rs);
                break;
            case left:
                walk(canvas, lr, ll, ls);
                break;
            case down:
                walk(canvas, dr, dl, ds);
                break;
        }
    }
}
