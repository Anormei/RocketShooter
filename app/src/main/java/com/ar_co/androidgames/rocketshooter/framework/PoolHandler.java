package com.ar_co.androidgames.rocketshooter.framework;

public class PoolHandler implements GLGame.DisposalUnit{

    private GLGame game;
    private Pool<Sprite> pool;

    public void setup(GLGame game, Pool.PoolObjectFactory factory, int size){
        this.game = game;
        pool = new Pool<>(factory, size);
        game.addDisposalUnit(this);
    }

    public Pool getPool(){
        return pool;
    }

    public boolean isInitialized(GLGame game){
        return pool != null && game != null;
    }

    @Override
    public void dispose(){
        game = null;
        pool.clear();
        pool = null;
    }

}
