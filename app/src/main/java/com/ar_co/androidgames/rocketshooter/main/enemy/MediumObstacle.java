package com.ar_co.androidgames.rocketshooter.main.enemy;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.main.sprites.Projectile;
import com.ar_co.androidgames.rocketshooter.main.sprites.Ship;

public class MediumObstacle extends Enemy{

    public static final int ID = 1;

    public MediumObstacle(GLGame game){
        super(game, Assets.getInstance(game).getImage("graphics/gameplay/medium-obstacle"), 4.0f);
        //body = HitBoxes.bodies[1];
        body = Assets.getInstance(game).getHitBox("hitboxes/gameplay/medium-obstacle");
        hpBar = new HPBar(game, this);

    }

    @Override
    public Enemy spawn(float x, float y) {
        HP = 60.0f;
        maxHP = 60.0f;
        speed = 100.0f;
        collisionDamage = 9.0f;
        hpBar.reset(HP);

        dead = false;
        alpha = 1.0f;

        pos.set(x, y);
        vel.set(0,speed);
        return this;
    }

    @Override
    public void update(float deltaTime){
        hpBar.update(deltaTime);
        if(state != null){
            state.update(deltaTime);
        }

        pos.add(vel.x * deltaTime, vel.y * deltaTime);
    }

    @Override
    public void onHit(Projectile projectile) {
        HP -= projectile.damage;

        if(HP <= 0){
            dead = true;
            state = null;
        }

        if(state != null || HP <= 0){
            return;
        }

        hitTime = 0;
        state = hit;
    }

    @Override
    public void onCollision(Ship ship){
        ship.onDamageReceived(collisionDamage);
    }

    @Override
    public int ID() {
        return MediumObstacle.ID;
    }
}
