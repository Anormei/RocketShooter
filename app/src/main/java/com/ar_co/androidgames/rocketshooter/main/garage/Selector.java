package com.ar_co.androidgames.rocketshooter.main.garage;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.core.Inventory;
import com.ar_co.androidgames.rocketshooter.main.core.ItemMetaData;
import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;
import com.ar_co.androidgames.rocketshooter.main.parts.Part;
import com.ar_co.androidgames.rocketshooter.main.screens.GarageScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Selector {

    public static final int SELECTOR_SIZE = 9;
    private static final float ITEM_SIZE = 176.0f;
    private static final float ITEM_SPACE = 20.0f;
    //private static final float START = -136.0f; //was 60.0f;
    private static final float START = -332.0f;
    private static final float ITEM_Y = 64.0f;

    private static final float SCROLL_ITEM_DRAG_SENSITIVITY = 50.0f;

    private static final float SCROLL_BAR_START_X = 40.0f;
    private static final float SCROLL_BAR_END_X = 1040.0f;
    private static final float SCROLL_BAR_WIDTH = 1000.0f;
    private static final float END;

    static{
        END = START + (ITEM_SIZE + ITEM_SPACE) * SELECTOR_SIZE;
    }


    private Sprite window;
    private Sprite scrollBar;
    private Inventory inventory;


    private List<SelectorItem> selection = new ArrayList<>();
    private SelectorItem head;
    private SelectorItem tail;

    private Vector2 vel;
    private Vector2 acc;

    private float x;
    private float y;

    private ItemExtractor itemExtractor;

    private int itemPos;
    private int prevItemPos;
    private boolean itemChanged;

    private int inventorySize;
    //private int inventoryStartIndex;

    private int selectionSize;

    private Pool<SelectorItem> selectorItemPool;

    private boolean select;
    private boolean scrolling;
    private boolean itemDrag;

    private boolean finger0;

    private Vector2 touchPos0;
    private Vector2 touchDrag0;
    private Vector2 lastTouchDrag0;

    private Vector2 math;

    private Vector2 difference;
    private TextWriter textWriter;

    private float scrollEndX;
    private float scrollLength;
    private float scrollVal = 0;

    private SelectorItem selectedItem;
    private ItemMetaData metaData;

    public Selector(final GLGame game, ItemExtractor itemExtractor, float x, float y){
        Assets assets = Assets.getInstance(game);

        this.x = x;
        this.y = y;
        this.textWriter = new TextWriter(game, "graphics/gameplay/debug/charset", 50);

        inventory = Inventory.getInstance(game);

        this.itemExtractor = itemExtractor;

        selectorItemPool = new Pool<>(new Pool.PoolObjectFactory<SelectorItem>() {
            @Override
            public SelectorItem createObject() {
                return new SelectorItem(game, textWriter);
            }
        }, 10);

        //itemPos = 0;

        metaData = inventory.getMetaData(Inventory.PARTS);
        inventorySize = metaData.rightBoundary - metaData.leftBoundary;
        Log.i("Selector", "inventorySize = " + inventorySize);

        //Make sure selection has 7 or more (if inventory has more), to make sure items are not repeated in the same section of items.
        selectionSize = Math.max(inventorySize, SELECTOR_SIZE);

        window = new Sprite(game, assets.getImage("graphics/gameplay/garage/selector"), 4.0f);
        window.pos.set(x, y);

        //Graphical representation of scrollbar.
        scrollBar = new Sprite(game, assets.getImage("graphics/gameplay/NoTexture"), SCROLL_BAR_WIDTH, 20.0f);
        scrollBar.width = 500;
        scrollBar.pos.set(x + SCROLL_BAR_START_X, y + 236.0f);

        //Scrollbar x limit.
        scrollEndX = SCROLL_BAR_END_X - scrollBar.width;

        //Creates the selector boxes and places them.
        for(int i = 0; i < SELECTOR_SIZE; i++){
            SelectorItem selectorItem = selectorItemPool.newObject();

            selectorItem.setPos(x + START + (i * (selectorItem.width + ITEM_SPACE)), y + 60.0f);

            selection.add(selectorItem);
        }

        //Gets selector at start and end
        head = selection.get(0);
        tail = selection.get(selection.size() - 1);

        //The length of a selector + margin multiplied by the selection size.
        scrollLength = (selection.get(0).width + ITEM_SPACE) * selectionSize;
        Log.i("Selector", "Selection width = " + selection.get(0).width);

        vel = new Vector2();
        acc = new Vector2();

        int inventoryIndex = metaData.leftBoundary;
        //Places items up to the selection size limit, if the inventory has less items, fill in empty.
        for(int i = 2; i < selection.size(); i++){
            if(inventoryIndex - metaData.leftBoundary < inventorySize){
                selection.get(i).setItem(inventory.selectItem(inventoryIndex));
                Log.i("Selector", "is id " + inventoryIndex + ", null? " + (inventory.selectItem(inventoryIndex) == null));
            }
            inventoryIndex++;
        }

        inventoryIndex = metaData.rightBoundary - 1;

        if(inventorySize > SELECTOR_SIZE){
            selection.get(0).setItem(inventory.selectItem(inventoryIndex));
            selection.get(1).setItem(inventory.selectItem(inventoryIndex - 1));
        }



        //selection.get(1).setItem(inventory.getItemByID(Square_A.ID));

        itemPos = selectionSize - 2;
        //prevItemPos = 6;

        touchPos0 = new Vector2();
        touchDrag0 = new Vector2();
        lastTouchDrag0 = new Vector2();
        difference = new Vector2();
        math = new Vector2();

        scrolling = false;
        itemDrag = false;

        Log.i("Selector", "scrollLength = " + scrollLength);
    }

    public void update(float dt){

        //float difference = (touchDrag0.x - touchPos0.x) / dt;

        //Difference between last drag input and current input
        difference.set(touchDrag0).sub(lastTouchDrag0).div(dt);
        /*if(difference.x > 10000.0f){
            difference.x = 10000.0f;
        }*/

        itemChanged = false;

        float angle = math.set(touchDrag0).sub(touchPos0).angle();
        //True if scroll angle is left or right within a range
        boolean scrollAngle = ((angle > 315.0f && angle <= 360.0) || (angle >= 0 && angle < 180.0f)) ||
                (angle > 180.0f && angle < 225.0f);

        //Log.i("Selctor", "angle = " + angle);

        if(scrollAngle && !itemDrag) {
            if (finger0) {//difference.x != 0 && Math.abs(difference.x) > vel.length()) {
                vel.set(difference.x, 0);
                acc.set(-(difference.x), 0);

                if(math.length() > SCROLL_ITEM_DRAG_SENSITIVITY){
                    scrolling = true;
                }
            }
        }else if(!itemDrag && finger0 && !scrolling){

            if(math.length() > SCROLL_ITEM_DRAG_SENSITIVITY) {
                if (selectedItem != null && selectedItem.hasItem()) {
                    itemDrag = true;
                    itemExtractor.attachItem((Part)selectedItem.item().retrieve(), touchDrag0.x, touchDrag0.y);
                }
            }

            //Log.i("Selector", "false, angle = " + angle);
            vel.set(0, 0);
            acc.set(0, 0);
        }

        if (vel.x != 0 && !finger0) {
            vel.add(acc.x * dt, 0);

            if ((acc.x < 0 && vel.x < 0) || (acc.x > 0 && vel.x > 0)) {
                vel.set(0, 0);
            }
        }

        //Log.i("Selector", "velocity.x = " + vel.x);

        scrollVal -= vel.x * dt;

        while(scrollVal < 0){
            scrollVal = scrollVal + scrollLength;
        }

        scrollVal %= scrollLength;

        float ratio = scrollVal / scrollLength;

        scrollBar.pos.x = SCROLL_BAR_START_X + (scrollEndX - SCROLL_BAR_START_X) *  ratio;

        //itemPos = (Math.round(((float)selectionSize) * ratio) + selectionSize - 1) % selectionSize; //or + selectionSize - 1??
        //Log.i("Selector", "itemPos = " + itemPos + ", selectionSize = " + selectionSize);
        //Log.i("Selector", String.format("selectionSize = %d scrollVal = %f, scrollLength = %f, ratio = %f, itemPos = %d", selectionSize, scrollVal, scrollLength, ratio, itemPos));
        textWriter.startWriting();
        for(Iterator<SelectorItem> iterator = selection.iterator(); iterator.hasNext();){
            SelectorItem selectorItem = iterator.next();

            selectorItem.addPos(vel.x * dt, 0);
            selectorItem.update(dt);

            /*if(selectorItem.pos.x > END || selectorItem.pos.x + selectorItem.width < START){
                if(selectorItem == head){
                    itemPos++;
                    itemPos = applyItemPosBoundary(itemPos);
                    head = selection.get(1);
                }

                if(selectorItem == tail){
                    itemPos--;
                    itemPos = applyItemPosBoundary(itemPos);
                    tail = selection.get(selection.size() - 2);

                }

                selectorItem.setItem(null);
                selectorItemPool.free(selectorItem);
                iterator.remove();
            }*/

        }

        while(head.pos.x < START){
            addItemPos(1);

            head.setItem(null);
            selectorItemPool.free(head);

            head = selection.get(1);
            selection.remove(0);

            SelectorItem selectorItem = selectorItemPool.newObject();
            selectorItem.pos.set(tail.pos.x + selectorItem.width + ITEM_SPACE, tail.pos.y);
            selection.add(selectorItem);

            int pos = (itemPos + SELECTOR_SIZE - 1) % selectionSize;

            if(pos < 0){
                pos = selectionSize - 1;
            }

            if(metaData.leftBoundary + pos < metaData.rightBoundary){
                selectorItem.setItem(inventory.selectItem(metaData.leftBoundary + pos));
            }

            tail = selectorItem;
        }

        while(tail.pos.x > END){
            addItemPos(-1);

            tail.setItem(null);
            selectorItemPool.free(tail);

            tail = selection.get(selection.size() - 2);
            selection.remove(selection.size() - 1);

            SelectorItem selectorItem = selectorItemPool.newObject();
            selectorItem.pos.set(head.pos.x - selectorItem.width - ITEM_SPACE, head.pos.y);
            selection.add(0, selectorItem);

            if(metaData.leftBoundary + itemPos < metaData.rightBoundary) {
                selectorItem.setItem(inventory.selectItem(metaData.leftBoundary + itemPos));
            }

            head = selectorItem;

        }

        //Log.i("Selector", "itemPos = " + itemPos);

        //TODO When while loop > 1, repeats item for each new selectorItem
        /*int repeat = 1;
        while(head.pos.x > START + ITEM_SIZE + ITEM_SPACE + 1.0f){
            //itemPos--;
            //applyItemPosBoundary();
            int pos = itemPos - repeat;
            pos = applyItemPosBoundary(pos);

            SelectorItem selectorItem = selectorItemPool.newObject();
            selectorItem.pos.set(head.pos.x - selectorItem.width - ITEM_SPACE, head.pos.y);
            selection.add(0, selectorItem);

            if(metaData.leftBoundary + pos < metaData.rightBoundary) {
                selectorItem.setItem(inventory.selectItem(metaData.leftBoundary + pos));
            }

            repeat++;
            Log.i("Selector", "head: repeat = " + repeat + ", itemPos = " + itemPos + ", pos = " + pos);
           // Log.i("Selector", "vel.length = " + vel.length());
            head = selectorItem;

        }

        repeat = 1;
        while(tail.pos.x + tail.width < END - ITEM_SIZE - ITEM_SPACE - 1.0f){
            //itemPos++;
            //applyItemPosBoundary();

            SelectorItem selectorItem = selectorItemPool.newObject();
            selectorItem.pos.set(tail.pos.x + selectorItem.width + ITEM_SPACE, tail.pos.y);
            selection.add(selectorItem);

            int pos = itemPos - 1 - repeat;
            pos = applyItemPosBoundary(pos);

            if(pos < 0){
                pos = selectionSize - 1;
            }

            if(metaData.leftBoundary + pos < metaData.rightBoundary){
                selectorItem.setItem(inventory.selectItem(metaData.leftBoundary + pos));
            }

            Log.i("Selector", "tail: repeat = " + repeat + ", itemPos = " + itemPos + ", pos = " + pos);

            repeat++;
            tail = selectorItem;
        }*/

        lastTouchDrag0.set(touchDrag0);
    }

    public void handleTouchEvent(Input.TouchEvent touchEvent, Camera2D camera2D){
        if(touchEvent.pointerId == 0 && touchEvent.type == Input.TouchEvent.TOUCH_DOWN){
            float x = camera2D.touchX(touchEvent.x);
            float y = camera2D.touchY(touchEvent.y);

            if(y > this.y && y < GarageScreen.WORLD_HEIGHT){
                select = true;
            }
        }

        if(select){
            if(touchEvent.pointerId == 0){
                if(touchEvent.type == Input.TouchEvent.TOUCH_DOWN){
                    finger0 = true;
                    touchPos0.set(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));
                    touchDrag0.set(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));
                    lastTouchDrag0.set(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));

                    for(int i = 0; i < selection.size(); i++){
                        SelectorItem selectorItem = selection.get(i);
                        if(selectorItem.isTouching(touchPos0.x, touchPos0.y)){
                            selectedItem = selectorItem;
                        }
                    }

                    vel.set(0, 0);

                }

                if(touchEvent.type == Input.TouchEvent.TOUCH_UP){
                    //temporary - should only recycle if it has not been placed on the grid

                    selectedItem = null;
                    scrolling = false;
                    itemDrag = false;
                    finger0 = false;
                    select = false;

                    //Log.i("Selector", "Event = touch_up");
                }
            }

            if (touchEvent.type == Input.TouchEvent.TOUCH_DRAGGED) {
                if (touchEvent.pointerId == 0 && finger0) {
                    touchDrag0.set(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));
                }

            }

        }
    }

    public void render(){
        window.render();
        for(int i = 0; i < selection.size(); i++){
            selection.get(i).render();
        }

        textWriter.renderText();
        scrollBar.render();
    }

    private void addItemPos(int val){
        itemPos += val;
        if(itemPos < 0){
            itemPos = selectionSize - 1;
        }

        itemPos %= selectionSize;
    }
}
