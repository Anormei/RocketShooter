package com.ar_co.androidgames.rocketshooter.main.sprites;

import android.graphics.Camera;
import android.text.method.Touch;

import com.ar_co.androidgames.rocketshooter.framework.Button;
import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;

import java.util.List;

public abstract class SpriteButton extends Sprite {

    private Button button;
    private Texture upButton;
    private Texture downButton;

    public SpriteButton(GLGame game, Texture texture) {
        super(game, texture);

        button = new Button(pos.x, pos.y, width, height){
            @Override
            public void onClick(){
                SpriteButton.this.onClick();
            }
        };
    }

    public SpriteButton(GLGame game, Texture texture, float width, float height) {
        super(game, texture, width, height);
        button = new Button(pos.x, pos.y, width, height){
            @Override
            public void onClick(){
                SpriteButton.this.onClick();
            }
        };
    }

    public SpriteButton(GLGame game, Texture texture, Texture downButton) {
        super(game, texture);
        this.upButton = texture;
        this.downButton = downButton;
        button = new Button(pos.x, pos.y, width, height){
            @Override
            public void onClick(){
                SpriteButton.this.onClick();
            }

            @Override
            public void onInactive(){
                SpriteButton.this.texture = SpriteButton.this.upButton;
            }
        };
    }

    public SpriteButton(GLGame game, Texture texture, Texture downButton, float width, float height) {
        super(game, texture, width, height);
        this.upButton = texture;
        this.downButton = downButton;
        button = new Button(pos.x, pos.y, width, height){
            @Override
            public void onClick(){
                SpriteButton.this.onClick();
            }

            @Override
            public void onInactive(){
                SpriteButton.this.texture = SpriteButton.this.upButton;
            }
        };
    }

    public abstract void onClick();

    public void readTouchEvent(List<Input.TouchEvent> touchEvent, Camera2D camera){
        for(int i = 0; i < touchEvent.size(); i++){
            Input.TouchEvent event = touchEvent.get(i);
            button.x = pos.x;
            button.y = pos.y;
            button.readTouchEvent(event.type, camera.touchX(event.x), camera.touchY(event.y));
        }
    }
}
