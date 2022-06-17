package com.ar_co.androidgames.rocketshooter.framework;

import android.graphics.RectF;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import dataconstructor.DataStructure;
import dataconstructor.DataStructureReader;

public class Body implements DataStructureReader {

    public HitBox[] hitBoxes;
    public String fileName;

    private float width;
    private float height;

    private RectF boundary0 = new RectF();
    private RectF boundary1 = new RectF();

    public Body(GLGame game, String fileName){

        try{
            InputStream fileIn = game.getAssets().open(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            readDataStructure((DataStructure)in.readObject());
            fileIn.close();
            in.close();
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public Body(GLGame game, String fileName, float width, float height, HitBox... hitBoxes){
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.hitBoxes = hitBoxes;
    }

    public void findHitBoxes(Sprite sprite, Sprite target, List<HitBox> collidingHitBoxes, List<HitBox> collidingTargetHitBoxes){

        boolean found;

        float scaleX = sprite.width / sprite.texture.width;
        float scaleY = sprite.height / sprite.texture.height;

        float targetScaleX = target.width / target.texture.width;
        float targetScaleY = target.height / target.texture.height;

        for(int i = 0; i < hitBoxes.length; i++){
            HitBox hitBox = hitBoxes[i];
            hitBox.count = 0;
            found = false;
            boundary0.set(
                    sprite.rotationMatrix[0] * (sprite.pos.x + hitBox.pos.x * scaleX - sprite.rX) + sprite.rotationMatrix[1] * (sprite.pos.y + hitBox.pos.y * scaleY - sprite.rY) + sprite.rX,
                    sprite.rotationMatrix[2] * (sprite.pos.x + hitBox.pos.x * scaleX - sprite.rX) + sprite.rotationMatrix[3] * (sprite.pos.y + hitBox.pos.y * scaleY - sprite.rY) + sprite.rY,
                    sprite.rotationMatrix[0] * (sprite.pos.x + (hitBox.pos.x + hitBox.width) * scaleX - sprite.rX) + sprite.rotationMatrix[1] * (sprite.pos.y + (hitBox.pos.y + hitBox.height) * scaleY + height - sprite.rY) + sprite.rX,
                    sprite.rotationMatrix[2] * (sprite.pos.x + (hitBox.pos.x + hitBox.width) * scaleX + sprite.rX) + sprite.rotationMatrix[3] * (sprite.pos.y + (hitBox.pos.y + hitBox.height) * scaleY - sprite.rY) + sprite.rY
            );

            for(int j = 0; j < target.body.hitBoxes.length; j++){
                HitBox targetHitBox = target.body.hitBoxes[j];
                boundary1.set(
                        target.rotationMatrix[0] * (target.pos.x + targetHitBox.pos.x * targetScaleX - target.rX) + target.rotationMatrix[1] * (target.pos. y + targetHitBox.pos.y * targetScaleY - target.rY) + target.rX,
                        target.rotationMatrix[2] * (target.pos.x + targetHitBox.pos.x * targetScaleX - target.rX) + target.rotationMatrix[3] * (target.pos.y + targetHitBox.pos.y * targetScaleY - target.rY) + target.rY,
                        target.rotationMatrix[0] * (target.pos.x + (targetHitBox.pos.x + targetHitBox.width) * targetScaleX - target.rX) + target.rotationMatrix[1] * (target.pos.y + (targetHitBox.pos.y + targetHitBox.height) * targetScaleY + height - target.rY) + target.rX,
                        target.rotationMatrix[2] * (target.pos.x + (targetHitBox.pos.x + targetHitBox.width) * targetScaleX + target.rX) + target.rotationMatrix[3] * (target.pos.y + (targetHitBox.pos.y + targetHitBox.height) * targetScaleY - target.rY) + target.rY
                );

                if(boundary0.intersect(boundary1)){
                    collidingTargetHitBoxes.add(targetHitBox);
                    hitBox.count++;
                    found = true;
                }
            }

            if(found){
                collidingHitBoxes.add(hitBox);
            }
        }
    }

    @Override
    public void readDataStructure(DataStructure dataStructure) {
        DataStructure hitBoxesData = (DataStructure)dataStructure.values[0];
        hitBoxes = new HitBox[hitBoxesData.size()];
        for(int i = 0; i < hitBoxes.length; i++){
            hitBoxes[i] = new HitBox();
            hitBoxes[i].readDataStructure((DataStructure)hitBoxesData.values[i]);
        }

        //fileName = (String)dataStructure.values[1];
        width = (float)dataStructure.values[2];
        height = (float)dataStructure.values[3];
    }
}
