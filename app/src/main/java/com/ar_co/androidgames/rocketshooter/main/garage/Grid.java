package com.ar_co.androidgames.rocketshooter.main.garage;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.core.Inventory;
import com.ar_co.androidgames.rocketshooter.main.parts.Part;
import com.ar_co.androidgames.rocketshooter.main.screens.GarageScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Grid {

    private static final float GRID_BLOCK_LENGTH = 20.0f;
    private static final float PART_MOVE_SENSITIVITY = 20.0f;
    private static final float LONG_HOLD_TIME = 1.0f;

    private GLGame game;
    private Sprite[] grid;
    private Part[] parts;
    private List<Part> partList = new ArrayList<>();
    private int size;

    private Vector2 touchPos0;

    private Vector2 lastTouchDrag0;
    private Vector2 lastTouchDrag1;

    private Vector2 touchDrag0;
    private Vector2 touchDrag1;

    private Vector2 math;

    private Vector2 trueTouch0;

    private boolean finger0;
    private boolean finger1;

    private boolean cameraControl;
    private boolean moveEnabled;
    private Part partSelected;

    private Vector2 pos;

    private ItemExtractor itemSetter;
    private boolean disableRotation;

    private float longHold = 0.0f;
    private boolean flipped = false;

    public Grid(GLGame game, ItemExtractor itemExtractor, int size){
        //blocks = new Block[size];
        this.game = game;
        this.size = size;
        grid = new Sprite[size * size];

        this.itemSetter = itemExtractor;

        float posX = GarageScreen.WORLD_WIDTH / 2.0f - ((GRID_BLOCK_LENGTH * size + 2.0f) / 2.0f);
        float posY = GarageScreen.WORLD_HEIGHT / 2.0f - ((GRID_BLOCK_LENGTH * size + 2.0f) / 2.0f);

        pos = new Vector2(posX, posY);

        for(int i = 0; i < grid.length; i++){
            Sprite gridBlock = new Sprite(game, Assets.getInstance(game).getImage("graphics/gameplay/garage/grid"));
            gridBlock.pos.set(posX + ((i % size) * GRID_BLOCK_LENGTH), posY + ((i / size) * GRID_BLOCK_LENGTH));
            //gridBlock.alpha = 0f;
            grid[i] = gridBlock;
        }

        parts = new Part[size * size];

        lastTouchDrag0 = new Vector2();
        lastTouchDrag1 = new Vector2();

        touchDrag0 = new Vector2();
        touchDrag1 = new Vector2();

        touchPos0 = new Vector2();
        math = new Vector2();
        trueTouch0 = new Vector2();
    }

    public boolean handleTouchEvent(Input.TouchEvent touchEvent, Camera2D gridCamera){

            //Log.i("GarageScreen", "touch.x = " + gridCamera.touchX(touchEvent.x) + ", touch.y = " + gridCamera.touchY(touchEvent.y));


            float x = gridCamera.nativeTouchX(touchEvent.x);
            float y = gridCamera.nativeTouchY(touchEvent.y);

            float touchX = gridCamera.touchX(touchEvent.x);
            float touchY = gridCamera.touchY(touchEvent.y);

            trueTouch0.set(touchX, touchY);

            if (touchEvent.pointerId == 0) {
                if (touchEvent.type == Input.TouchEvent.TOUCH_DOWN) {
                    if(y > 360.0f && y < 1652.0f){
                        cameraControl = true;
                        partSelected = null;
                        disableRotation = false;
                        for(Iterator<Part> iterator = partList.iterator(); iterator.hasNext();){
                            Part part = iterator.next();

                            if(touchX > part.pos.x && touchY > part.pos.y && touchX < part.pos.x + part.width && touchY < part.pos.y + part.height){

                                cameraControl = false;
                                partSelected = part;
                                longHold = 0.0f;
                                flipped = false;

                                //Inventory.getInstance(game).selectItem(partSelected.id).putAway(partSelected);
                                iterator.remove();
                            }
                        }
                    }

                    finger0 = true;
                    touchPos0.set(x, y);
                    lastTouchDrag0.set(x, y);
                    touchDrag0.set(x, y);
                } else if (touchEvent.type == Input.TouchEvent.TOUCH_UP) {
                    //float length = math.set(touchPos0).sub(touchDrag0).length();
                    if(!disableRotation && partSelected != null && !flipped){
                        //partSelected.rotate(partSelected.angle + 90.0f);
                        Sprite g = grid[partSelected.gridInfo.y * size + partSelected.gridInfo.x];
                        float pointX = (g.pos.x + g.width / 2.0f) - partSelected.pos.x;
                        float pointY = (g.pos.y + g.height / 2.0f) - partSelected.pos.y;
                        partSelected.gridInfo.rotationPoint.set(pointX, pointY);
                        partSelected.gridInfo.rotate();
                        Log.i("Grid", "Rotate part");
                    }

                    deselectPart();// && partSelected != null;
                    finger0 = false;
                }
            }

            if (touchEvent.pointerId == 1) {
                if (touchEvent.type == Input.TouchEvent.TOUCH_DOWN) {
                        finger1 = true;
                        lastTouchDrag1.set(x, y);
                        touchDrag1.set(x, y);
                        deselectPart();
                } else if (touchEvent.type == Input.TouchEvent.TOUCH_UP) {

                        finger1 = false;
                        cameraControl = finger0 && partSelected == null;
                }
            }

            if (touchEvent.type == Input.TouchEvent.TOUCH_DRAGGED) {
                if (touchEvent.pointerId == 0 && finger0) {
                        touchDrag0.set(x, y);
                }
                if ((touchEvent.pointerId == 1 && finger1) || (finger1 && !finger0)) {
                        touchDrag1.set(x, y);

                }
            }

            return cameraControl;
    }

    public void update(float deltaTime, Camera2D gridCamera){

        if(cameraControl) {
            if (finger0 && finger1) {

                float difference = lastTouchDrag0.dist(lastTouchDrag1) / touchDrag0.dist(touchDrag1);

                if (difference != 1.0f) {
                    gridCamera.zoom *= difference;
                }

                if (gridCamera.zoom < 0.025f) {
                    gridCamera.zoom = 0.025f;
                }

                if (gridCamera.zoom > 1.0f) {
                    gridCamera.zoom = 1.0f;
                }
            }

            float frustumWidth = gridCamera.getRightFrustum() - gridCamera.getLeftFrustum();
            float frustumHeight = gridCamera.getBottomFrustum() - gridCamera.getTopFrustum();

            if (finger0) {

                gridCamera.x += (lastTouchDrag0.x - touchDrag0.x) / GarageScreen.WORLD_WIDTH * frustumWidth;
                gridCamera.y += (lastTouchDrag0.y - touchDrag0.y) / GarageScreen.WORLD_HEIGHT * frustumHeight;

            }

            if (finger1) {

                gridCamera.x += (lastTouchDrag1.x - touchDrag1.x) / GarageScreen.WORLD_WIDTH * frustumWidth;
                gridCamera.y += (lastTouchDrag1.y - touchDrag1.y) / GarageScreen.WORLD_HEIGHT * frustumHeight;
            }

            float leftFrustum = gridCamera.getLeftFrustum();
            float topFrustum = gridCamera.getTopFrustum();
            float rightFrustum = gridCamera.getRightFrustum();
            float bottomFrustum = gridCamera.getBottomFrustum();

            if (leftFrustum < 0) {
                gridCamera.x -= leftFrustum;
            }

            if (topFrustum < 0) {
                gridCamera.y -= topFrustum;

            }

            if (rightFrustum > GarageScreen.WORLD_WIDTH) {
                gridCamera.x -= rightFrustum - GarageScreen.WORLD_WIDTH;
            }

            if (bottomFrustum > GarageScreen.WORLD_HEIGHT) {
                gridCamera.y -= bottomFrustum - GarageScreen.WORLD_HEIGHT;
            }
        }

        float length = math.set(touchDrag0).sub(touchPos0).length();

        longHold += deltaTime;

        if(finger0 && !cameraControl && longHold >= LONG_HOLD_TIME && !flipped){
            if(partSelected != null){
                flipped = true;
                partSelected.flip();
            }
        }

        if(partSelected != null && length > PART_MOVE_SENSITIVITY && !cameraControl){
            disableRotation = true;
            if(!itemSetter.hasItem()) {
                //Log.i("Grid", "Yes");
                itemSetter.attachItem(partSelected, trueTouch0.x, trueTouch0.y);

                parts[partSelected.gridInfo.x + partSelected.gridInfo.y * size] = null;
                partSelected = null;
            }
        }

        lastTouchDrag0.set(touchDrag0);
        lastTouchDrag1.set(touchDrag1);
    }

    public void placeBlock(Part part, boolean samePart){
        //float x = part.pos.x + part.width / 2.0f;
        //float y = part.pos.y + part.height / 2.0f;

        float x = part.pos.x + GRID_BLOCK_LENGTH / 2.0f;
        float y = part.pos.y + GRID_BLOCK_LENGTH / 2.0f;

        float gridWidth = GRID_BLOCK_LENGTH * size;
        float gridHeight = GRID_BLOCK_LENGTH * size;

        //Log.i("Grid", String.format("grid.x = %f, grid.y = %f, grid.x2 = %f, grod.y2 = %f", pos.x, pos.y, pos.x + gridWidth, pos.y + gridHeight));
        //Log.i("Grid", String.format("block.x = %f, block.y = %f, block.midX = %f, block.midY = %f", block.pos.x, block.pos.y, x, y));



        if(x > pos.x && x < pos.x + gridWidth && y > pos.y && y < pos.y + gridHeight){
            int gridX = (int)((x - pos.x) / (gridWidth / size));
            int gridY = (int)((y - pos.y) / (gridHeight / size));
            int index = gridX + gridY * size;

            //Log.i("Grid", "Index = " + index + ", x index = " + (x - pos.x) / (gridWidth / size) + ", y index = " + ((y - pos.y) / (gridHeight / size)));
            if(parts[index] != null && !samePart){
                Log.i("Grid", "New part");
                Inventory.getInstance(game).selectItem(parts[index].id).putAway(parts[index]);
            }
            part.gridInfo.set(gridX, gridY);
            //part.pos.set(grid[index].pos.x + grid[index].width / 2.0f - part.width / 2.0f, grid[index].pos.y + grid[index].height / 2.0f - part.height / 2.0f);
            part.pos.set(grid[index].pos.x + 1.0f, grid[index].pos.y + 1.0f);
            part.rotate(part.pos.x + part.gridInfo.rotationPoint.x, part.pos.y + part.gridInfo.rotationPoint.y, part.gridInfo.rotation * 90.0f);
            //Log.i("Grid", String.format("block.pos: x = %f, y = %f", block.pos.x, block.pos.y));
            parts[index] = part;
            partList.add(part);
        }else if(!samePart){
            Log.i("Grid", "New part");
            Inventory.getInstance(game).selectItem(part.id).putAway(part);
        }
    }

    public void render(){
        for(int i = 0; i < grid.length; i++){
            grid[i].render();
            if(parts[i] != null){
                parts[i].render();
            }
        }
    }

    private void deselectPart(){

        if(partSelected != null) {
            placeBlock(partSelected, true);
        }
        partSelected = null;
        cameraControl = finger1;
    }
}
