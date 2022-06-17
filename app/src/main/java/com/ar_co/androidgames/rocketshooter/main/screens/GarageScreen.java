package com.ar_co.androidgames.rocketshooter.main.screens;

import static android.opengl.GLES20.*;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.FPSCounter;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Screen;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Shader;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.ShaderProgram;
import com.ar_co.androidgames.rocketshooter.interfaces.Game;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;
import com.ar_co.androidgames.rocketshooter.main.garage.Grid;
import com.ar_co.androidgames.rocketshooter.main.garage.ItemExtractor;
import com.ar_co.androidgames.rocketshooter.main.garage.Menu;
import com.ar_co.androidgames.rocketshooter.main.garage.Selector;
import com.ar_co.androidgames.rocketshooter.main.parts.Part;

import java.util.List;

public class GarageScreen extends Screen implements ItemExtractor {

    public static final float WORLD_WIDTH = 1080.0f;
    public static final float WORLD_HEIGHT = 1920.0f;

    private static final float ZOOM_SENSITIVITY = 20000.0f;

    private Assets assets;

    private Camera2D gridCamera;
    private Camera2D uiCamera;
    private FPSCounter fpsCounter;

    //private Vector2 moveStart0;
    //private Vector2 moveStart1;

    //private Vector2 moveEnd0;
    //private Vector2 moveEnd1;

    /*private Vector2 touchMove0;
    private Vector2 touchMove1;
*/
    private Vector2 itemDrag0;
    //private Vector2 touchDrag1;

    /*
    private boolean finger0;
    private boolean finger1;

    private boolean moveEnabled;*/

    private GLGame g;

    private Menu menu;
    private Grid grid;
    private Selector selector;

    private TextWriter textWriter;

    private Part part;
    private ShaderProgram shaderProgram;

    public GarageScreen(Game game) {
        super(game);
        this.g = (GLGame) game;

        assets = Assets.getInstance(g);

    }

    @Override
    public void onStart() {
        assets.loadAllAssets(g);

        shaderProgram = new ShaderProgram(
                new Shader(assets.getShaderCode("shaders/DefaultVertShader"), GL_VERTEX_SHADER),
                new Shader(assets.getShaderCode("shaders/DefaultFragShader"), GL_FRAGMENT_SHADER)
        );
        shaderProgram.compile();

        this.gridCamera = new Camera2D(g.getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);
        this.uiCamera = new Camera2D(g.getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);
        this.fpsCounter = new FPSCounter();

        textWriter = new TextWriter(g, "graphics/gameplay/debug/charset", 200);

        gridCamera.zoom = 0.1f;

        menu = new Menu(g, 0, 0);
        selector = new Selector(g, this, 0, 1652.0f);
        grid = new Grid(g, this, 5);

        itemDrag0 = new Vector2();
    }

    @Override
    public void update(float deltaTime) {

        List<Input.TouchEvent> touchEvents = g.getInput().getTouchEvents();

        for(int i = 0; i < touchEvents.size(); i++) {
            Input.TouchEvent touchEvent = touchEvents.get(i);

            //Log.i("GarageScreen", "touch.x = " + gridCamera.touchX(touchEvent.x) + ", touch.y = " + gridCamera.touchY(touchEvent.y));

            if(part == null) {
                grid.handleTouchEvent(touchEvent, gridCamera);
            }
            selector.handleTouchEvent(touchEvent, uiCamera);

            if(touchEvent.pointerId == 0){
                if(touchEvent.type == Input.TouchEvent.TOUCH_DRAGGED) {
                    itemDrag0.set(gridCamera.touchX(touchEvent.x), gridCamera.touchY(touchEvent.y));
                }

                if(touchEvent.type == Input.TouchEvent.TOUCH_UP){
                    if(part != null) {
                        grid.placeBlock(part, false);
                        part = null;
                    }
                }
            }

        }

        //textWriter.startWriting();
        menu.update(deltaTime);
        grid.update(deltaTime, gridCamera);

        /*SelectorItem selectedItem = selector.getSelectedItem();
        if(selectedItem != null){
            if(part == null){
                part = (Block)selectedItem.item().extract();
            }
        }else{
            if(part != null){

            }
        }*/

        selector.update(deltaTime);

        if(part != null){
            part.pos.set(itemDrag0.x - part.width / 2.0f, itemDrag0.y - part.height / 2.0f);
            part.rotate(part.pos.x + part.gridInfo.rotationPoint.x, part.pos.y + part.gridInfo.rotationPoint.y, part.gridInfo.rotation * 90.0f);
        }

    }

    @Override
    public void present(float deltaTime) {

        glClearColor(0, 0.0784313f, 0.0039215f, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        float[] gridMVP = gridCamera.setViewportAndMatrices();
        float[] uiMVP = uiCamera.setViewportAndMatrices();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);

        shaderProgram.setMVP(gridMVP);

        g.getGLGraphics().startRender();
        grid.render();
        g.getGLGraphics().finishRender(shaderProgram);

        shaderProgram.setMVP(uiMVP);

        g.getGLGraphics().startRender();
        menu.render();
        selector.render();
        //textWriter.renderText();
        g.getGLGraphics().finishRender(shaderProgram);

        if(part != null){
            shaderProgram.setMVP(gridMVP);
            g.getGLGraphics().startRender();
            part.render();
            g.getGLGraphics().finishRender(shaderProgram);
        }

        fpsCounter.logFrame();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        assets.recreateContext(g);
        shaderProgram.compile();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void attachItem(Part part, float x, float y) {
        this.part = part;
        itemDrag0.set(x, y);
    }

    @Override
    public boolean hasItem(){
        return part != null;
    }
}
