package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by cole on 11/30/13.
 */
public class Player {
    private Bitmap ur, ul, us,
                   rr, rl, rs,
                   lr, ll, ls,
                   dr, dl, ds;
    public GameActivity.Direction direction = GameActivity.Direction.right;
    public boolean moving = false;
    public float posX, posY;
    private Paint paint;
    public Rect playerRect = new Rect();
    public boolean walkSwitch = true;
    public String msg = " ";
    private int walkT, imgW, imgH;
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
        playerRect.set((int)posX, (int)posY, (int)posX+imgW, (int)posY+imgH);
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
