package com.ar_co.androidgames.rocketshooter.main.screens;

import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.FPSCounter;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Screen;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.interfaces.Game;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.sprites.SpriteButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

public class TestScreen extends Screen {

    public static final float WORLD_WIDTH = 1080.0f;
    public static final float WORLD_HEIGHT = 1920.0f;

    private enum GameState{
        Ready,
        Running,
        Pause,
        GameOver
    }

    private GLGame game;
    private Assets assets;
    private Camera2D camera2D;

    private FPSCounter fpsCounter;

    private SpriteButton square;
    private Sprite square1;
    private List<Sprite> sprites = new ArrayList<>();

    private float maxVel = 300.0f;
    float[] accel = new float[2];

    public TestScreen(Game g){
        super(g);
        game = (GLGame) g;
    }

    @Override
    public void onStart(){
        assets = Assets.getInstance(game);
        camera2D = new Camera2D(game.getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);

        fpsCounter = new FPSCounter(){
            @Override
            public void logMsg(){
                Vector2 buffer = game.getInput().getAccel();
                float[] accel = new float[]{buffer.x, buffer.y};
                Log.i("Accelerometer", "x = " + accel[0] + ", y = " + accel[1]);
            }
        };

        /*
        galaxy = new Galaxy(game, WORLD_WIDTH, WORLD_HEIGHT, true);

        scoreSystem = ScoreSystem.getInstance();

        gameplay = new Gameplay(game, WORLD_WIDTH, WORLD_HEIGHT);
        introGameplay = new IntroGameplay(game, camera2D, gameplay.getBall(), 0, 328f, WORLD_WIDTH, 1794f, gameplay);

        //obstacleHandler = new ObstacleHandler(game, gameplay);
        scoreboard = new Scoreboard(game, camera2D);


        gamebar = new Gamebar(game, gameplay);
        topBoundary = new Boundary(game, 0, 0, 1080, 250, true);
        bottomBoundary = new Boundary(game, 0, 1815, 1080, 105, false);
        gameState = GameState.Ready;*/

        final Random r = new Random();

        square = new SpriteButton(game, null, 200, 200){
            @Override
            public void onClick(){
                square.rgb[0] = r.nextFloat();
                square.rgb[1] = r.nextFloat();
                square.rgb[2] = r.nextFloat();
            }
        };
        square.pos.x = 496.0f;
        square.pos.y = 916.0f;
    }

    @Override
    public void update(float deltaTime){
       /* List<Input.TouchEvent> touchEvents = getGame().getInput().getTouchEvents();

        square.readTouchEvent(touchEvents, camera2D);

        //square.rotate(angle += 10.0f * deltaTime);

        /*List<Input.TouchEvent> touchEvents = getGame().getInput().getTouchEvents();
        galaxy.updateWorld(deltaTime);

        if(gameState == GameState.Ready){
            introGameplay.updateWorld(deltaTime, touchEvents);
            if(introGameplay.isFinished()){
                gameState = GameState.Running;
                gameplay.started = true;
            }
        }else if(gameState == GameState.Running) {
            updateRunning(touchEvents, deltaTime);
        }else if(gameState == GameState.GameOver){
            scoreboard.updateWorld(deltaTime, touchEvents);
        }
        //obstacleHandler.updateWorld(deltaTime);
        gamebar.updateWorld(deltaTime);
        topBoundary.updateWorld(deltaTime);
        bottomBoundary.updateWorld(deltaTime);*/
    }

    @Override
    public void present(float deltaTime) {
      /*  GL11 gl = game.getGLGraphics().getGL();

        gl.glClearColor(0, 0.0784313f, 0.0039215f, 1);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        camera2D.setViewportAndMatrices();

        gl.glEnable(GL11.GL_BLEND);
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL11.GL_TEXTURE_2D);

        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        game.getGLGraphics().startRender();
        square.render();
        //..square1.render();

        /*for(int i = 0; i < sprites.size(); i++){
            Sprite sprite = sprites.get(i);

            sprite.render();
        }*/
       /* game.getGLGraphics().finishRender();

        gl.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL11.GL_COLOR_ARRAY);

        fpsCounter.logFrame();
        */
    }

    @Override
    public boolean onBackPressed(){
        /*synchronized(this){
            if (gameState == GameState.GameOver) {
                game.transitionScreen(this, new MenuScreen(game), new float[]{0, 0.07843137254f, 0.00392156862f});
            } else if (gameState == GameState.Running) {
                gameplay.die();
            }
            return true;
        }*/
        return super.onBackPressed();
    }

    @Override
    public void pause(){
        /*if(gameState == GameState.Running){
            gameState = GameState.Pause;
        }*/
    }

    @Override
    public void resume(){
        /*if(gameState == GameState.Pause){
            gameState = GameState.Running;
        }*/
    }

    @Override
    public void dispose(){
        //scoreSystem.dispose();
    }
}
