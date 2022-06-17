package com.ar_co.androidgames.rocketshooter.main;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.ar_co.androidgames.rocketshooter.framework.AndroidAudio;
import com.ar_co.androidgames.rocketshooter.framework.AndroidFileIO;
import com.ar_co.androidgames.rocketshooter.framework.AndroidInputHandler;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.GLGraphics;
import com.ar_co.androidgames.rocketshooter.framework.Screen;
//import com.ar_co.androidgames.rocketshooter.game.screens.TestScreen;
import com.ar_co.androidgames.rocketshooter.interfaces.Audio;
import com.ar_co.androidgames.rocketshooter.interfaces.FileIO;
import com.ar_co.androidgames.rocketshooter.interfaces.Graphics;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.core.Inventory;
import com.ar_co.androidgames.rocketshooter.main.gameplay.SimpleMission;
import com.ar_co.androidgames.rocketshooter.main.screens.BattleScreen;
import com.ar_co.androidgames.rocketshooter.main.screens.GarageScreen;

public class SpaceFrenzy extends GLGame {

    private GLGraphics mGraphics;
    private Audio mAudio;
    private Input mInput;
    private FileIO mFileIO;
    private Screen mCurrentScreen;
    private SensorManager sensorManager;

    private Inventory inventory;

    /*@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mGraphics = new GLGraphics(glView);
        mAudio = new AndroidAudio(this);
        mInput = new AndroidInputHandler(glView, 1, 1);
        mFileIO = new AndroidFileIO(this);
        mCurrentScreen = getStartScreen();
    }*/

    @Override
    public void setupGame(){
        super.setupGame();
        mGraphics = new GLGraphics(glView);
        mAudio = new AndroidAudio(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        PackageManager packageManager = getPackageManager();
        mInput = new AndroidInputHandler(glView, 1, 1, packageManager, sensorManager);
        mFileIO = new AndroidFileIO(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        sensorManager.registerListener((AndroidInputHandler)mInput, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop(){
        sensorManager.unregisterListener((AndroidInputHandler)mInput);
        super.onStop();
    }

    @Override
    public Graphics getGraphics(){
        throw new IllegalStateException("Use GL");
    }

    @Override
    public GLGraphics getGLGraphics(){
        return mGraphics;
    }

    @Override
    public Audio getAudio(){
        return mAudio;
    }

    @Override
    public Input getInput(){
        return mInput;
    }

    @Override
    public FileIO getFileIO(){
        return mFileIO;
    }

    @Override
    public void createScreen(Screen screen){
        if(screen == null){
            throw new IllegalArgumentException("Null pointer exception");
        }
        mCurrentScreen.pause();
        mCurrentScreen.dispose();
        screen.onStart();
        screen.resume();
        screen.update(0);
        mCurrentScreen = screen;
    }

    @Override
    public void setScreen(Screen screen){
        if(screen == null){
            throw new IllegalArgumentException("Null pointer exception");
        }
        mCurrentScreen.pause();
        mCurrentScreen.dispose();
        screen.resume();
        screen.update(0);
        mCurrentScreen = screen;
    }

    @Override
    public void transitionScreen(Screen prevScreen, Screen nextScreen, float[] rgb){
        /*if(prevScreen == null || nextScreen == null){
            throw new IllegalArgumentException("Null pointer exception");
        }
        Screen screen = new Transition(this, prevScreen, nextScreen, rgb);
        screen.onStart();
        screen.resume();
        screen.roamUpdate(0);
        mCurrentScreen = screen;*/
    }

    @Override
    public Screen getCurrentScreen(){
        return mCurrentScreen;
    }

    @Override
    public Screen getStartScreen(){
        mCurrentScreen = new BattleScreen(this, new SimpleMission(this));
        mCurrentScreen.onStart();
        return mCurrentScreen;
    }

    @Override
    public void onBackPressed(){
        if(getCurrentScreen() == null || !getCurrentScreen().onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void loadGame(){
        inventory = Inventory.getInstance(this);
    }

    @Override
    public void unloadGame(){
        inventory.cleanUp();
    }
}
