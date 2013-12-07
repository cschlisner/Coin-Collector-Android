package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by cole on 12/1/13.
 */
public class Potion {
    private Bitmap image1, image2;
    public boolean collected, switchImage;
    private int posX, posY, animate;
    int timer;
    private RectF bounds = new RectF();
    int scrW, scrH;
    private Paint paint = new Paint();
    public Potion(Context context, int sw, int sh){
        image1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.pot1);
        image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.pot2);
        scrW = sw;
        scrH = sh;
        generate();
    }

    private void generate(){
        Random rand = new Random();
        posX = rand.nextInt(scrW-image1.getWidth());
        posY = rand.nextInt(scrH-image1.getHeight());
        bounds.set(posX, posY, posX+image1.getWidth(), posY+image1.getHeight());
    }

    public void update(RectF playerRect){
        if (!collected){
            bounds.set(posX, posY, posX+image1.getWidth(), posY+image1.getHeight());
            if (bounds.intersect(playerRect)){
                collected = true;
                Globals.potionCollision = true;
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
        if (!collected){
            ++animate;
            if (animate >= 10){
                switchImage = !switchImage;
                animate = 0;
            }
            if (switchImage) canvas.drawBitmap(image1, posX, posY, paint);
            else canvas.drawBitmap(image2, posX, posY, paint);
        }
    }
}
