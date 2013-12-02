package com.cschlisner.cc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**
 * Created by cole on 11/30/13.
 */
public class FireBall{
    private Bitmap image;
    private Paint paint = new Paint();
    private int velocityX, velocityY, maxSpeed, screenWidth, screenHeight,
                imageWidth, imageHeight, offset;
    private float posX, posY;
    private Rect bounds = new Rect();

    public FireBall(Context context, int speed, int sw, int sh, int sBarOffset){
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.fireball);
        maxSpeed = speed;
        screenWidth = sw;
        screenHeight = sh;
        offset = sBarOffset;
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        generate();
    }

    public void generate(){
        int screenEdge;
        Random rand = new Random(System.currentTimeMillis());
        int randS = (rand.nextInt(2*maxSpeed))-maxSpeed; //random number between -maxSpeed and maxSpeed
        int remainderP = maxSpeed - Math.abs(randS); //positive remainder of speed to be allocated
        int remainderN = -(maxSpeed-Math.abs(randS));//negative remainder of speed to be allocated
        screenEdge = rand.nextInt(4);
        if (screenEdge == 0){
            velocityX = randS;
            velocityY = remainderP;
            posX = rand.nextInt(screenWidth-imageWidth);
            posY = offset;
        }
        else if (screenEdge == 1){
            velocityY = randS;
            velocityX = remainderN;
            posX = screenWidth - imageWidth;
            posY = rand.nextInt(screenHeight-imageHeight);
        }
        else if (screenEdge == 2){
            velocityX = randS;
            velocityY = remainderN;
            posX = rand.nextInt(screenWidth-imageWidth);
            posY = screenHeight-imageHeight;
        }
        else if (screenEdge == 3){
            velocityY = randS;
            velocityX = remainderP;
            posX = 0;
            posY = rand.nextInt(screenHeight-imageHeight);
        }
    }

    public void update(Rect playerRect){
        if (bounds.intersect(playerRect)){Collisions.fireCollision = true;}
        if (posX < screenWidth && posX >= 0){ posX += velocityX;}
        else generate();
        if (posY < screenHeight && posY >= offset){posY += velocityY;}
        else generate();
    }

    public void draw(Canvas canvas){
        bounds.set((int)posX, (int)posY, (int)posX+imageWidth, (int)posY+imageHeight);
        canvas.drawBitmap(image, posX, posY, paint);
    }
}
