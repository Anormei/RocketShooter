package com.ar_co.androidgames.rocketshooter.interfaces;

import com.ar_co.androidgames.rocketshooter.framework.Vector2;

import java.util.List;

public interface Input {
    class TouchEvent {
        public static final int TOUCH_DOWN = 0;
        public static final int TOUCH_UP = 1;
        public static final int TOUCH_DRAGGED = 2;

        public int type;
        public int x, y;
        public int pointerId;
    }

    class SensorEvent{
        public int x, y, z;
    }

    boolean isTouchDown(int pointerId);
    
    int getTouchX(int pointerId);

    int getTouchY(int pointerId);

    List<TouchEvent> getTouchEvents();

    Vector2 getAccel();
}
