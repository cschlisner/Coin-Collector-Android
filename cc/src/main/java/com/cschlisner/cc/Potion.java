package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by cole on 12/1/13.
 */
public class Potion {
    private Bitmap image;
    public boolean collected;
    private int posX, posY;
    int timer;
    private Rect bounds = new Rect();
    int scrW, scrH, offset;
    private Paint paint = new Paint();
    public Potion(Context context, int sw, int sh, int sBarOffset){
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.powerup);
        scrW = sw;
        scrH = sh;
        offset = sBarOffset;
        generate();
    }

    private void generate(){
        Random rand = new Random();
        posX = rand.nextInt(scrW-image.getWidth());
        int randY = rand.nextInt(scrH-image.getHeight());
        if (randY < offset) posY = offset+randY;
        else posY = randY;
        bounds.set(posX, posY, posX+image.getWidth(), posY+image.getHeight());
    }

    public void update(Rect playerRect){
        if (!collected){
            bounds.set(posX, posY, posX+image.getWidth(), posY+image.getHeight());
            if (bounds.intersect(playerRect)){
                collected = true;
                Collisions.potionCollision = true;
                bounds.set(0,0,0,0);
            }
            ++timer;
            if (timer >= 100){
                timer = 0;
                generate();
            }
        }
    }

    public void draw(Canvas canvas){
        if (!collected)
            canvas.drawBitmap(image, posX, posY, paint);
    }
}
