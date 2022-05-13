package com.example.clashroyale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Obstacle {
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void down(){
        y -= 10;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    int x,y;
    Bitmap img;

    public Obstacle(Context context, int x, int drawable){
        this.x = x;
        this.y = 600;
        img = BitmapFactory.decodeResource(context.getResources(), drawable);
    }

    public Bitmap getImg() {
        return img;
    }

    public boolean checkHit(int x, int y){
        if((Math.abs(x - this.x) <= (170)) && Math.abs(y-this.y) <= (170))
            return true;
        return false;
    }
}
