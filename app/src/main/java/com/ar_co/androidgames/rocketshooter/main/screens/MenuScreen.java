package com.ar_co.androidgames.rocketshooter.main.screens;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.FPSCounter;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.State;
import com.ar_co.androidgames.rocketshooter.framework.Screen;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.actions.BlinkAction;
import com.ar_co.androidgames.rocketshooter.framework.actions.Sequence;
import com.ar_co.androidgames.rocketshooter.interfaces.Game;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.sprites.SpriteButton;

import java.util.List;

public class MenuScreen extends Screen{

    private static final float WORLD_WIDTH = 1080.0f;
    private static final float WORLD_HEIGHT = 1920.0f;


    private GLGame g;

    private Camera2D camera2D;
    private FPSCounter fpsCounter;

    private Sprite title;
    private Sprite titleShine;

    private Sprite tapLabel;
    private Sprite tapShine;

    private Sprite copyrightLabel;

    private boolean hitStart;

    private SpriteButton slot1;
    private SpriteButton slot2;
    private SpriteButton slot3;

    private Sprite selectedSlot;

    private Sequence opening;
    private Sequence closing;
    private Sequence showSlots;
    private Sequence selectSlot;
    private BlinkAction tapBlink;

    private State[] states = new State[6];
    private int currPhase = 0;

    private boolean selected;
    private boolean worldLoaded;

    private List<Input.TouchEvent> touchEvents;

    public MenuScreen(Game game){
        super(game);
        this.g = (GLGame) game;
        this.camera2D = new Camera2D(((GLGame) game).getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);
        this.fpsCounter = new FPSCounter();

        title = new Sprite(g, null, 820.0f, 400.0f);
        title.rgb[0] = 0; //remove
        title.rgb[1] = 0; //remove
        title.alpha = 0;
        title.pos.set(130.0f, 370.0f);

        titleShine = new Sprite(g, null, 820.0f, 400.0f);
        titleShine.alpha = 0;
        titleShine.pos.set(130.0f, 370.0f);

        tapLabel = new Sprite(g, null, 450.0f, 120.0f);
        tapLabel.alpha = 0;
        tapLabel.pos.set(315.0f, 950.0f);

        tapShine = new Sprite(g, null, 450.0f, 120.0f);
        tapShine.rgb[0] = 0;
        tapShine.alpha = 0;
        tapShine.pos.set(315.0f, 950.0f);

        copyrightLabel = new Sprite(g, null, 150.0f, 50.0f);
        copyrightLabel.alpha = 0;
        copyrightLabel.pos.set(465.0f, 1850.0f);

        slot1 = new SpriteButton(g, null, 740.f, 254.0f){
            @Override
            public void onClick(){
                setWorld(this);
            }
        };
        slot1.pos.set(WORLD_WIDTH + 740.0f, 385.0f);

        slot2 = new SpriteButton(g, null, 740.f, 254.0f){
            @Override
            public void onClick(){
                setWorld(this);
            }
        };
        slot2.pos.set(WORLD_WIDTH + 740.0f, 800.0f);

        slot3 = new SpriteButton(g, null, 740.f, 254.0f){
            @Override
            public void onClick(){
                setWorld(this);
            }
        };
        slot3.pos.set(WORLD_WIDTH + 740.0f, 1215.0f);

        opening = new Sequence(){
            @Override
            public void init(){
                fade(title, 1.0f, 3.0f, 2.0f);
                fadeEW(titleShine, 1.0f, 1.5f, 4.0f);
                show(tapLabel, 8.5f);
                show(copyrightLabel, 8.5f);
            }
        };

        closing = new Sequence(){
            @Override
            public void init(){
                show(tapShine, 0);
                blink(tapShine, 0.1f, 0.1f, 5, 0);
                hide(tapShine, 1.1f);
                hide(tapLabel, 1.1f);
                hide(copyrightLabel, 1.1f);
                moveFrom(title, -820.0f, 0, 1.5f, 1.1f);
                fade(title, 0, 0.8f, 1.1f);

            }
        };

        showSlots = new Sequence(){
            @Override
            public void init(){
                moveTo(slot1, 170.0f, 385.0f, 1.7f, 0);
                moveTo(slot2, 170.0f, 800.0f, 1.7f, 0.3f);
                moveTo(slot3, 170.0f, 1215.0f, 1.7f, 0.6f);

            }
        };

        tapBlink = new BlinkAction(tapLabel, 0.6f, 1.2f, 0);

        states[0] = new State(){

            @Override
            public void update(float deltaTime) {
                //List<Input.TouchEvent> touchEvents = g.getInput().getTouchEvents();

                opening.update(deltaTime);

                if(touchEvents.size() > 0){
                    if(!opening.isFinished()){
                        opening.skip();
                    }
                }

                if(opening.isFinished()){
                    currPhase++;
                }
            }
        };

        states[1] = new State(){

            @Override
            public void update(float deltaTime) {
                //List<Input.TouchEvent> touchEvents = g.getInput().getTouchEvents();

                tapBlink.steps[tapBlink.step].perform(deltaTime);
                if (tapBlink.finished) {
                    tapBlink.rewind();
                }

                if(touchEvents.size() > 0){
                    tapLabel.alpha = 1;
                    currPhase++;
                }
            }
        };

        states[2] = new State(){

            @Override
            public void update(float deltaTime) {
                closing.update(deltaTime);

                if(closing.isFinished()){
                    currPhase++;
                }
            }
        };

        states[3] = new State(){

            @Override
            public void update(float deltaTime) {
                //touchEvents = g.getInput().getTouchEvents();

                showSlots.update(deltaTime);

                if(showSlots.isFinished()) {
                    slot1.readTouchEvent(touchEvents, camera2D);
                    slot2.readTouchEvent(touchEvents, camera2D);
                    slot3.readTouchEvent(touchEvents, camera2D);
                }

                if(selected){
                    currPhase++;
                }
            }
        };

        states[4] = new State(){

            @Override
            public void update(float deltaTime) {
                selectSlot.update(deltaTime);

                if(selectSlot.isFinished()){
                    g.setScreen(new WorldScreen(g));
                    currPhase++;
                }
            }
        };

        states[5] = new State(){

            @Override
            public void update(float deltaTime) {

            }
        };
    }

