package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

import java.util.ArrayList;
import java.util.List;

public abstract class Gun<T extends Projectile> extends Sprite{

    protected Pool<T> pool;
    public Vector2 barrelAxis;
    //public Vector2 barrelAxis;
    protected Vector2 direction;

    public Vector2 shipAxisPos;

    protected float fireRate;
    protected float cooldown = 0;

    protected List<Projectile> projectiles = new ArrayList<>();
    protected Camera2D camera2D;

    public Gun(final GLGame game, Texture texture, Camera2D camera2D) {
        super(game, texture);
        pool = new Pool<>(new Pool.PoolObjectFactory<T>() {
            @Override
            public T createObject() {
                return createProjectile();
            }
        }, 100);
        barrelAxis = new Vector2();
        direction = new Vector2();
        shipAxisPos = new Vector2();
        //barrelAxis = new Vector2();
        this.camera2D = camera2D;
    }

    public Gun(final GLGame game, Texture texture, float width, float height, Camera2D camera2D) {
        super(game, texture, width, height);
        pool = new Pool<>(new Pool.PoolObjectFactory<T>() {
            @Override
            public T createObject() {
                return createProjectile();
            }
        }, 100);
        barrelAxis = new Vector2();
        direction = new Vector2();
        shipAxisPos = new Vector2();
        //barrelAxis = new Vector2();
        this.camera2D = camera2D;
    }

    public abstract T createProjectile();


    public void shoot(float dt, float power, boolean friendly, boolean enabled){

    }

    public void rotateTrajectory(float angle){
        barrelAxis.sub(width / 2.0f, height / 2.0f);
        barrelAxis.rotate(angle);
        barrelAxis.add(width / 2.0f, height / 2.0f);
    }


    public void renderProjectiles(float dt){
        for(int i = 0; i < projectiles.size(); i++){
            Projectile projectile = projectiles.get(i);
            if(!projectile.delete){
                projectile.render();
            }
        }
    }

    @Override
    public void rotate(float angle){
        super.rotate(angle);
        barrelAxis.sub(pos);
        barrelAxis.rotate(angle);
        barrelAxis.add(pos);
    }

    @Override
    public void rotate(float x, float y, float angle){
        super.rotate(x, y, angle);
        barrelAxis.sub(x, y);
        barrelAxis.rotate(angle);
        barrelAxis.add(x, y);
    }


    /*public void update(float deltaTime){
        cooldown += deltaTime;
    }*/
}
