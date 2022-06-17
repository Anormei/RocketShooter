package com.ar_co.androidgames.rocketshooter.framework;

import android.opengl.GLES20;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Camera2D {

    public float zoom;
    public final float frustumWidth;
    public final float frustumHeight;
    private final GLGraphics glGraphics;

    public float x;
    public float y;

    //boundary


    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] vPMatrix = new float[16];

    public Camera2D(GLGraphics glGraphics, float frustumWidth, float frustumHeight) {
        this.glGraphics = glGraphics;
        this.frustumWidth = frustumWidth;
        this.frustumHeight = frustumHeight;
        x = frustumWidth / 2.0f;
        y = frustumHeight / 2.0f;
        this.zoom = 1.0f;
    }

    public float[] setViewportAndMatrices() {
        GLES20.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        //GLES20.glMatrixMode(GL10.GL_PROJECTION);
        //GLES20.glLoadIdentity();
        Matrix.frustumM(projectionMatrix, 0,
                -(x-frustumWidth * zoom / 2),
                -(x+frustumWidth * zoom / 2),
                y + frustumHeight * zoom / 2 ,
                y - frustumHeight * zoom / 2 ,
                3, 7);
       /* GLES20.glOrthof(
                x - frustumWidth * zoom / 2,
                x + frustumWidth * zoom / 2,
                y + frustumHeight * zoom / 2,
                y - frustumHeight * zoom / 2,
                1, -1
                );*/

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1.0f, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        //GLES20.glLoadIdentity();
        return vPMatrix;
    }

    public float[] setMatrices(){
        Matrix.frustumM(projectionMatrix, 0,
                -(x-frustumWidth * zoom / 2),
                -(x+frustumWidth * zoom / 2),
                y + frustumHeight * zoom / 2 ,
                y - frustumHeight * zoom / 2 ,
                3, 7);
       /* GLES20.glOrthof(
                x - frustumWidth * zoom / 2,
                x + frustumWidth * zoom / 2,
                y + frustumHeight * zoom / 2,
                y - frustumHeight * zoom / 2,
                1, -1
                );*/

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0, 0, 0, 0, 1.0f, 0);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        //GLES20.glLoadIdentity();
        return vPMatrix;
    }

    public void center(){
        x = frustumWidth / 2.0f;
        y = frustumHeight / 2.0f;
    }


    public float touchX(float xTouch){
        float result = (xTouch / (float)glGraphics.getWidth()) * frustumWidth * zoom;
        return (result + x) - (frustumWidth * zoom / 2);
    }

    public float touchY(float yTouch){

        float result = (yTouch / (float)glGraphics.getHeight()) * frustumHeight * zoom;
        return (result + y) - (frustumHeight * zoom / 2);
    }

    public float nativeTouchX(float xTouch){
        return (xTouch / (float)glGraphics.getWidth()) * frustumWidth;
    }

    public float nativeTouchY(float yTouch){
        return (yTouch / (float)glGraphics.getHeight()) * frustumHeight;
    }

    public float getLeftFrustum(){
        return x - (frustumWidth * zoom / 2);
    }

    public float getRightFrustum(){
        return x + (frustumWidth * zoom / 2);
    }

    public float getTopFrustum(){
        return y - (frustumHeight * zoom / 2);
    }

    public float getBottomFrustum(){
        return y + (frustumHeight * zoom / 2);
    }

    public void reset(){
        x = frustumWidth / 2;
        y = frustumHeight / 2;
        zoom = 1.0f;
    }
}

