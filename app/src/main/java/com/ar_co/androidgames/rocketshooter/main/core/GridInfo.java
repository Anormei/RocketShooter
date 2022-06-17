package com.ar_co.androidgames.rocketshooter.main.core;

import android.graphics.Point;
import android.graphics.PointF;

import dataconstructor.DataStructure;
import dataconstructor.DataStructureReader;

public class GridInfo implements DataStructureReader {

    private static final float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
    public int x;
    public int y;
    public int rotation;
    public PointF rotationPoint;

    public int width;
    public int height;

    public boolean flipped;

    //public Point[] spaceOccupancy;
    //public Point calculatedPoint;

    public GridInfo(){
        x = 0;
        y = 0;
        //calculatedPoint = new Point();
        rotationPoint = new PointF();
    }

    public GridInfo(int x, int y){
        this.x = x;
        this.y = y;
    }

    public GridInfo(GridInfo v){
        this.x = v.x;
        this.y = v.y;
    }

    public GridInfo copy(){
        return new GridInfo(x, y);
    }

    public GridInfo set(int x, int y){
        this.x = x;
        this.y = y;
        return this;
    }

    public GridInfo set(GridInfo v){
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public GridInfo add(int x, int y){
        this.x += x;
        this.y += y;
        return this;
    }

    public GridInfo add(GridInfo v){
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public GridInfo sub(int x, int y){
        this.x -= x;
        this.y -= y;
        return this;
    }

    public GridInfo sub(GridInfo v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public GridInfo mul(int scalar){
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    public GridInfo div(int scalar){
        if(scalar != 0){
            this.x /= scalar;
            this.y /= scalar;
        }
        return this;
    }

    public void rotate(){
        rotation++;
        rotation %= 4;
    }

    /*public Point getPoint(int index){
        Point normalPoint = spaceOccupancy[index];

        float midX = ((float)width) / 2.0f;
        float midY = ((float)height) / 2.0f;

        float pointX = normalPoint.x - midX;
        float pointY = normalPoint.y - midY;

        float rad = (rotation * 90.0f) * TO_RADIANS;

        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float newX = pointX * cos - pointY * sin;
        float newY = pointX* sin + pointY * cos;

        pointX = newX + midX;
        pointY = newY + midY;

        calculatedPoint.set(Math.round(pointX), Math.round(pointY));

        return calculatedPoint;
    }*/

    @Override
    public void readDataStructure(DataStructure dataStructure) {
        this.width = (int)dataStructure.values[0];
        this.height = (int)dataStructure.values[1];

        //DataStructure dataPoint0 = (DataStructure)dataStructure.values[2];
        //this.anchorPoint = new Point((int)dataPoint0.values[0], (int)dataPoint0.values[1]);

        /*DataStructure pointArr = (DataStructure)dataStructure.values[3];

        spaceOccupancy = new Point[pointArr.size()];
        for(int i = 0; i < spaceOccupancy.length; i++){
            DataStructure dataPoint1 = (DataStructure)pointArr.values[i];
            spaceOccupancy[i] = new Point((int)dataPoint1.values[0], (int)dataPoint1.values[1]);
        }*/

    }
}
