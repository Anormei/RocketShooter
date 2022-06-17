package com.ar_co.androidgames.rocketshooter.main.enemy;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;

public class HPBar {

    public static float IDLE_TIME = 5.0f;

    private Sprite leftOutline;
    private Sprite midOutline;
    private Sprite rightOutline;

    private Sprite bar;

    private Enemy enemy;

    private float lastUpdate = IDLE_TIME;
    private float lastHP;

    private float length;
    private float outlineLength;

    public HPBar(GLGame game, Enemy enemy){
        this.enemy = enemy;
        lastHP = enemy.HP;

        Assets assets = Assets.getInstance(game);
        length = 100.0f;
        outlineLength = length + 8.0f;
        leftOutline = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), 4.0f, 16.0f);
        midOutline = new Sprite(game, assets.getImage("graphics/gameplay/hp-bar/mid-outline"), outlineLength, 24.0f);
        rightOutline = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), 4.0f, 16.0f);

        bar = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), length, 8.0f);
    }

    public HPBar(GLGame game, Enemy enemy, float length){
        this.enemy = enemy;
        lastHP = enemy.HP;

        Assets assets = Assets.getInstance(game);
        this.length = length;
        outlineLength = length + 16.0f;
        leftOutline = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), 4.0f, 16.0f);
        midOutline = new Sprite(game, assets.getImage("graphics/gameplay/hp-bar/mid-outline"), outlineLength, 24.0f);
        rightOutline = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), 4.0f, 16.0f);

        bar = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), length, 8.0f);
    }

    public void update(float deltaTime){
        lastUpdate += deltaTime;

        leftOutline.pos.set(enemy.pos.x + enemy.width / 2.0f - outlineLength / 2.0f, enemy.pos.y - 28.0f);
        rightOutline.pos.set(enemy.pos.x + enemy.width / 2.0f + outlineLength / 2.0f + 4.0f, enemy.pos.y - 28.0f);
        midOutline.pos.set(enemy.pos.x + enemy.width / 2.0f - outlineLength / 2.0f + 4.0f, enemy.pos.y - 32.0f);

        bar.pos.set(enemy.pos.x + enemy.width / 2.0f - length / 2.0f + 4.0f, enemy.pos.y - 24.0f);
        bar.width = (enemy.HP / enemy.maxHP) * length;

        if(lastHP != enemy.HP){
            lastHP = enemy.HP;
            lastUpdate = 0;
        }
    }

    public void render(){
        if(lastUpdate < IDLE_TIME){
            leftOutline.render();
            midOutline.render();
            rightOutline.render();
            bar.render();
        }
    }

    public void reset(float HP){
        lastHP = HP;
        lastUpdate = IDLE_TIME;
    }



}
