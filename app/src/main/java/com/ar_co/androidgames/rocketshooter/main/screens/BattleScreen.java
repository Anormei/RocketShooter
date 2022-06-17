package com.ar_co.androidgames.rocketshooter.main.screens;

import static android.opengl.GLES20.*;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.FPSCounter;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Screen;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Shader;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.ShaderProgram;
import com.ar_co.androidgames.rocketshooter.interfaces.Game;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;
import com.ar_co.androidgames.rocketshooter.main.backgrounds.Galaxy;
import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;
import com.ar_co.androidgames.rocketshooter.main.enemy.Enemy;
import com.ar_co.androidgames.rocketshooter.main.enemy.LargeObstacle;
import com.ar_co.androidgames.rocketshooter.main.gameplay.GameHandler;
import com.ar_co.androidgames.rocketshooter.main.gameplay.Mission;
import com.ar_co.androidgames.rocketshooter.main.sprites.Blaster;
import com.ar_co.androidgames.rocketshooter.main.sprites.Projectile;
import com.ar_co.androidgames.rocketshooter.main.sprites.Ship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BattleScreen extends Screen implements GameHandler {

    public static final float WORLD_WIDTH = 1080.0f;
    public static final float WORLD_HEIGHT = 1920.0f;

    private static final float SWIPE_TIME_THRESHOLD = 0.15f;
    private static final float SWIPE_DISTANCE_THRESHOLD = 200.0f;

    private GLGame g;
    private Assets assets;
    private Camera2D camera2D;
    private FPSCounter fpsCounter;

    private Galaxy galaxy;
    private Ship ship;
    private Sprite statusMenu;
    private Sprite hpBar;
    private Sprite box;
    private LargeObstacle largeObstacle;

    private TextWriter textWriter;

    private Sprite stickCenter;
    private Sprite controlStick;

    private Mission mission;

    private Vector2 touchPos0;
    private Vector2 touchPos1;
    private Vector2 touchMath;
    private Vector2 userMovement;

    private boolean touchingScreen;
    private boolean movementEnabled;
    private boolean autofire;


    private float touchSensitivity = 100.0f;

    private float swipeTime = 0;

    private float time = 0;
    private boolean yea;

    //half of screen-height

    //projectile handling
    private List<Projectile> projectiles = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private boolean touchControls = true;

    private float galaxyHalf;
    private float galaxySize;

    private ShaderProgram shaderProgram;
    //temp

    public BattleScreen(Game game, Mission mission){
        super(game);
        this.g = (GLGame) game;
        this.assets = Assets.getInstance(g);
        this.mission = mission;
        mission.setGameHandler(this);

    }

    @Override
    public void onStart() {
        assets.loadAllAssets(g);

        shaderProgram = new ShaderProgram(
                new Shader(assets.getShaderCode("shaders/DefaultVertShader"), GL_VERTEX_SHADER),
                new Shader(assets.getShaderCode("shaders/DefaultFragShader"), GL_FRAGMENT_SHADER)
        );

        shaderProgram.compile();

        mission.init();
        this.camera2D = new Camera2D(g.getGLGraphics(), WORLD_WIDTH, WORLD_HEIGHT);
        this.fpsCounter = new FPSCounter();

        //galaxy = Galaxy.createGalaxy(g, -half, -100.0f, WORLD_WIDTH + half * 2.0f, WORLD_HEIGHT + 100.0f);
        //galaxy.configElements(1000.0f, 4.0f, 100, 200);

        galaxySize = (float)Math.sqrt(WORLD_WIDTH * WORLD_WIDTH + WORLD_HEIGHT * WORLD_HEIGHT); //2202

        galaxyHalf = galaxySize / 2.0f;

        galaxy = new Galaxy(g, WORLD_WIDTH / 2.0f - galaxyHalf, WORLD_HEIGHT / 2.0f - galaxyHalf, galaxySize, galaxySize, 1000.0f);

        ship = new Ship(g, assets.getImage("graphics/gameplay/ship"), 100.0f, 68.0f);
        ship.pos.set(500, 500);
        //box = new Sprite(g, assets.getImage("graphics/gameplay/NoTexture"), 100.0f, 68.0f);
        //box.pos.set(500, 500);
        //box.alpha = 0.5f;

        statusMenu = new Sprite(g, assets.getImage("graphics/gameplay/status-menu"), 4.0f);
        statusMenu.pos.set(0, 0);

        hpBar = new Sprite(g, assets.getImage("graphics/gameplay/NoTexture"), 516.0f,20.0f);
        hpBar.pos.set(108.0f, 68.0f);

        /*largeObstacle = new LargeObstacle(g);
        largeObstacle.pos.set(500, 200);
        enemies.add(largeObstacle);*/

        textWriter = new TextWriter(g, "graphics/gameplay/debug/charset", 100);

        stickCenter = new Sprite(g, assets.getImage("graphics/gameplay/NoTexture"),4.0f, 4.0f);
        controlStick = new Sprite(g, assets.getImage("graphics/gameplay/controlstick"), 4.0f);

        //temp (placing guns)
        Blaster b1 = new Blaster(g, null, camera2D);
        b1.shipAxisPos.set(48.0f, 0);
        //b1.barrelAxis.set(20.0f, 44.0f);

        //b2.barrelAxis.set(75.0f, 44.0f);

        ship.guns.add(b1);
        //end temp

        touchPos0 = new Vector2();
        touchPos1 = new Vector2();
        userMovement = new Vector2();
        touchMath = new Vector2();

    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = g.getInput().getTouchEvents();


        galaxy.update(deltaTime);

        ship.updateUserMovement(userMovement);

        if(!touchControls){
            Vector2 accel = g.getInput().getAccel();
            userMovement.set(-accel.x, -accel.y);
        }

        for(int i = 0; i < touchEvents.size(); i++){
            Input.TouchEvent touchEvent = touchEvents.get(i);

            //Log.i("BattleScreen", "index = " + i + ", id[" + touchEvent.pointerId + "], TouchEvent = " + touchEvent.type);

            if(touchEvent.pointerId == 0) {

                if(touchEvent.type != Input.TouchEvent.TOUCH_UP){


                    if(!touchControls) {
                        autofire = true;
                    }
                }else{
                    swipeTime = 0;
                    touchingScreen = false;
                    movementEnabled = false;
                    if(!touchControls) {
                        autofire = false;
                    }
                    ship.hasIframed = false;
                    userMovement.set(0, 0);
                }

                if(touchEvent.type == Input.TouchEvent.TOUCH_DOWN){
                    touchPos0.set(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));
                    stickCenter.pos.set(touchPos0.x - stickCenter.width / 2.0f, touchPos0.y - stickCenter.height / 2.0f);
                    controlStick.pos.set(touchPos0.x - controlStick.width / 2.0f, touchPos0.y - controlStick.height / 2.0f);
                    movementEnabled = true;
                    touchingScreen = true;
                    //if touching enemy then target rockets
                    //if touching player then launch rockets

                }else if(touchEvent.type == Input.TouchEvent.TOUCH_DRAGGED && movementEnabled){
                    touchPos1.set(camera2D.touchX(touchEvent.x), camera2D.touchY(touchEvent.y));

                    //Dodging mechanic
                    if(swipeTime < SWIPE_TIME_THRESHOLD){
                        if(!ship.isIframing && !ship.hasIframed && touchPos1.dist(touchPos0) > SWIPE_DISTANCE_THRESHOLD){ //and if ship not already in iframe
                            //ship.do iframe
                            float radians = touchMath.set(touchPos1).sub(touchPos0).angle() * Vector2.TO_RADIANS;
                            ship.dodge((float)Math.cos(radians), (float)Math.sin(radians));

                        }
                    }

                    if(touchControls){
                        userMovement.set(touchPos1).sub(touchPos0);

                        if(touchPos1.dist(touchPos0) > touchSensitivity) {
                            float radians = userMovement.angle() * Vector2.TO_RADIANS;
                            userMovement.set((float) Math.cos(radians) * touchSensitivity, (float) Math.sin(radians) * touchSensitivity);
                        }

                        controlStick.pos.set(touchPos0.x + userMovement.x - (controlStick.width / 2.0f), touchPos0.y + userMovement.y - (controlStick.height / 2.0f));
                        ship.updateUserMovement(userMovement.div(touchSensitivity));
                        //Log.i("BattleScreen", "userMovement.x = " + userMovement.x + ", userMovement.y = " + userMovement.y);
                    }
                }
            }

            if(touchEvent.pointerId == 1 && touchControls){
                    autofire = touchEvent.type != Input.TouchEvent.TOUCH_UP;
            }
        }

        if(touchingScreen && !ship.isIframing){
            swipeTime += deltaTime;

        }

        projectiles.clear();

        ship.updateBattle(deltaTime, autofire && !ship.isIframing);
        ship.getProjectiles(projectiles);
       // box.pos.set(ship.pos);

        for(Iterator<Enemy> enemyIterator = enemies.iterator(); enemyIterator.hasNext();) {
            Enemy enemy = enemyIterator.next();
            enemy.update(deltaTime);

            if(ship.isTouching(enemy)){
                enemy.onCollision(ship);
            }

            for(Iterator<Projectile> iterator = projectiles.iterator(); iterator.hasNext();){
                Projectile projectile = iterator.next();
                if (enemy.isTouching(projectile) && !enemy.dead && projectile.friendly) {
                    projectile.delete = true;
                    iterator.remove();
                    enemy.onHit(projectile);
                    //Log.i("BattleScreen", "LargeObstacle hit!");
                }
            }

            if(enemy.dead || enemy.pos.y > WORLD_HEIGHT){
                mission.despawnEnemy(enemy);
                enemyIterator.remove();
            }
        }

        /*if(ship.isTouching(largeObstacle)){
            ship.onDamageReceived(13.0f);
            //Log.i("BattleScreen", "Ship has been hit!");
        }*/

        mission.update(deltaTime);

        hpBar.width = (ship.HP / ship.maxHP) * 516.0f;

        //textWriter.startWriting(8.0f, 8.0f, 24.0f);
        //textWriter.writeText(30.0f, 50.0f, "HP: " + (int)ship.HP + "/" + (int)ship.maxHP);

        //for galaxy rotation (remove later)
        time += deltaTime;
        if(time >= 5.0f){
            time = 0;
            galaxy.rotate(somerandom(), 0.2f);
        }
    }

    //remove later
    Random r = new Random();
    private float somerandom(){
        return (r.nextFloat() * 90.0f) -45.0f;
    }

    @Override
    public void present(float deltaTime) {

        glClearColor(0, 0.0784313f, 0.0039215f, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        float[] mvp = camera2D.setViewportAndMatrices();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        //GLES20.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        // GLES20.glEnableClientState(GL11.GL_COLOR_ARRAY);
        // GLES20.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        shaderProgram.setMVP(mvp);

        g.getGLGraphics().startRender();

        galaxy.draw(deltaTime, WORLD_WIDTH / 2.0f - galaxyHalf, WORLD_HEIGHT / 2.0f - galaxyHalf, WORLD_WIDTH / 2.0f + galaxyHalf, WORLD_HEIGHT / 2.0f + galaxyHalf);
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).render();
        }
        //box.render();
        ship.renderProjectiles(deltaTime);
        ship.render();
        if(touchingScreen && touchControls){
            stickCenter.render();
            controlStick.render();
        }
        statusMenu.render();
        hpBar.render();

        g.getGLGraphics().finishRender(shaderProgram);

        //  GLES20.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        //  GLES20.glDisableClientState(GL11.GL_COLOR_ARRAY);
        //GLES20.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

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
    public void spawnEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    @Override
    public boolean enemiesPresent() {
        return enemies.size() > 0;
    }

    @Override
    public void setMovementDirection(float angle) {

    }
}
