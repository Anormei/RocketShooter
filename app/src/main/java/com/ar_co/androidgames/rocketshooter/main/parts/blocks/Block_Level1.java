package com.ar_co.androidgames.rocketshooter.main.parts.blocks;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.main.parts.Part;

public class Block_Level1 extends Part {

    public static final int ID = 1;

    public Block_Level1(GLGame game){
        super(game, Assets.getInstance(game).getImage("graphics/gameplay/parts/square_a"));

        type = Part.TYPE_BLOCK;

        HP = 5.0f;
        damage = 0;
        power = 0;
        mobility = 0;
        speed = 0;
        memory = 8 * TO_BYTES;
    }

    /*@Override
    public int ID() {
        return ID;
    }*/
}
