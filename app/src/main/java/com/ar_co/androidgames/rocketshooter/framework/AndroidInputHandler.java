package com.ar_co.androidgames.rocketshooter.framework;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ar_co.androidgames.rocketshooter.interfaces.Input;

import java.util.ArrayList;
import java.util.List;

public class AndroidInputHandler implements Input, View.OnTouchListener, SensorEventListener{

    private static final int MAX_TOUCH_POINTS = 10;
    private boolean[] mIsTouched = new boolean[MAX_TOUCH_POINTS];
    private int[] mTouchX = new int[MAX_TOUCH_POINTS];
    private int[] mTouchY = new int[MAX_TOUCH_POINTS];
    private int[] mId = new int[MAX_TOUCH_POINTS];

    private static final float ALPHA = 0.65f;
    private static final float SENSITIVITY = 1f/128f;

    private Pool<TouchEvent> mTouchEventPool;
    private List<TouchEvent> mTouchEvents = new ArrayList<>();
    private List<TouchEvent> mTouchEventsBuffer = new ArrayList<>();

    private float mScaleX;
    private float mScaleY;

    private Vector2 accel = new Vector2();
    private Vector2 init = new Vector2();
    private float maximumRange;

    private boolean firstTime = true;

    private SensorManager sensorManager;

    private boolean hasAccelerometer;
    private boolean hasGyroscope;

    public AndroidInputHandler(View v, float scaleX, float scaleY, PackageManager packageManager, SensorManager sensorManager){
        Pool.PoolObjectFactory factory = new Pool.PoolObjectFactory() {
            @Override
            public Object createObject() {
                return new Input.TouchEvent();
            }
        };

        mTouchEventPool = new Pool<>(factory, 100);
        v.setOnTouchListener(this);

        mScaleX = scaleX;
        mScaleY = scaleY;
        hasAccelerometer = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        hasGyroscope = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        this.maximumRange = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).getMaximumRange();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        synchronized(this){
            int action = event.getActionMasked();
            int pointerIndex = event.getActionIndex();
            int pointerCount = event.getPointerCount();

            Input.TouchEvent touchEvent;

            /*if(action == MotionEvent.ACTION_MOVE && pointerCount > 1){
                Log.i("AndroidInputHandler", "1. touch.x = " + event.getX(0) + "touch.y = " + event.getY(0)); // WORKS
                Log.i("AndroidInputHandler", "2. touch.x = " + event.getX(1) + "touch.y = " + event.getY(1)); // WORKS
            }*/

            for(int i = 0; i < MAX_TOUCH_POINTS; i++){
                if(i >= pointerCount){
                    mIsTouched[i] = false;
                    mId[i] = -1;
                    continue;
                }

                if(i != pointerIndex){
                    continue;
                }

                //if(MotionEvent.ACTION_BUTTON_PRESS == 12);

                switch(action){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        touchEvent = mTouchEventPool.newObject();
                        processTouch(touchEvent, event, Input.TouchEvent.TOUCH_DOWN);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Log.i("AndroidInputHandler", "pointerCount = " + pointerCount);
                        for(int j = 0; j < pointerCount; j++) {
                            processMultiTouch(event, j);
                        }
                        /*for(int j = 0; j < mTouchEventsBuffer.size(); j++) {
                            Log.i("AndroidInputHandler", "[" + j + "] pointer.id = " + mTouchEventsBuffer.get(j).pointerId + " touch.x = " + mTouchEventsBuffer.get(j).x + ", touch.y = " + mTouchEventsBuffer.get(j).y);
                        }*/
                        //Log.i("AndroidInputHandler", "touchEvents.size() = " + mTouchEvents.size());
                        //Log.i("AndroidInputHandler", "___________________________________________________");
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchEvent = mTouchEventPool.newObject();
                        processTouch(touchEvent, event, Input.TouchEvent.TOUCH_UP);
                        break;

                }
            }
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointerId){
        synchronized(this){
            int idIndex = getIdIndex(pointerId);
            if(checkIndex(idIndex))
                return false;
            else
                return mIsTouched[idIndex];
        }
    }

    @Override
    public int getTouchX(int pointerId){
        synchronized(this){
            int idIndex = getIdIndex(pointerId);
            if(checkIndex(idIndex))
                return 0;
            else
                return mTouchX[idIndex];
        }
    }

    @Override
    public int getTouchY(int pointerId){
        synchronized(this){
            int idIndex = getIdIndex(pointerId);
            if(checkIndex(idIndex))
                return 0;
            else
                return mTouchY[idIndex];
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents(){
        synchronized(this){
            for(int i = 0; i < mTouchEvents.size(); i++){
                mTouchEventPool.free(mTouchEvents.get(i));
            }
            mTouchEvents.clear();
            mTouchEvents.addAll(mTouchEventsBuffer);
            mTouchEventsBuffer.clear();

            return mTouchEvents;
        }
    }


    @Override
    public Vector2 getAccel(){
        synchronized(this){
            return accel;
        }
    }

    private void processTouch(Input.TouchEvent touchEvent, MotionEvent event, int type){
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        touchEvent.type = type;
        touchEvent.pointerId = pointerId;
        touchEvent.x = mTouchX[index] = (int) (event.getX(index) * mScaleX);
        touchEvent.y = mTouchY[index] = (int) (event.getY(index) * mScaleY);

        mIsTouched[index] = true;
        mId[index] = pointerId;
        mTouchEventsBuffer.add(touchEvent);
    }

    private void processMultiTouch(MotionEvent event, int index){
        TouchEvent touchEvent = mTouchEventPool.newObject();

        int pointerId = event.getPointerId(index);

        touchEvent.type = TouchEvent.TOUCH_DRAGGED;
        touchEvent.pointerId = index;
        touchEvent.x = mTouchX[index] = (int) (event.getX(index) * mScaleX);
        touchEvent.y = mTouchY[index] = (int) (event.getY(index) * mScaleY);

        mIsTouched[index] = true;
        mId[index] = index;
        mTouchEventsBuffer.add(touchEvent);
        //Log.i("AndroidInputHandler", String.format("touchEvent: pointerId = %d, x = %d, y = %d", touchEvent.pointerId, touchEvent.x, touchEvent.y));
    }

    private int getIdIndex(int pointerId){
        for(int i = 0; i < MAX_TOUCH_POINTS; i++){
            if(mId[i] == pointerId)
            return i;

        }
        return - 1;
    }

    private boolean checkIndex(int index) {
        if (index < 0 || index >= MAX_TOUCH_POINTS)
            return true;
        else
            return false;
    }


    @Override
    public void onSensorChanged(android.hardware.SensorEvent sensorEvent) {
        synchronized(this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                /*if(firstTime) {
                    init.set(sensorEvent.values[0], -sensorEvent.values[1]);
                    firstTime = false;
                }*/

                /*accel[0] = (accel[0] + ALPHA * (sensorEvent.values[0] - accel[0])) * SENSITIVITY;
                accel[1] = (accel[1] + ALPHA * (sensorEvent.values[1] - accel[1])) * SENSITIVITY;

                accel[0] = sensorEvent.values[0] - init[0];
                accel[1] = -sensorEvent.values[1] - init[1];

                init[0] = ALPHA * init[0] + (1-ALPHA) * sensorEvent.values[0];
                init[1] = ALPHA * init[1] + (1-ALPHA) * sensorEvent.values[1];*/

                accel.set(sensorEvent.values[0], -sensorEvent.values[1]);//.sub(init);
            }

            if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
