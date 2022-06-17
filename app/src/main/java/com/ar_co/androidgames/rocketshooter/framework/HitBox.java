package com.ar_co.androidgames.rocketshooter.framework;

import android.util.Log;

import dataconstructor.DataStructure;
import dataconstructor.DataStructureReader;

public class HitBox implements DataStructureReader {

    public int count;

    Vector2[] vertices;
    Vector2[] axis;

    Vector2 p1 = new Vector2();
    Vector2 p2 = new Vector2();

    Vector2 point = new Vector2();

    Vector2 pos;

    float width;
    float height;

    public HitBox(){

    }

    public HitBox(Vector2 pos, float width, float height, Vector2... vertices) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.vertices = vertices;

        axis = new Vector2[vertices.length];

        for(int i = 0; i < axis.length; i++){
            axis[i] = new Vector2();
        }
    }

    /*public HitBox(Vector2... vertices){

        this.vertices = vertices;
        axis = new Vector2[vertices.length];
    }*/

    public HitBox(float width, float height){

    }

    public Vector2[] getAxis(Sprite s){


        float wScale = s.width / s.texture.width;
        float hScale = s.height / s.texture.height;

        for(int i = 0; i < vertices.length; i++){
            Vector2 a = vertices[i];
            Vector2 b = vertices[(i + 1) % vertices.length];


            p1.x = (a.x * wScale + s.pos.x) - s.rX;
            p1.y = (a.y * hScale + s.pos.y) - s.rY;

            p2.x = (b.x * wScale + s.pos.x) - s.rX;
            p2.y = (b.y * hScale + s.pos.y) - s.rY;

            p1.x = s.rotationMatrix[0] * p1.x + s.rotationMatrix[1] * p1.y + s.rX;
            p1.y = s.rotationMatrix[2] * p1.x + s.rotationMatrix[3] * p1.y + s.rY;

            p2.x = s.rotationMatrix[0] * p2.x + s.rotationMatrix[1] * p2.y + s.rX;
            p2.y = s.rotationMatrix[2] * p2.x + s.rotationMatrix[3] * p2.y + s.rY;

            //Log.i("HitBox", "p1: x = " + p1.x + ", y = " + p1.y + ", p2: x = " + p2.x + ", y = " + p2.y);

            axis[i].set(p1).sub(p2.x, p2.y);

            float x = axis[i].x;

            axis[i].x = -axis[i].y;
            axis[i].y = x;

        }

        return axis;
    }

    public void project(Sprite s, Vector2 axis, Projection proj){

        float wScale = s.width / s.texture.width;
        float hScale = s.height / s.texture.height;
        point.set(s.pos.x + vertices[0].x * wScale, s.pos.y + vertices[0].y * hScale);
        proj.min = axis.dot(point);
        proj.max = proj.min;
        for(int i = 0; i < vertices.length; i++) {
            point.set(s.pos.x + vertices[i].x * wScale, s.pos.y + vertices[i].y * hScale);
            float p = axis.dot(point);
            if (p < proj.min) {
                proj.min = p;
            } else if (p > proj.max) {
                proj.max = p;
            }

        }

    }

    public int size(){
        return vertices.length;
    }

    @Override
    public void readDataStructure(DataStructure dataStructure) {
        DataStructure verticesData = (DataStructure)dataStructure.values[0];
        vertices = new Vector2[verticesData.size()];
        for(int i = 0; i < vertices.length; i++){
            vertices[i] = new Vector2();
            vertices[i].readDataStructure((DataStructure)verticesData.values[i]);
        }

        pos = new Vector2();
        pos.readDataStructure((DataStructure)dataStructure.values[1]);

        width = (float)dataStructure.values[2];
        height = (float)dataStructure.values[3];

        axis = new Vector2[vertices.length];

        for(int i = 0; i < axis.length; i++){
            axis[i] = new Vector2();
        }
    }
}
