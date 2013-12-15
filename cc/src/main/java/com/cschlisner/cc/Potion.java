package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by cole on 12/1/13.
 */
public class Potion {
    private Bitmap image1, image2;
    public boolean collected, switchImage;
    public int type;
    private int posX, posY, animate, velocityX, velocityY;
    private boolean drawMe;
    private RectF bounds = new RectF();
    int scrW, scrH;
    private Paint paint = new Paint();
    public Potion(Context context, int sw, int sh){
        type = (new Random().nextBoolean())?1:2;
        if (type == 1) {
            image1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.pot1);
            image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.pot2);
        }
        else {
            image1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gpot1);
            image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.gpot2);
        }
        scrW = sw;
        scrH = sh;
        generate();
    }

    private void generate(){ 
        int screenEdge, maxSpeed = new Random().nextInt(5)+5, imageWidth = image1.getWidth(), imageHeight = image1.getWidth();
        Random rand = new Random(System.currentTimeMillis());
        int randS = (rand.nextInt(2*maxSpeed))-maxSpeed; //random number between -maxSpeed and maxSpeed
        int remainderP = maxSpeed - Math.abs(randS); //positive remainder of speed to be allocated
        int remainderN = -(maxSpeed-Math.abs(randS));//negative remainder of speed to be allocated
        screenEdge = rand.nextInt(4);
        if (screenEdge == 0){
            velocityX = randS;
            velocityY = remainderP;
            posX = rand.nextInt(scrW-imageWidth);
            posY = 0;
        }
        else if (screenEdge == 1){
            velocityY = randS;
            velocityX = remainderN;
            posX = scrW - imageWidth;
            posY = rand.nextInt(scrH-imageHeight);
        }
        else if (screenEdge == 2){
            velocityX = randS;
            velocityY = remainderN;
            posX = rand.nextInt(scrW-imageWidth);
            posY = scrH;
        }
        else if (screenEdge == 3){
            velocityY = randS;
            velocityX = remainderP;
            posX = 0;
            posY = rand.nextInt(scrH-imageHeight);
        }
    }

    public void update(RectF playerRect, RectF view){
        bounds.set(posX, posY, posX+image1.getWidth(), posY+image1.getHeight());
        drawMe = (bounds.intersect(view));
        if (!collected && drawMe){
            if (bounds.intersect(playerRect)){
                collected = true;
                Globals.potionCollision = true;
                bounds.set(0,0,0,0);
            }
        }
        if (posX+image1.getWidth() < scrW && posX >= 0)
            posX += velocityX;
        else{
            velocityX = -velocityX;
            posX += velocityX;
        }
        if (posY+image1.getHeight() < scrH && posY >= 0)
            posY += velocityY;
        else {
            velocityY = -velocityY;
            posY += velocityY;
        }
    }

    public void draw(Canvas canvas){
        if (!collected && drawMe){
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
