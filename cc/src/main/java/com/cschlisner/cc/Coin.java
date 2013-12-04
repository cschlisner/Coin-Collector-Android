package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import java.util.Random;

/**
 * Created by cole on 11/30/13.
 */
public class Coin {
    private Bitmap[] images = new Bitmap[8];
    private Paint paint = new Paint();
    private int posX, posY, animate, switchImage;
    public boolean collected;
    private Rect bounds = new Rect();
    public Coin(Context context, int sw, int sh, int sBarOffset){
        images[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c1);
        images[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c2);
        images[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c3);
        images[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c4);
        images[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c5);
        images[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c6);
        images[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c7);
        images[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.c8);
        generate(sw, sh, sBarOffset);
    }

    private void generate(int scrW, int scrH, int offset){
        Random rand = new Random();
        posX = rand.nextInt(scrW-images[4].getWidth());
        int randY = rand.nextInt(scrH-images[4].getHeight());
        if (randY < offset) posY = offset+randY;
        else posY = randY;
        bounds.set(posX, posY, posX + images[4].getWidth(), posY + images[4].getHeight());
    }

    public void update(Rect playerRect){
        if (!collected){
            if (bounds.intersect(playerRect)){
                Collisions.coinCollision = true;
                collected = true;
                bounds.set(0,0,0,0);
            }
        }
    }
      
    public void draw(Canvas canvas){
        if (!collected){
            ++animate;
            if (animate >= 4){
                if (switchImage < 7) ++switchImage;
                else switchImage = 0;
                animate = 0;
            }
            canvas.drawBitmap(images[switchImage], posX, posY-(18 - images[switchImage].getHeight()), paint);
        }
    }
}
