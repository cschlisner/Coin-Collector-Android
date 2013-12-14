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
    private Bitmap dieSheet, walkSheet;
    public GameActivity.Direction direction = GameActivity.Direction.right;
    private boolean alphaSwitch = false;
    public int blinks;
    public float posX, posY;
    private Paint paint;
    public RectF playerRect = new RectF();
    public boolean moving = false, invincible, isDead;
    public String msg = "";
    private int walkT, frameCol = 1, walkW, walkH, blinkT, dieFrame, talkT;
    public Player(Context context, int sw, int sh){
        walkSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.walksheet);
        dieSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.diesheet);
        //walkSheet = Bitmap.createScaledBitmap(owalkSheet, 180, 120, true);
        //scale to about 1/71 of screenW, and 1/32 of screenH
        paint = new Paint();
        paint.setColor(Color.WHITE);
        walkW = walkSheet.getWidth()/9;
        walkH = walkSheet.getHeight()/4;
    }

    private void walk(Canvas canvas, GameActivity.Direction dir){
        int frameRow, srcX, srcY;
        switch (dir){
            case up:
                frameRow = 0;
                break;
            case left:
                frameRow = 1;
                break;
            case down:
                frameRow = 2;
                break;
            case right:
                frameRow = 3;
                break;
            default:
                frameRow = 0;
                break;
        }
        if (moving){
            if (frameCol==0) frameCol = 1; //set it to the first walk frame
            ++walkT;
            if (walkT >= 2){
                if (frameCol < 8) ++frameCol;
                else frameCol = 1;
                walkT = 0;
            }
        }
        else frameCol = 0;
        srcX = frameCol*walkW;
        srcY = frameRow*walkH;
        Rect src = new Rect(srcX, srcY, srcX + walkW, srcY + walkH);
        Rect dst = new Rect((int)posX, (int)posY, (int)posX+walkW, (int)posY+walkH);
        canvas.drawBitmap(walkSheet, src, dst, paint);
    }
    
    private void die(Canvas canvas){
        int dieW = dieSheet.getWidth()/6;
        int dieH = dieSheet.getHeight();
        ++walkT;
        if (walkT >= 4){
            if (dieFrame < 5) ++dieFrame;
            walkT = 0;
        }
        int srcX = dieFrame*dieW;
        Rect src = new Rect(srcX, 0, srcX + dieW, dieH);
        Rect dst = new Rect((int)posX, (int)posY, (int)posX+dieW, (int)posY+dieH);
        canvas.drawBitmap(dieSheet, src, dst, paint);
        if (dieFrame == 5){
            dieFrame = 0;
            isDead = false;
        }
        
    }
    
    public void draw(Canvas canvas){
        playerRect.set(posX+3, posY+4, posX+walkW-3, posY+walkH-4);
        if (invincible){
            ++blinkT;
            if (blinkT > 10){
                alphaSwitch = !alphaSwitch;
                paint.setAlpha((alphaSwitch)?125:255);
                blinkT = 0;
                ++blinks;
            }
        }
        if (msg != ""){
            ++talkT;
            if (talkT > 10){
                msg = "";
                talkT = 0;
            }
        }
        if (!isDead)walk(canvas, direction);
        else die(canvas);
    }
}
