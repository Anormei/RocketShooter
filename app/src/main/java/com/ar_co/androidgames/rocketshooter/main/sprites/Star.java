package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

import java.util.Random;

public class Star extends Sprite {

    private static Random r = new Random();

    public Star(GLGame game, float size){
        super(game, RandomizeTexture(game), size);
    }

    public static Texture RandomizeTexture(GLGame game){
        String path = r.nextBoolean() ? "graphics/gameplay/one" : "graphics/gameplay/zero";
        return Assets.getInstance(game).getImage(path);
    }
}
