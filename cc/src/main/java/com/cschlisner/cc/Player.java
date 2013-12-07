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
    private Bitmap sheet;
    public GameActivity.Direction direction = GameActivity.Direction.right;
    private boolean alphaSwitch = false;
    public int blinks;
    public float posX, posY;
    private Paint paint;
    public RectF playerRect = new RectF();
    public boolean walkSwitch = true, moving = false, invincible;
    public String msg = " ";
    private int walkT, frameCol = 1, blinkT, imgW, imgH;
    public Player(Context context){
        sheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.charsheet);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        imgW = sheet.getWidth()/3;
        imgH = sheet.getHeight()/4;

    }

    private void walk(Canvas canvas, GameActivity.Direction dir){
        int frameRow, srcX, srcY;
        switch (dir){
            case down:
                frameRow = 0;
                break;
            case right:
                frameRow = 1;
                break;
            case up:
                frameRow = 2;
                break;
            case left:
                frameRow = 3;
                break;
            default:
                frameRow = 0;
                break;
        }
        if (moving){
            if (frameCol==1) frameCol = 0;
            ++walkT;
            if (walkT >= 7){
                frameCol = (frameCol == 0)?2:0;
                walkT = 0;
            }
        }
        else frameCol = 1;

        srcX = frameCol*imgW;
        srcY = frameRow*imgH;
        Rect src = new Rect(srcX, srcY, srcX + imgW, srcY + imgH);
        Rect dst = new Rect((int)posX, (int)posY, (int)posX+imgW, (int)posY+imgH);
        canvas.drawBitmap(sheet, src, dst, paint);
    }

    public void draw(Canvas canvas){
        playerRect.set(posX, posY, posX+imgW, posY+imgH);
        if (invincible){
            ++blinkT;
            if (blinkT > 10){
                alphaSwitch = !alphaSwitch;
                paint.setAlpha((alphaSwitch)?125:255);
                blinkT = 0;
                ++blinks;
            }
        }
        walk(canvas, direction);
    }
}
