package com.ar_co.androidgames.rocketshooter.main.sprites;

import com.ar_co.androidgames.rocketshooter.framework.Camera2D;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.State;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;
import com.ar_co.androidgames.rocketshooter.interfaces.Input;

import java.util.ArrayList;
import java.util.List;

public class Ship extends Sprite{

    private static final float WORLD_SPEED = 500.0f;
    private static final float MIN_DEACC_CAP = 200.0f;
    private static final float USER_MOVEMENT_LIMIT = 1.0f;

    //world screen
    private float angle;
    private float angleDifference;
    private float velAngle;
    private Vector2 touchPos;

    private Vector2 deacceleration;
    private boolean isAccelerating;

    //battle screen
    private Vector2 offset;
    private Vector2 userMovement;

    //state
    private State state;

    private State damaged = new State(){

        @Override
        public void update(float deltaTime){
            invinsibility += deltaTime;

            if(invinsibility >= maxInvinsibility / 4.0f){
                alpha = 1.0f;
            }

            if(invinsibility >= maxInvinsibility){
                invinsibility = 0;

                state = null;
            }
        }
    };

    //engines
    public State engine;

    private State shift = new State(){

        @Override
        public void update(float deltaTime) {
            //Log.i("Ship","Usermovement = " + userMovement.length());

            if (userMovement.length() > 0) {

                acc.set(userMovement.x * battleSpeed, userMovement.y * battleSpeed);
                vel.rotate(userMovement.angle() - vel.angle());
                if (vel.length() < acc.length()){
                    vel.add(acc.x * deltaTime, acc.y * deltaTime);
                }else if(vel.length() > acc.length()){
                    vel.sub(acc.x * deltaTime, acc.y * deltaTime);
                }
            } else {
                float radians = vel.angle() * Vector2.TO_RADIANS;
                float cos = (float) Math.cos(radians);
                float sin = (float) Math.sin(radians);

                deacceleration.set(cos * battleSpeed * deltaTime, sin * battleSpeed * deltaTime);
                if(vel.length() - deacceleration.length() > 0){

                    vel.sub(deacceleration);
                }else{
                    vel.set(0, 0);
                }
            }

            pos.add(vel.x * deltaTime, vel.y * deltaTime);
        }
    };

    private State iframe = new State(){

        @Override
        public void update(float deltaTime) {
            rotationVel = targetAngle / rotationSpeed * ((targetAngle - angle) / targetAngle);
            //rotationTime += deltaTime;
            angleDifference = angle;

            angle += rotationVel * deltaTime;
            angle = targetAngle > 0 ? (float)Math.ceil(angle) : (float)Math.floor(angle);

            angleDifference = angle - angleDifference;

                /*if(rotationTime > midpoint){
                    float ease = dodgeSpeed - battleSpeed;
                    float percentage = dodgeSpeed - (ease - ((ease * ((midpoint - (rotationTime - midpoint)) / midpoint))));
                    Log.i("Ship", "Percentage = " + percentage);
                    dodgeVel.set(dodgeMagnitude.x * percentage, dodgeMagnitude.y * percentage);
                }*/

            float ease = dodgeSpeed - (battleSpeed / 2.0f);
            float percentage = dodgeSpeed - (ease * angle / targetAngle);
            // Log.i("Ship", "Percentage = " + percentage);
            vel.set(dodgeMagnitude.x * percentage, dodgeMagnitude.y * percentage);

            pos.add(vel.x * deltaTime, vel.y * deltaTime);

            rotate(angle);
            //Log.i("Ship", "dodge angle = " + angle + ", dodge vel = " + percentage);
            if(Math.abs(angle) >= 360.0f){
                angleDifference = 0;
                rotate(0);
                angle = 0;
                isIframing = false;
                engine = shift;
                userMovement.set(0, 0);

            }
        }
    };

    //stats
    public float HP;
    public float maxHP;
    private float power;
    private float invinsibility = 0;
    private float maxInvinsibility = 0.5f;

    private float battleSpeed = 300.0f;
    private float dodgeSpeed = 1200.0f;

    private float targetAngle;

    //dodge
    private float rotationVel;
    public float rotationSpeed = 0.15f;
    private Vector2 dodgeMagnitude;

    //private float dodgeDistance;

    public boolean movementEnabled;
    public boolean isIframing;
    public boolean hasIframed;

    //guns
    public List<Gun> guns = new ArrayList<>();


    public Ship(GLGame game, Texture texture) {
        super(game, texture);

        initialize();
    }

    public Ship(GLGame game, Texture texture, float width, float height) {
        super(game, texture, width, height);

        initialize();

    }

    private void initialize(){
        HP = 50.0f;
        maxHP = 50.0f;
        power = 1.0f;
        acc.set(0, 0);
        touchPos = new Vector2(0, 0);
        deacceleration = new Vector2();
        offset = new Vector2(game.getInput().getAccel());
        offset.set(-offset.x, -offset.y);
        userMovement = new Vector2();
        dodgeMagnitude = new Vector2();
        body = Assets.getInstance(game).getHitBox("hitboxes/gameplay/ship");
        //createEngine();
        engine = shift;
    }

    /*public Ship(ShipInfo shipInfo){

    }*/

