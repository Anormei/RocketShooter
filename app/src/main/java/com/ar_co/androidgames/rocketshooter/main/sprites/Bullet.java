package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.HitBoxes;

public class Bullet extends Projectile {

    public Bullet(GLGame game, Gun gunParent) {
        super(game, Assets.getInstance(game).getImage("graphics/gameplay/bullet"), 4.0f, 20.0f);
        this.gunParent = gunParent;
        this.speed = 2000.0f;
        this.baseDamage = 3.0f;
       // body = HitBoxes.bodies[2];
        body = Assets.getInstance(game).getHitBox("hitboxes/gameplay/bullet");
    }

}
