package com.ar_co.androidgames.rocketshooter.framework;

import android.graphics.RectF;
import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

import java.util.ArrayList;
import java.util.List;

public class Sprite {

    public static float TO_RADIANS = (1.0f / 180.0f) * (float) Math.PI;

    public Texture texture;
    //protected Vertices vertices;

    protected float[] verticesBuffer = new float[32];
    //protected short[] indices = new short[]{0, 1, 2, 2, 3, 1};

    public Vector2 pos;
    public Vector2 vel;
    public Vector2 acc;

    public float angle = 0;

    public float rX;
    public float rY;

    public float width;
    public float height;
    protected RectF region = new RectF();
    public float[] rgb = new float[]{1, 1, 1};

    public float alpha = 1;

    public Body body;
    protected Projection p1 = new Projection();
    protected Projection p2 = new Projection();

    protected GLGraphics glGraphics;
    protected GLGame game;

    private RectF boundary0 = new RectF();
    private RectF boundary1 = new RectF();

    private List<HitBox> hitBoxes = new ArrayList<>();
    private List<HitBox> targetHitBoxes = new ArrayList<>();

    public float[] rotationMatrix = new float[]{
            1,  0,
            0,  1

    };

    public Sprite(GLGame game, Texture texture) {
        this.game = game;
        this.glGraphics = game.getGLGraphics();
        pos = new Vector2();
        vel = new Vector2();
        acc = new Vector2();
        //this.vertices = new Vertices(glGraphics, 4, 6, true, true);
        //vertices.setIndices(indices, 0, 6);

        if (texture != null) {
            this.width = texture.width;
            this.height = texture.height;
            this.texture = texture;
            defaultRegion();
           //this.body = Assets.getInstance(game).getHitBox(texture.file);
            //Log.i("Sprite", texture.file);
        }/*else{
            this.texture = Assets.getInstance(game).getEmptyTexture();
        }*/

    }

    public Sprite(GLGame game, Texture texture, float width, float height) {
        this(game, texture);
        this.width = width;
        this.height = height;
    }

    public Sprite(GLGame game, Texture texture, float scale){
        this(game, texture, texture.width * scale, texture.height * scale);
    }

    public Sprite(GLGame game, Sprite sprite){
        this(game, sprite.texture, sprite.width, sprite.height);
    }

    public void render(){
        //Log.i("Sprite", "is texture null? " + (texture == null));
        if(texture != null) {
            texture.atlas.batcher.visual.draw(this);
        }
    }

    public void copy(Sprite sprite){
        this.texture = sprite.texture;
        this.width = sprite.width;
        this.height = sprite.height;
        defaultRegion();
    }

    public void setRegion(float x, float y, float width, float height) {
        float w = texture.atlas.width;
        float h = texture.atlas.height;

        region.left = (texture.x + x) / w;
        region.top = (texture.y + y) / h;
        region.right = (texture.x + width) / w;
        region.bottom = (texture.y + height) / h;
    }

    public void defaultRegion(){
        float w = texture.atlas.width;
        float h = texture.atlas.height;

        region.left = texture.x / w;
        region.top = texture.y / h;
        region.right = (texture.x + texture.width) / w;
        region.bottom = (texture.y + texture.height) / h;

        /*region.right = texture.x / w;
        region.top = texture.y / h;
        region.left = (texture.x + texture.width) / w;
        region.bottom = (texture.y + texture.height) / h;*/
    }

    public void scale(float val){
        width *= val;
        height *= val;
    }

    public void rotate(float angle) {
        this.angle = angle;
        float rad = angle * TO_RADIANS;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        rX = pos.x + width / 2;
        rY = pos.y + height / 2;

        rotationMatrix[0] = cos;
        rotationMatrix[1] = -sin;
        rotationMatrix[2] = sin;
        rotationMatrix[3] = cos;

    }

