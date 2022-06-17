package com.ar_co.androidgames.rocketshooter.main.screens;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.FPSCounter;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.State;
import com.ar_co.androidgames.rocketshooter.framework.Screen;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.interfaces.Game;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.backgrounds.Galaxy;
import com.ar_co.androidgames.rocketshooter.main.interfaces.PhaseHandler;
import com.ar_co.androidgames.rocketshooter.main.sprites.Ship;
import com.ar_co.androidgames.rocketshooter.main.sprites.World;

import java.util.ArrayList;
import java.util.List;

public class WorldScreen extends Screen implements PhaseHandler{

    private static final float WORLD_WIDTH = 1080.0f;
    private static final float WORLD_HEIGHT = 1920.0f;

    private static final float MAP_SIZE = 6420.0f;

    private static final float CAM_WIDTH_THRESHOLD = 200.0f;
    private static final float CAM_HEIGHT_THRESHOLD = 300.0f;

    private static final float WORLD_DIST_PROX = 300.0f;

    private GLGame g;
    private Assets assets;
    private Camera2D camera2D;
    private FPSCounter fpsCounter;

    private Galaxy galaxy;
    private List<World> worlds = new ArrayList<>();
    private World nearestWorld;
    private Ship ship;

    private State worldSelected;
    private boolean finished;

    public WorldScreen(Game game){
        super(game);
        this.g = (GLGame) game;
        this.assets = Assets.getInstance(g);
        this.camera2D = new Camera2D(((GLGame) game).getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);
        this.fpsCounter = new FPSCounter();

       //galaxy = Galaxy.createGalaxy(g, 0, 0, MAP_SIZE, MAP_SIZE);
        //galaxy.configElements(0, 4.0f, 6000, 10000);

        World world = new World(g, null, 200.0f, 200.0f);
        world.pos.set(3420, 3420);
        worlds.add(world);

        ship = new Ship(g, assets.getImage("graphics/gameplay/ship"), 120.0f, 92.0f);
        ship.pos.set(500, 500);

    }

    @Override
    public void onStart() {

    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = g.getInput().getTouchEvents();

        ship.updateWorld(deltaTime, touchEvents, camera2D);
        /*if(ship.pos.x - camera2D.getLeftFrustum() < CAM_WIDTH_THRESHOLD ||
                camera2D.getRightFrustum() - (ship.pos.x + ship.width) < CAM_WIDTH_THRESHOLD ||
                ship.pos.y - camera2D.getTopFrustum() < CAM_WIDTH_THRESHOLD ||
                camera2D.getBottomFrustum() - (ship.pos.y + ship.height) < CAM_WIDTH_THRESHOLD){


        }*/

        if(ship.pos.x - camera2D.getLeftFrustum() < CAM_WIDTH_THRESHOLD){
            camera2D.x += ship.pos.x - camera2D.getLeftFrustum() - CAM_WIDTH_THRESHOLD;
        }else if(camera2D.getRightFrustum() - (ship.pos.x + ship.width) < CAM_WIDTH_THRESHOLD){
            camera2D.x += CAM_WIDTH_THRESHOLD - (camera2D.getRightFrustum() - (ship.pos.x + ship.width));
        }

        if(ship.pos.y - camera2D.getTopFrustum() < CAM_HEIGHT_THRESHOLD){
            camera2D.y += ship.pos.y - camera2D.getTopFrustum() - CAM_HEIGHT_THRESHOLD;
        }else if(camera2D.getBottomFrustum() - (ship.pos.y + ship.height) < CAM_HEIGHT_THRESHOLD){
            camera2D.y += CAM_HEIGHT_THRESHOLD - (camera2D.getBottomFrustum() - (ship.pos.y + ship.height));
        }

        if(camera2D.x - camera2D.frustumWidth / 2.0f < 0){
            //Log.i("WorldScreen", "Camera2D.x = " + (camera2D.x - camera2D.frustumWidth / 2.0f));
            camera2D.x = camera2D.frustumWidth / 2.0f;
        }

        if(camera2D.x + camera2D.frustumWidth / 2.0f > MAP_SIZE){
            camera2D.x = MAP_SIZE - camera2D.frustumWidth / 2.0f;
        }

        if(camera2D.y - camera2D.frustumHeight / 2.0f < 0){
            camera2D.y = camera2D.frustumHeight / 2.0f;
        }

        if(camera2D.y + camera2D.frustumHeight / 2.0f > MAP_SIZE){
            camera2D.y = MAP_SIZE - camera2D.frustumHeight / 2.0f;
        }

        float dist = WORLD_DIST_PROX;
        nearestWorld = null;

        for(int i = 0; i < worlds.size(); i++){
            World world = worlds.get(i);
            if(world.pos.dist(ship.pos) < dist){
                nearestWorld = world;
            }
        }

        if(nearestWorld != null){
            nearestWorld.readTouchEvent(touchEvents, camera2D);
            if(nearestWorld.clicked && !finished){
                finished = true;
                g.setScreen(new BattleScreen(g, null));
                //new gameplayscreen
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        /*GL11 gl = g.getGLGraphics().getGL();

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
        galaxy.draw(deltaTime, camera2D.getLeftFrustum(), camera2D.getTopFrustum(), camera2D.getRightFrustum(), camera2D.getBottomFrustum());
        for(int i = 0; i < worlds.size(); i++){
            worlds.get(i).render();
        }
        ship.render();
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

    @Override
    public void handlePhase(State state) {

    }
}
