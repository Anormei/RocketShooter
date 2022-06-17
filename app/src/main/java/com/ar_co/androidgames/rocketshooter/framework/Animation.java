package com.ar_co.androidgames.rocketshooter.framework;

public class Animation {

    private Vector2 velocity;

    public Animation(){

    }

    public void move(Sprite sprite, float x, float y, float time){
        velocity = new Vector2(sprite.pos);

        velocity.sub(x, y);

    }
}
