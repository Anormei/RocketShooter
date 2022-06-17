package com.ar_co.androidgames.rocketshooter.framework;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.interfaces.FileIO;

import java.io.IOException;
import java.io.Serializable;

public class Settings implements Serializable {
    /*private static final int VERSION = 2;
    private static final int AD_BOUND = 10;
    private float score;
    private float time;
    private int platforms;
    private int obstacles;
    private int tries = 0;
    private int ad;
    private static final int ATTEMPTS = 10;

    private static Settings settings;

    private Settings(){
        score = 0;
        ad = 0;
    }

    public static Settings getSettings(FileIO fileIO){
        if(settings == null){
            try{
                settings = (Settings) fileIO.readObject("settings");
            }catch(IOException e){
                settings = new Settings();
            }

            if(settings == null){
                settings = new Settings();
            }
        }
        return settings;
    }

    public static void showLeaderboard(GLGame game, float score){
        getActivity(game).requestLeaderboard(score);
    }

    public static void rateGame(GLGame game){
        getActivity(game).requestReview();
    }

    public static void displayBanner(GLGame game, boolean visible){
        Log.i("Settings", "displayBanner() called");
        getActivity(game).displayBanner(visible);
    }

    public static void displayInterstitial(GLGame game){
        getActivity(game).displayInterstitial();
    }

    public static GameActivity getActivity(GLGame game){
        return game;
    }

    public void save(FileIO fileIO){
        try{
            fileIO.writeObject(settings, "settings");
        }catch (IOException e) {
            tries++;
            if(tries < ATTEMPTS) {
                save(fileIO);
            }else{
                throw new RuntimeException("Failed to save");
            }
        }
    }

    public void showAd(GLGame game){
        Log.i("Settings", "showAd() called");
        if(ad < 10){
            Settings.displayBanner(game, true);
        }else{
            Settings.displayInterstitial(game);
        }

        ad = (ad + 1) % (AD_BOUND + 1);
    }

    public static void hideAd(GLGame game){
        displayBanner(game, false);
    }

    public void pushHighScore(float score){
        if(this.score < score) {
            this.score = score;
        }
    }

    public void setAdCount(int ad){
        this.ad = ad % AD_BOUND;
    }

    public float getHighScore(){
        return score;
    }

    public int getAdCount(){
        return ad;
    }*/

}
