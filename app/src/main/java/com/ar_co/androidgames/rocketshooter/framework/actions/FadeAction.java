package com.ar_co.androidgames.rocketshooter.framework.actions;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

public class FadeAction extends Action {

    private float time;
    private float alpha;

    private float alphaOrigin;

    private float duration;
    private float speed;

    private boolean ew;

    public FadeAction(Sprite sprite, float alpha, float time, boolean ew){
        super(sprite, 2 + (ew ? 1 : 0));
        this.time = time;
        this.alpha = alpha;

        this.ew = ew;
        initSteps();
    }

    @Override
    protected void initSteps() {
        steps[0] = new Step(){

            @Override
            public void perform(float deltaTime) {
                duration = 0;
                alphaOrigin = sprite.alpha;
                speed = alpha - sprite.alpha;
                speed /= time;
                step++;
            }
        };

        steps[1] = new Step(){

            @Override
            public void perform(float deltaTime) {
                duration += deltaTime;
                sprite.alpha += speed * deltaTime;
                if(duration >= time){
                    duration = 0;
                    sprite.alpha = alpha;
                    if(!ew){
                        finished = true;
                    }
                    step++;
                }

            }
        };

        if(ew){
            steps[2] = new Step(){

                @Override
                public void perform(float deltaTime) {
                    duration += deltaTime;
                    sprite.alpha -= speed * deltaTime;

                    if(duration >= time){
                        sprite.alpha = alphaOrigin;
                        finished = true;
                        step++;
                    }
                }
            };
        }
    }
}
