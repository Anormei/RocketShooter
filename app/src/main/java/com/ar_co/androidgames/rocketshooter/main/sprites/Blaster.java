package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

import java.util.Iterator;

public class Blaster extends Gun<Bullet> {

    public Blaster(final GLGame game, Texture texture, Camera2D camera2D) {
        super(game, texture, 12.0f, 16.0f, camera2D); //theoretical size
        direction.set(0, -1.0f);
        barrelAxis.set(width / 2.0f, 0);
        fireRate = 0.18f; //0.18f
    }

    /*public Blaster(final GLGame game, Texture texture, float width, float height, Camera2D camera2D) {
        super(game, texture, width, height, camera2D);
        direction.set(0, -1.0f);
        fireRate = 0.18f;
    }*/
    @Override
    public Bullet createProjectile() {
        return new Bullet(game, this);
    }

    @Override
    public void shoot(float dt, float power, boolean friendly, boolean enabled) {
        if(cooldown >= fireRate && enabled) {
            cooldown = 0;
            Bullet bullet = pool.newObject();
            bullet.vel.set(direction.x * bullet.speed, direction.y * bullet.speed);
            //Log.i("Blaster", "Bullet speed = " + bullet.vel.length());
            bullet.pos.set(pos.x + (barrelAxis.x - (bullet.width / 2.0f)), pos.y + (barrelAxis.y - bullet.height));
            bullet.damage = bullet.baseDamage * power;
            bullet.friendly = friendly;
            bullet.delete = false;
            projectiles.add(bullet);
            //return bullet;
        }

        for(Iterator<Projectile> iterator = projectiles.iterator(); iterator.hasNext();){
            Projectile p = iterator.next();
            p.pos.add(p.vel.x * dt, p.vel.y * dt);
            if(p.pos.y < 0 || p.delete){
                p.remove();
                iterator.remove();
            }
        }
        cooldown += dt;



        //return null;
    }
}