    public void updateUserMovement(Vector2 movement){
        userMovement.set(movement.x, movement.y);
        if(userMovement.x > USER_MOVEMENT_LIMIT){
            //offset.x += userMovement.x - USER_MOVEMENT_LIMIT;
            //Log.i("Ship", "offset.x = " + offset.x);
            userMovement.x = USER_MOVEMENT_LIMIT;
        }else if(userMovement.x < -USER_MOVEMENT_LIMIT){
            //offset.x += userMovement.x + USER_MOVEMENT_LIMIT;
            userMovement.x = -USER_MOVEMENT_LIMIT;
        }

        if(userMovement.y > USER_MOVEMENT_LIMIT){
            //offset.y += userMovement.y - USER_MOVEMENT_LIMIT;
            //Log.i("Ship", "offset.y = " + offset.y);
            userMovement.y = USER_MOVEMENT_LIMIT;
        }else if(userMovement.y < -USER_MOVEMENT_LIMIT){
            //offset.y += userMovement.y + USER_MOVEMENT_LIMIT;
            userMovement.y = -USER_MOVEMENT_LIMIT;
        }

        //Log.i("Ship", "userMovement: x " + userMovement.x + ", y = " + userMovement.y);
    }

    public void updateWorld(float deltaTime, List<Input.TouchEvent> touchEvents, Camera2D camera2D){
        float len = vel.length();

        for(int i = 0; i < touchEvents.size(); i++){
            Input.TouchEvent touchEvent = touchEvents.get(i);

            touchPos.x = camera2D.touchX(touchEvent.x);
            touchPos.y = camera2D.touchY(touchEvent.y);

            if(touchEvent.pointerId == 0) {
                if (touchEvent.type == Input.TouchEvent.TOUCH_DRAGGED || touchEvent.type == Input.TouchEvent.TOUCH_DOWN) {
                    angle = touchPos.sub(pos.x + (width/2.0f), pos.y + (height/2.0f)).angle();
                    velAngle = vel.angle();

                    float radians = angle * Vector2.TO_RADIANS;
                    float slen = touchPos.length();

                    acc.x = (float) Math.cos(radians) * WORLD_SPEED;
                    acc.y = (float) Math.sin(radians) * WORLD_SPEED;

                    isAccelerating = true;

                    if(velAngle != angle){
                        vel.rotate(angle - velAngle);
                    }

                    if (len < WORLD_SPEED) {
                        vel.add(acc.x * deltaTime, acc.y * deltaTime);
                    } else{
                        vel.set(acc.x, acc.y);
                    }

                    if(len > slen && slen <= MIN_DEACC_CAP) {
                        slen /= MIN_DEACC_CAP;
                        vel.set(acc.x * slen, acc.y * slen);

                    }

                }else {
                    isAccelerating = false;
                }
            }
        }



        if(len > 0 && !isAccelerating) {
            deacceleration.set(-acc.x * deltaTime, -acc.y * deltaTime);
            vel.add(deacceleration);
            if(len - deacceleration.length() < 0){
                vel.set(0, 0);
            }
        }

        pos.add(vel.x * deltaTime, vel.y * deltaTime);
        rotate(angle + 90.0f);

    }

    public void updateBattle(float deltaTime, boolean shoot){
        engine.update(deltaTime);
        if(state != null){
            state.update(deltaTime);
        }

        if(pos.x < 0){
            pos.x = 0;
        }else if(pos.x + width > 1080.0f){
            pos.x = 1080.0f - width;
        }

        if(pos.y < 0){
            pos.y = 0;
        }else if(pos.y + height > 1920.0f){
            pos.y = 1920.0f - height;
        }

        for(int i = 0; i < guns.size(); i++){
            Gun gun = guns.get(i);
            gun.pos.set(pos.x + gun.shipAxisPos.x, pos.y + gun.shipAxisPos.y);
            //gun.barrelAxis.set(pos.x + gun.barrelAxis.x, pos.y + gun.barrelAxis.y);
            gun.shoot(deltaTime, power, true, shoot);
        }
    }

    public void getProjectiles(List<Projectile> projectiles){
        for(int i = 0; i < guns.size(); i++){
            projectiles.addAll(guns.get(i).projectiles);
        }
    }

    public void renderProjectiles(float dt){
        for(int i = 0; i < guns.size(); i++){
            Gun gun = guns.get(i);
            gun.renderProjectiles(dt);
        }
    }

    public void onDamageReceived(float damage){
        if(invinsibility > 0){
            return;
        }

        if(HP > damage) {
            HP -= damage;
        }else{
            HP = 0;
        }
        state = damaged;
        alpha = 0;
    }

    public void dodge(float x, float y){
        isIframing = true;
        hasIframed = true;
        dodgeMagnitude.set(x, y);
        targetAngle = (x > 0 ? 360.0f : -360.0f);
        engine = iframe;
    }

    @Override
    public void rotate(float angle){
        super.rotate(angle);

        for(int i = 0; i < guns.size(); i++){
            Gun gun = guns.get(i);



            gun.pos.sub(pos.x + width / 2.0f, pos.y + height / 2.0f);
            gun.pos.rotate(angleDifference);
            gun.pos.add(pos.x + width / 2.0f, pos.y + height / 2.0f);

            gun.rotateTrajectory(angleDifference);

            //Log.i("Ship", "angle = " + angle + ", angle differnece = " + angleDifference +  ", gun.barrelAxis: x = " + gun.barrelAxis.x + ", y = " + gun.barrelAxis.y);

        }
    }

    @Override
    public void rotate(float x, float y, float angle){
        super.rotate(x, y, angle);

        for(int i = 0; i < guns.size(); i++){
            Gun gun = guns.get(i);

            gun.pos.sub(pos.x + width / 2.0f, pos.y + height / 2.0f);
            gun.pos.rotate(angleDifference);
            gun.pos.add(pos.x + width / 2.0f, pos.y + height / 2.0f);

            gun.rotateTrajectory(angleDifference);
        }
    }


}
