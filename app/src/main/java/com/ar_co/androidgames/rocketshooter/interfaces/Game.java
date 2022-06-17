package com.ar_co.androidgames.rocketshooter.interfaces;

import com.ar_co.androidgames.rocketshooter.framework.Screen;

public interface Game {

    Input getInput();

    Graphics getGraphics();

    Audio getAudio();

    FileIO getFileIO();

    void createScreen(Screen screen);

    void setScreen(Screen screen);

    void transitionScreen(Screen prevScreen, Screen nextScreen, float[] rgb);

    Screen getCurrentScreen();

    Screen getStartScreen();
}
