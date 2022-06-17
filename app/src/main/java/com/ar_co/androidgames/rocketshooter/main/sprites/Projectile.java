package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

public abstract class Projectile extends Sprite{

    protected Gun gunParent;
    protected float speed;
    protected float baseDamage;
    public float damage;

    public boolean friendly;

    public boolean delete;

    public Projectile(GLGame game, Texture texture) {
        super(game, texture);
    }

    public Projectile(GLGame game, Texture texture, float width, float height) {
        super(game, texture, width, height);
    }

    public void remove(){
        gunParent.pool.free(this);
    }
}
