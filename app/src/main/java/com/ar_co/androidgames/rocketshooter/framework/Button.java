package com.ar_co.androidgames.rocketshooter.framework;

import com.ar_co.androidgames.rocketshooter.interfaces.Input;

public class Button {

    /*public static abstract class Listener{
        public void onTouchUp(){

        }

        public void onTouchDown(){

        }

        public void onDrag(){

        }

        public void onDefault(){

        }
    }*/

    public float x;
    public float y;
    public float width;
    public float height;

    private boolean inside;
    private boolean isDown;

    //private GLGraphics glGraphics;
    //private Listener listener;

    public Button(float x, float y, float width, float height){
        //this.glGraphics = glGraphics;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void readTouchEvent(int touchEvent, float touchX, float touchY){
        if(touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height){
            if(touchEvent == Input.TouchEvent.TOUCH_DOWN && !isDown){
                inside = true;
                isDown = true;
                onTouchDown();
            }

            if(touchEvent == Input.TouchEvent.TOUCH_DRAGGED && isDown){
                onDrag();
            }

            if(touchEvent == Input.TouchEvent.TOUCH_UP && isDown){
                isDown = false;
                onClick();
            }
                //return true;
            }else{
                if(inside && isDown) {
                    isDown = false;
                    inside = false;
                    onInactive();
                }
            }

    }

    /*public void attachListener(Listener listener){
        this.listener = listener;
    }*/

    public void onClick(){

    }

    public void onTouchDown(){

    }

    /*public void onTouchUp(){

    }*/

    public void onDrag(){

    }

    public void onInactive(){

    }
}
