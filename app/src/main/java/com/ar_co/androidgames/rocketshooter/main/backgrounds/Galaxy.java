package com.ar_co.androidgames.rocketshooter.main.backgrounds;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.State;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Galaxy {

    private GLGame g;
    private final float WIDTH;
    private final float HEIGHT;

    private float x;
    private float y;

    private Vector2 nextPos;

    private List<Sprite> gridBlocks = new ArrayList<>();
    private Vector2 vel;

    private float angle;
    private float tAngle;
    private float rotationVel;
    private float time;
    private float[] rotationMatrix = new float[]{
            1,  0,
            0,  1

    };
    private State rotateGalaxy;


    private float rX;
    private float rY;

    private float size;

    private Sprite head;
    private Vector2 left;
    private Vector2 right;

    private Random r = new Random();
    private Pool<Sprite> pool;

    private State activeState;

    public float speed;

    float gridBlockWidth;
    float gridBlockHeight;

    public Galaxy(GLGame game, final float x, final float y, float width, float height, float speed){
        this.g = game;
        this.x = x;
        this.y = y;
        this.WIDTH = width;
        this.HEIGHT = height;

        nextPos = new Vector2();
        left = new Vector2();
        right = new Vector2();

        Log.i("Galaxy", String.format("x = %f, y = %f, width = %f, height = %f", x, y, width, height));

        rotateGalaxy = new State(){

            private float duration = 0;

            @Override
            public void update(float deltaTime) {
                duration += deltaTime;
                angle += rotationVel * deltaTime;
                directRotate(angle);
                if(duration >= time){
                    duration = 0;
                    directRotate(tAngle);
                    Log.i("Galaxy", "Angle = " + angle);
                    activeState = null;
                }
            }
        };

        //vvvv from setConfig

        this.speed = speed;

        angle = 90;
        float radians = angle * Vector2.TO_RADIANS;
        vel = new Vector2((float)Math.cos(radians) * speed, (float)Math.sin(radians) * speed);
        angle = 0;

        final Assets assets = Assets.getInstance(g);

        Texture gridBlockTexture = assets.getImage("graphics/gameplay/gridblock");

        gridBlockWidth = gridBlockTexture.width;
        gridBlockHeight = gridBlockTexture.height;

        float wLength = (int)Math.ceil(width / gridBlockWidth);

        float shavedWidth = (WIDTH - (gridBlockWidth * wLength)) / 2.0f;
        Log.i("Galaxy", "shavedWidth = " + shavedWidth);
        this.x += shavedWidth;


        int max = (int)(Math.ceil((WIDTH + gridBlockWidth * 2) * (HEIGHT + gridBlockHeight * 2) / (gridBlockWidth * gridBlockHeight)) * 1.5f);

        pool = new Pool<>(new Pool.PoolObjectFactory<Sprite>() {
            @Override
            public Sprite createObject() {
                Sprite gridBlock = new Sprite(g, assets.getImage("graphics/gameplay/gridblock"));
                gridBlock.rotationMatrix = rotationMatrix;
                gridBlock.rX = x + (WIDTH / 2.0f);
                gridBlock.rY = y + (HEIGHT / 2.0f);
                gridBlock.alpha = 0.3f;
                return gridBlock;
            }
        }, max);

        createBatch(WIDTH, HEIGHT);
    }

    public void update(float deltaTime){

        if(activeState != null){
            activeState.update(deltaTime);
        }

        if(speed == 0){
            return;
        }

        if(head.pos.y > y){
            createBatch(WIDTH, gridBlockHeight);
        }

        for(Iterator<Sprite> iterator = gridBlocks.iterator(); iterator.hasNext();){
            Sprite gridBlock = iterator.next();
            gridBlock.pos.add(vel.x * deltaTime, vel.y * deltaTime);
            /*if(star.angle != angle){
                star.rotate(angle);
            }*/
            if(gridBlock.pos.y > HEIGHT){
                pool.free(gridBlock);
                iterator.remove();
            }
        }
    }

    public void draw(float deltaTime, float left, float top, float right, float bottom){
        for(int i = 0; i < gridBlocks.size(); i++){
            Sprite star = gridBlocks.get(i);
            //if(star.pos.x < right && star.pos.x > left - size && star.pos.y < bottom && star.pos.y > top - size) {
                star.render();
           // }
        }
    }

    public void rotate(float angle, float duration){
        this.tAngle = angle;
        this.time = duration;

        activeState = rotateGalaxy;
        rotationVel = (tAngle - this.angle) / duration;
    }

    public void directRotate(float angle) {
        this.angle = angle;
        float rad = (angle) * Vector2.TO_RADIANS;
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        rotationMatrix[0] = cos;
        rotationMatrix[1] = -sin;
        rotationMatrix[2] = sin;
        rotationMatrix[3] = cos;
    }

        public void createBatch(float width, float height){
        //Log.i("Galaxy", "Batch created");
        int wLength = (int)Math.ceil(width / gridBlockWidth);
        int hLength = (int)Math.ceil(height/ gridBlockHeight);

        //Log.i("Galaxy", "wLength = " + wLength + ", hLength = " + hLength);
        boolean foundHead = false;
        float y1 = y;
        if(head != null) {
            //Log.i("Galaxy", String.format("head: x = %f, y = %f", head.pos.x, head.pos.y));
             y1 = head.pos.y;
        }

        for(int y0 = 0; y0 < hLength; y0++){
            for(int x0 = 0; x0 < wLength; x0++) {
                Sprite gridBlock = pool.newObject();
                gridBlock.pos.x = x + ((gridBlock.width) * x0);
                gridBlock.pos.y = (y1 - gridBlock.height) + (gridBlock.height * y0);
                //Log.i("Galaxy", String.format("gridBlock[%d]: x = %f, y2 = %f", x0, gridBlock.pos.x, gridBlock.pos.y + gridBlockHeight));
                if (!foundHead) {
                    head = gridBlock;
                    foundHead = true;
                    //Log.i("Galaxy", "Lowest = " + head.pos.y);
                }
                gridBlocks.add(gridBlock);
            }
        }
    }

}
