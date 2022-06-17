package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;

import java.util.List;

public class World extends SpriteButton{

    public boolean clicked;

    public World(GLGame game, Texture texture) {
        super(game, texture);
    }

    public World(GLGame game, Texture texture, float width, float height) {
        super(game, texture, width, height);
    }

    @Override
    public void onClick() {
        clicked = true;
    }

    @Override
    public void readTouchEvent(List<Input.TouchEvent> touchEvent, Camera2D camera){
        clicked = false;
        super.readTouchEvent(touchEvent, camera);
    }

}
