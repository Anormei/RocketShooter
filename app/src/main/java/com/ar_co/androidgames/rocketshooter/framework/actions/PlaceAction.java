package com.ar_co.androidgames.rocketshooter.framework.actions;

import com.ar_co.androidgames.rocketshooter.framework.Sprite;

public class PlaceAction extends Action{

    private float x;
    private float y;
    private float angle;


    public PlaceAction(Sprite sprite, float x, float y, float angle){
        super(sprite, 1);
        this.x = x;
        this.y = y;
        this.angle = angle;
        initSteps();
    }

    @Override
    protected void initSteps() {
        steps[0] = new Step(){

            @Override
            public void perform(float deltaTime) {
                sprite.pos.x = x;
                sprite.pos.y = y;
                if(angle > 0){
                    sprite.rotate(angle);
                }
                finished = true;
                step++;
            }
        };
    }
}