    @Override
    public void onStart() {

    }

    @Override
    public void update(float deltaTime) {
        touchEvents = g.getInput().getTouchEvents();

        states[currPhase].update(deltaTime);
        //opening.updateWorld(deltaTime);o

        /*if(touchEvents.size() > 0){
            if(!opening.isFinished()){
                opening.skip();
            }else{
                if(!hitStart)
                tapLabel.alpha = 1;
                hitStart = true;
            }
        }

        if(opening.isFinished()){
            if(!hitStart) {
                tapBlink.steps[tapBlink.step].perform(deltaTime);
                if (tapBlink.complete) {
                    tapBlink.rewind();
                }
            }else{
                closing.updateWorld(deltaTime);
            }
        }

        if(closing.isFinished()){
            //do loading
            //if loaded...
            showSlots.updateWorld(deltaTime);
            slot1.readTouchEvent(touchEvents, camera2D);
            slot2.readTouchEvent(touchEvents, camera2D);
            slot3.readTouchEvent(touchEvents, camera2D);
        }

        if(selected){
            selectSlot.updateWorld(deltaTime);

            if(selectSlot.isFinished()){
                if(!worldLoaded) {
                    worldLoaded = true;
                    g.setScreen(new WorldScreen(g));
                }
            }
        }*/


    }

    @Override
    public void present(float deltaTime) {
      /*  GL11 gl = g.getGLGraphics().getGL();

        gl.glClearColor(0, 0.0784313f, 0.0039215f, 1);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        camera2D.setViewportAndMatrices();

        gl.glEnable(GL11.GL_BLEND);
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL11.GL_TEXTURE_2D);

        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        g.getGLGraphics().startRender();
        title.render();
        titleShine.render();
        tapLabel.render();
        tapShine.render();
        copyrightLabel.render();
        slot1.render();
        slot2.render();
        slot3.render();
        //render here
        g.getGLGraphics().finishRender();

        gl.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL11.GL_COLOR_ARRAY);

        fpsCounter.logFrame();*/
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    private void setWorld(final Sprite slot){
        if(selected){
            return;
        }
        Log.i("MenuScreen", "clicked");
        selected = true;

        selectSlot = new Sequence(){
            @Override
            public void init(){
                blink(slot, 0.1f, 0.1f, 4, 0);
                moveFrom(slot, WORLD_WIDTH - 170.0f, 0, 1.5f, 1.0f);
                fade(slot, 0, 0.8f, 1.0f);
            }
        };

    }
}
