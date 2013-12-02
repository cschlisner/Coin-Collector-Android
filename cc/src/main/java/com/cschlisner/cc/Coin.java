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
    private Bitmap image;
    private Paint paint = new Paint();
    private int posX, posY;
    public boolean collected;
    private Rect bounds = new Rect();
    public Coin(Context context, int sw, int sh, int sBarOffset){
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
        generate(sw, sh, sBarOffset);
    }

    private void generate(int scrW, int scrH, int offset){
        Random rand = new Random();
        posX = rand.nextInt(scrW-image.getWidth());
        int randY = rand.nextInt(scrH-image.getHeight());
        if (randY < offset) posY = offset+randY;
        else posY = randY;
        bounds.set(posX, posY, posX+image.getWidth(), posY+image.getHeight());
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
        if (!collected)
            canvas.drawBitmap(image, posX, posY, paint);
    }
}
