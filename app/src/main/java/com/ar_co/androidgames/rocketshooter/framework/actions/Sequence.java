package com.ar_co.androidgames.rocketshooter.framework.actions;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Sequence {

    private static class Timestamp{
        public float time;
        public Action action;

        public Timestamp(){

        }

        public Timestamp(float time, Action action){
            this.time = time;
            this.action = action;
        }
    }

    //public float timeline;
    public float time = 0;

    private List<Timestamp> timestamps = new ArrayList<>();
    private List<Action> activeActions = new ArrayList<>();

    private int curr = 0;
    private int completed = 0;


    public Sequence(){
        init();
    }

    public Sequence(Timestamp... actions){
        init();
        for(Timestamp a : actions){
            timestamps.add(a);
        }

    }

    public void init(){

    }

    public void update(float deltaTime){
        time += deltaTime;

        for(;curr < timestamps.size() && time >= timestamps.get(curr).time; curr++){
            activeActions.add(timestamps.get(curr).action);
        }

        for(Iterator<Action> iterator = activeActions.iterator(); iterator.hasNext();){
            Action action = iterator.next();
            action.steps[action.step].perform(deltaTime);
            if(action.finished){
                completed++;
                iterator.remove();
            }
        }
    }

    public void rewind(){
        completed = 0;
        curr = 0;
        time = 0;
        for(int i = 0; i < timestamps.size(); i++){
            Timestamp t = timestamps.get(i);
            t.action.rewind();
        }
    }

    public void skip(){
        for(int i = 0; i < timestamps.size(); i++){
            Action a = timestamps.get(i).action;
            while(!a.finished) {
                a.steps[a.step].perform(100000.0f);
            }
        }
        completed = timestamps.size();
    }

    public boolean isFinished(){
        return completed >= timestamps.size();
    }

    public void addAction(Action action, float time){
        timestamps.add(new Timestamp(time, action));
    }

    public void moveTo(Sprite sprite, float x, float y, float duration, float time){
        timestamps.add(new Timestamp(time, new MoveAction(sprite, x, y, duration, false)));
    }

    public void moveFrom(Sprite sprite, float x, float y, float duration, float time){
        timestamps.add(new Timestamp(time, new MoveAction(sprite, x, y, duration, true)));
    }

    public void place(Sprite sprite, float x, float y, float angle, float time){
        timestamps.add(new Timestamp(time, new PlaceAction(sprite, x, y, angle)));
    }

    public void fade(Sprite sprite, float alpha, float duration, float time){
        timestamps.add(new Timestamp(time, new FadeAction(sprite, alpha, duration, false)));
    }

    public void fadeEW(Sprite sprite, float alpha, float duration, float time){
        timestamps.add(new Timestamp(time, new FadeAction(sprite, alpha, duration, true)));
    }

    public void blink(Sprite sprite, float blinkInterval, float appearInterval, int repeat, float time){
        timestamps.add(new Timestamp(time, new BlinkAction(sprite, blinkInterval, appearInterval, repeat)));
    }

    public void show(Sprite sprite, float time){
        timestamps.add(new Timestamp(time, new ShowAction(sprite)));
    }


    public void hide(Sprite sprite, float time){
        timestamps.add(new Timestamp(time, new HideAction(sprite)));
    }

    public void sortTimes(){

    }
}
