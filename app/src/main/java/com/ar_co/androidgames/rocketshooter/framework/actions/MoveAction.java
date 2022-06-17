package com.ar_co.androidgames.rocketshooter.framework.actions;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;

public class MoveAction extends Action {

    public Vector2 dest;

    private Vector2 vel;
    private float duration;

    private float time;
    private boolean fromPlace;

    public MoveAction(Sprite sprite, float x, float y, float time, boolean fromPlace){
        super(sprite, 2);
        //this.x = x;
        //this.y = y;
        dest = new Vector2(x, y);
        vel = new Vector2();
        this.time = time;
        this.fromPlace = fromPlace;
        initSteps();
    }

    @Override
    public void initSteps(){
        steps[0] = new Step(){
            @Override
            public void perform(float deltaTime){
                //work out direction/speed/etc
                duration = 0;
                if(fromPlace) {
                    vel.set(sprite.pos.x + dest.x, sprite.pos.y + dest.y);
                }else {
                    vel.set(dest.x, dest.y);
                }
                float angle = vel.sub(sprite.pos).angle();
                float radians = angle * Vector2.TO_RADIANS;

                float speed = vel.length();

                vel.x = (float)Math.cos(radians) * (speed/time);
                vel.y = (float)Math.sin(radians) * (speed/time);
                step++;
            }
        };

        steps[1] = new Step(){
            @Override
            public void perform(float deltaTime){
                duration += deltaTime;
                sprite.pos.add(vel.x * deltaTime, vel.y * deltaTime);
                if(duration >= time){
                    sprite.pos.set(dest.x, dest.y);
                    finished = true;
                    step++;
                }
            }
        };
    }

}
