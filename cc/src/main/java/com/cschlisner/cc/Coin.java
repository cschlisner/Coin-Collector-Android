package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by cole on 11/30/13.
 */
public class Coin {
    private Bitmap sheet;
    private Paint paint = new Paint();
    private int posX, posY, animate, switchImage, imgW, imgH;
    public boolean collected, drawMe;
    private RectF bounds = new RectF();
    public Coin(Context context, int sw, int sh){
        sheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.coinsheet);
        imgW = sheet.getWidth()/8;
        imgH = sheet.getHeight();
        generate(sw, sh);
    }

    private void generate(int scrW, int scrH){
        Random rand = new Random();
        posX = rand.nextInt(scrW-imgW);
        posY = rand.nextInt(scrH-imgH);
        bounds.set(posX, posY, posX + imgW, posY + imgH);
        switchImage = rand.nextInt(6);
    }

    public void update(RectF playerRect, RectF view){
        drawMe = true;
        if (!collected && drawMe){
            if (bounds.intersect(playerRect)){
                ++Globals.coinCollisions;
                collected = true;
                bounds.set(0,0,0,0);
            }
        }
    }
      
    public void draw(Canvas canvas){
        if (!collected && drawMe){
            ++animate;
            if (animate == 5){
                if (switchImage < 7) ++switchImage;
                else switchImage = 0;
                animate = 0;
            }
            Rect src = new Rect(imgW*switchImage, 0, (imgW*switchImage)+imgW, imgH);
            Rect dst = new Rect(posX, posY, posX+imgW, posY+imgH);
            canvas.drawBitmap(sheet, src, dst, paint);
        }
    }
}