    public void rotate(float x, float y, float angle) {
        this.angle = angle;
        float rad = angle * TO_RADIANS;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        rX = x;
        rY = y;

        rotationMatrix[0] = cos;
        rotationMatrix[1] = -sin;
        rotationMatrix[2] = sin;
        rotationMatrix[3] = cos;

    }

    public void setTexture(Texture texture){
        this.width = texture.width;
        this.height = texture.height;
        this.texture = texture;
        defaultRegion();
    }

    public boolean isTouching(Sprite target) {

        boundary0.set(
                rotationMatrix[0] * (pos.x - rX) + rotationMatrix[1] * (pos.y - rY) + rX,
                rotationMatrix[2] * (pos.x - rX) + rotationMatrix[3] * (pos.y - rY) + rY,
                rotationMatrix[0] * (pos.x + width - rX) + rotationMatrix[1] * (pos.y + height - rY) + rX,
                rotationMatrix[2] * (pos.x + width - rX) + rotationMatrix[3] * (pos.y + height - rY) + rY
                );

        boundary1.set(
                rotationMatrix[0] * (target.pos.x - target.rX) + rotationMatrix[1] * (target.pos.y - target.rY) + target.rX,
                rotationMatrix[2] * (target.pos.x - target.rX) + rotationMatrix[3] * (target.pos.y - target.rY) + target.rY,
                rotationMatrix[0] * (target.pos.x + target.width - target.rX) + rotationMatrix[1] * (target.pos.y + target.height - target.rY) + target.rX,
                rotationMatrix[2] * (target.pos.x + target.width - target.rX) + rotationMatrix[3] * (target.pos.y + target.height - target.rY) + target.rY
        );

        if(!boundary0.intersect(boundary1)) {
            return false;
        }

        /**if(boundary0.contains(boundary1) || boundary1.contains(boundary0)){
            return true;
        }*/

        //Log.i("Sprite", "Boundaries intersecting");

        hitBoxes.clear();
        targetHitBoxes.clear();

        body.findHitBoxes(this, target, hitBoxes, targetHitBoxes);

        int j = 0;

        boolean overlaps;

        //Log.i("Sprite", "hitBoxes.size() = " + hitBoxes.size() + ", targetHitBoxes.size() = " + targetHitBoxes.size());

        for(int i = 0; i < hitBoxes.size(); i++) {
            HitBox hitBox = hitBoxes.get(i);
            overlaps = true;
            while(hitBox.count > 0 && j < targetHitBoxes.size() && overlaps){
                //Log.i("Sprite", "Doing collision calculation");
                HitBox targetHitBox = targetHitBoxes.get(j);

                Vector2[] axes1 = hitBox.getAxis(this);
                Vector2[] axes2 = targetHitBox.getAxis(target);

                HitBox hitBox1;
                HitBox hitBox2;

                Sprite sprite1;
                Sprite sprite2;

                if(hitBox.size() < targetHitBox.size()){
                    hitBox1 = hitBox;
                    hitBox2 = targetHitBox;

                    sprite1 = this;
                    sprite2 = target;
                }else{
                    hitBox1 = targetHitBox;
                    hitBox2 = hitBox;

                    sprite1 = target;
                    sprite2 = this;
                }

                for (int k = 0; k < axes1.length; k++) {
                    Vector2 axis = axes1[k];
                    hitBox1.project(sprite1, axis, p1);
                    hitBox2.project(sprite2, axis, p2);
                    if (!p1.overlap(p2)) {
                        overlaps = false;
                    }
                }

                for (int l = 0; l < axes2.length; l++) {
                    Vector2 axis = axes2[l];
                    hitBox1.project(sprite1, axis, p1);
                    hitBox2.project(sprite2, axis, p2);
                    if (!p1.overlap(p2)) {
                        overlaps = false;
                    }
                }

                j++;
                hitBox.count--;
            }

            if(overlaps){
                return true;
            }
        }

        return false;
    }


}
    