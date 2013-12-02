package com.cschlisner.cc;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.PathShape;

/**
 * Created by cole on 11/29/13.
 */
public class ControlField {
    private int screenWidth, screenHeight;
    public Rect centerRect, topRect, rightRect, bottomRect, leftRect;
    public GameActivity.Direction direction = GameActivity.Direction.none;
    private Paint paint;
    private int topC, rightC, bottomC, leftC;
    private int selectedC = Color.argb(40, 0, 0, 0), defaultC = Color.argb(40, 150, 150, 150);
    public boolean drawField = false;
    
    public ControlField(int screenW, int screenH){
        screenWidth = screenW;
        screenHeight = screenH;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        topRect = new Rect();
        bottomRect = new Rect();
        rightRect = new Rect();
        leftRect = new Rect();
        centerRect = new Rect();

    }
    
    public void setDirection(int x, int y){
        if (4*topRect.bottom-y > leftRect.right-x && 4*topRect.bottom-y > x-rightRect.left && y <= topRect.bottom){
            direction = GameActivity.Direction.up;
        }
        else if (4*y-bottomRect.top > leftRect.right-x && 4*y-bottomRect.top > x-rightRect.left && y >= bottomRect.top){
            direction = GameActivity.Direction.down;
        }
        else if (x-rightRect.left > topRect.bottom-y && x-rightRect.left > y-bottomRect.top && x >= rightRect.left){
            direction = GameActivity.Direction.right;
        }
        else if (leftRect.right-x > topRect.bottom-y && leftRect.right-x > y-bottomRect.top && x <= leftRect.right){
            direction = GameActivity.Direction.left;
        }
        if (centerRect.contains(x,y)) direction = GameActivity.Direction.none;
    }
    
    public void positionField(int x, int y){
        int RectWidth = (screenWidth/12);
        int RectHeight = (screenHeight/11);
        topRect.set(x-(RectHeight+(RectWidth/2)), y-(RectHeight+(RectWidth/2)), x+(RectHeight+(RectWidth/2)), y-RectWidth/2);
        rightRect.set(x+RectWidth/2, y-(RectHeight+(RectWidth/2)), x+(RectHeight+(RectWidth/2)), y+(RectHeight+(RectWidth/2)));
        bottomRect.set(x-(RectHeight+(RectWidth/2)), y+RectWidth/2, x+(RectHeight+(RectWidth/2)), y+(RectHeight+(RectWidth/2)));
        leftRect.set(x-(RectHeight+(RectWidth/2)), y-(RectHeight+(RectWidth/2)), x-RectWidth/2, y+(RectHeight+(RectWidth/2)));
        centerRect.set(x-RectWidth/2, y-RectWidth/2, x+RectWidth/2, y+RectWidth/2);
    }

    public void draw(Canvas canvas){
        if (drawField){
            topC = defaultC;
            rightC = defaultC;
            bottomC = defaultC;
            leftC = defaultC;
            switch (direction){
                case up:
                    topC = selectedC;
                    break;
                case right:
                    rightC = selectedC;
                    break;
                case left:
                    leftC = selectedC;
                    break;
                case down:
                    bottomC = selectedC;
                    break;
                case none:
                    break;
            }
            paint.setColor(topC);
            canvas.drawRect(topRect, paint);
            paint.setColor(rightC);
            canvas.drawRect(rightRect, paint);
            paint.setColor(bottomC);
            canvas.drawRect(bottomRect, paint);
            paint.setColor(leftC);
            canvas.drawRect(leftRect, paint);
        }
    }
}
