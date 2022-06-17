package com.ar_co.androidgames.rocketshooter.main.gameplay;

import com.ar_co.androidgames.rocketshooter.main.enemy.Enemy;

public interface GameHandler {

    void spawnEnemy(Enemy enemy);

    boolean enemiesPresent();

    void setMovementDirection(float angle);


}
