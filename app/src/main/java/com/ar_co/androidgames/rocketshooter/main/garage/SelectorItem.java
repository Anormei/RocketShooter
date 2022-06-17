package com.ar_co.androidgames.rocketshooter.main.garage;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;
import com.ar_co.androidgames.rocketshooter.main.core.Item;
import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;
import com.ar_co.androidgames.rocketshooter.main.parts.Part;

public class SelectorItem extends Sprite{

    private static final float TEXT_POS_Y = 124.0f;

    private GLGame game;
    //private Sprite border;
    private Item item;
    private Sprite icon;
    private TextWriter textWriter;

    private String quantityText = "* %d";

    public SelectorItem(GLGame game, TextWriter textWriter){
        //super(game, Assets.getInstance(game).getImage("graphics/gameplay/garage/block-border"), 4.0f);
        super(game, Assets.getInstance(game).getImage("graphics/gameplay/garage/block-border"), 4.0f);
        this.game = game;
        icon = new Sprite(game, (Texture)null);
        this.textWriter = textWriter;
    }

    public void setPos(float x, float y){
        pos.set(x, y);
        if(item != null){
            icon.pos.set(pos.x + (width / 2.0f) - icon.width / 2.0f, pos.y + (height / 2.0f) - icon.height / 2.0f);
        }
    }

    public void addPos(float x, float y){
        pos.set(pos.x + x, pos.y + y);
        if(item != null){
            icon.pos.set(pos.x + (width / 2.0f) - icon.width / 2.0f, pos.y + (height / 2.0f) - icon.height / 2.0f);
        }
    }

    public void setItem(Item item){
        this.item = item;
       // Log.i("SelectorItem", "sprite = " + item.sprite.texture.file);

        if(item != null) {
            /*icon.copy(item.sprite);
            icon.scale(8.0f);*/
            icon.setTexture(Assets.getInstance(game).getImage(item.iconImage));
            icon.scale(8.0f);
            icon.pos.set(pos.x + (width / 2.0f) - icon.width / 2.0f, pos.y + (height / 2.0f) - icon.height / 2.0f);

        }else{
            icon.texture = null;
        }
    }

    public void update(float dt){
        if(item != null) {
            int quantity = item.getQuantity();
            float textWidth = textWriter.getTextWidth(quantityText) + textWriter.getNumberWidth(quantity);
            //Log.i("SelectorItem", "textWidth = " + textWriter.getTextWidth(quantityText) + ", numberWidth = " + textWriter.getNumberWidth(quantity) + ", totalWidth = " + textWidth);
            textWriter.writeFormattedText(pos.x + (width / 2.0f) - (textWidth / 2.0f), pos.y + TEXT_POS_Y, quantityText, quantity);
        }
    }

    public boolean isTouching(float x, float y){
        return x > pos.x && y > pos.y && x < pos.x + width && y < pos.y + height;
    }

    public Item item(){
        return item;
    }

    public boolean hasItem(){
        return item != null && item.getQuantity() > 0;
    }

    @Override
    public void render(){
        super.render();
        if(item != null){
            icon.render();
        }
    }
}
