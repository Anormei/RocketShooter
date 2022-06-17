package com.ar_co.androidgames.rocketshooter.main.parts;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;
import com.ar_co.androidgames.rocketshooter.main.core.GridInfo;

import java.io.Serializable;

import dataconstructor.DataStructure;
import dataconstructor.DataStructureReader;

public class Part extends Sprite implements Serializable, DataStructureReader {

    public static final int TO_BYTES = 8;

    public static final int TYPE_BLOCK = 0;
    public static final int TYPE_WING = 1;
    public static final int TYPE_GUN = 2;
    public static final int TYPE_ENGINE = 3;

    public int id;

    public int type;

    public float HP;
    public float damage;
    public float power;
    public float mobility;
    public float speed;
    public int memory;

    public GridInfo gridInfo;


    public Part(GLGame game){
        super(game, (Texture)null);
        gridInfo = new GridInfo();
    }

    public Part(GLGame game, Texture texture) {
        super(game, texture, 4.0f);
        gridInfo = new GridInfo();
    }

    public Part(GLGame game, Texture texture, float id, float type, float HP, float damage, float power, float mobility, float speed, float memory){
        super(game, texture, 4.0f);
        gridInfo = new GridInfo();
    }

    public void flip(){
        gridInfo.flipped = !gridInfo.flipped;
        if(gridInfo.flipped) {
            setRegion(texture.width, 0, 0, texture.height);
        }else{
            defaultRegion();
        }
    }

    @Override
    public void readDataStructure(DataStructure dataStructure) {
        id = (int)dataStructure.values[0];
        texture = Assets.getInstance(game).getImage((String)dataStructure.values[1]);

        width = texture.width;
        height = texture.height;
        scale(4.0f);
        defaultRegion();

        gridInfo.rotationPoint.set(width / 2.0f, height / 2.0f);

        type = (int)dataStructure.values[2];
        HP = (float)dataStructure.values[3];
        damage = (float)dataStructure.values[4];
        power = (float)dataStructure.values[5];
        mobility = (float)dataStructure.values[6];
        speed = (float)dataStructure.values[7];
        memory = (int)dataStructure.values[8];
    }
}
