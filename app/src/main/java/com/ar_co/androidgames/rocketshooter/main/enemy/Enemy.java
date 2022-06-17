package com.ar_co.androidgames.rocketshooter.main.enemy;

        import com.ar_co.androidgames.rocketshooter.framework.GLGame;
        import com.ar_co.androidgames.rocketshooter.framework.Sprite;
        import com.ar_co.androidgames.rocketshooter.framework.State;
        import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;
        import com.ar_co.androidgames.rocketshooter.main.sprites.Projectile;
        import com.ar_co.androidgames.rocketshooter.main.sprites.Ship;

public abstract class Enemy extends Sprite {

    public static final float HIT_TICK = 0.1f;

    public float maxHP;
    public float HP;
    protected HPBar hpBar;

    protected float speed;
    protected float collisionDamage;


    public boolean dead = false;

    protected float hitTime = 0;

    protected State state;
    protected State hit = new State(){
        public void update(float deltaTime){
            hitTime += deltaTime;

            if(hitTime >= HIT_TICK / 2.0f){
                alpha = 0.3f;
            }

            if(hitTime >= HIT_TICK){
                alpha = 1.0f;
                state = null;
            }
        }
    };

    public Enemy(GLGame game, Texture texture){
        super(game, texture);
    }

    public Enemy(GLGame game, Texture texture, float width, float height){
        super(game, texture, width, height);
    }

    public Enemy(GLGame game, Texture texture, float scale){
        super(game, texture, scale);
    }

    public abstract Enemy spawn(float x, float y);

    public abstract void update(float deltaTime);

    public abstract void onHit(Projectile projectile);

    public abstract void onCollision(Ship ship);

    public abstract int ID();

    @Override
    public void render(){
        super.render();
        hpBar.render();
    }
}
