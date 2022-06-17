package com.ar_co.androidgames.rocketshooter.main.gameplay;

import com.ar_co.androidgames.rocketshooter.interfaces.Input;

import java.util.List;

public interface Controller {

    void handleControls(float dt, List<Input.TouchEvent> touchEvents);
}
