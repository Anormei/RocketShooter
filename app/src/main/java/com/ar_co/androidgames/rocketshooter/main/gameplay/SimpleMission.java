package com.ar_co.androidgames.rocketshooter.main.gameplay;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.main.enemy.Enemy;
import com.ar_co.androidgames.rocketshooter.main.enemy.LargeObstacle;
import com.ar_co.androidgames.rocketshooter.main.enemy.MediumObstacle;
import com.ar_co.androidgames.rocketshooter.main.enemy.SmallObstacle;
import com.ar_co.androidgames.rocketshooter.main.screens.BattleScreen;

public class SimpleMission extends Mission {

    public SimpleMission(final GLGame game){
        super(game);
    }

    @Override
    public void init() {
        spawner.append(SmallObstacle.ID, new Pool<Enemy>(new Pool.PoolObjectFactory<Enemy>() {
            @Override
            public SmallObstacle createObject() {
                return new SmallObstacle(game);
            }
        }, 50));

        spawner.append(MediumObstacle.ID, new Pool<Enemy>(new Pool.PoolObjectFactory<Enemy>() {
            @Override
            public MediumObstacle createObject() {
                return new MediumObstacle(game);
            }
        }, 50));

        spawner.append(LargeObstacle.ID, new Pool<Enemy>(new Pool.PoolObjectFactory<Enemy>() {
            @Override
            public LargeObstacle createObject() {
                return new LargeObstacle(game);
            }
        }, 50));

        timeStamps = new TimeStamp[]{
                new TimeStamp(3.0f){
                    @Override
                    public void perform(){
                        for(int x = 0; x < BattleScreen.WORLD_WIDTH; x += 132.0f){
                            //Log.i("SimpleMission", "x = " + x);
                            gameHandler.spawnEnemy(spawner.get(MediumObstacle.ID).newObject().spawn(x, -132.0f));
                        }
                        complete = true;
                    }
                },

                new TimeStamp(10.0f){

                    @Override
                    public void perform() {

                        int x = 0;

                        while(x < BattleScreen.WORLD_WIDTH){
                            Enemy enemy = spawner.get(SmallObstacle.ID).newObject().spawn(0, 0);
                            enemy.pos.set(x, -enemy.height);
                            x += enemy.width * 2.0f;
                            gameHandler.spawnEnemy(enemy);
                        }

                        complete = true;
                    }
                },

                new TimeStamp(5.0f){

                    @Override
                    public void perform() {
                        Enemy largeObstacle = spawner.get(LargeObstacle.ID).newObject().spawn(0, 0);
                        largeObstacle.pos.set(BattleScreen.WORLD_WIDTH / 2.0f - largeObstacle.width / 2.0f, -largeObstacle.height);

                        gameHandler.spawnEnemy(largeObstacle);

                        Enemy mediumObstacle = spawner.get(MediumObstacle.ID).newObject().spawn(0, 0);
                        mediumObstacle.pos.set(largeObstacle.pos.x / 2.0f - mediumObstacle.width / 2.0f, -largeObstacle.height - mediumObstacle.height);

                        gameHandler.spawnEnemy(mediumObstacle);

                        mediumObstacle = spawner.get(MediumObstacle.ID).newObject().spawn(0, 0);
                        mediumObstacle.pos.set(largeObstacle.pos.x + largeObstacle.width + largeObstacle.pos.x / 2.0f - mediumObstacle.width / 2.0f, -largeObstacle.height - mediumObstacle.height);

                        gameHandler.spawnEnemy(mediumObstacle);

                        complete = true;
                    }
                },

                new TimeStamp(0){
                    @Override
                    public void perform(){
                        if(gameHandler.enemiesPresent()){
                            return;
                        }

                        for(int i = 0; i < 30; i++){
                            Enemy smallObstacle = spawner.get(SmallObstacle.ID).newObject().spawn(0, 0);
                            smallObstacle.pos.set(random.nextFloat() * BattleScreen.WORLD_WIDTH, -smallObstacle.height + random.nextFloat() * (-BattleScreen.WORLD_HEIGHT - -smallObstacle.height));
                            gameHandler.spawnEnemy(smallObstacle);
                        }

                        complete = true;
                    }
                },

                new TimeStamp(0){
                    @Override
                    public void perform(){
                        if(gameHandler.enemiesPresent()){
                            return;
                        }
                        for(int x = 0; x < BattleScreen.WORLD_WIDTH; x += 132.0f){
                            //Log.i("SimpleMission", "x = " + x);
                            gameHandler.spawnEnemy(spawner.get(MediumObstacle.ID).newObject().spawn(x, -132.0f));
                        }
                        complete = true;
                    }
                },

                new TimeStamp(10.0f){
                    @Override
                    public void perform(){

                        for(int i = 0; i < 30; i++){
                            Enemy smallObstacle = spawner.get(SmallObstacle.ID).newObject().spawn(0, 0);
                            smallObstacle.pos.set(random.nextFloat() * BattleScreen.WORLD_WIDTH, -smallObstacle.height + random.nextFloat() * (-BattleScreen.WORLD_HEIGHT - -smallObstacle.height));
                            gameHandler.spawnEnemy(smallObstacle);
                        }

                        complete = true;
                    }
                }

        };

    }

    @Override
    public void despawnEnemy(Enemy enemy) {
        spawner.get(enemy.ID()).free(enemy);
    }
}
