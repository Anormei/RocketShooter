package com.ar_co.androidgames.rocketshooter.interfaces;

import android.view.View;

import java.util.List;

public interface TouchHandler extends View.OnTouchListener {
    boolean isTouchDown(int pointerId);

    int getTouchX(int pointerId);

    int getTouchY(int pointerId);

    List<Input.TouchEvent> getTouchEvents();
}
