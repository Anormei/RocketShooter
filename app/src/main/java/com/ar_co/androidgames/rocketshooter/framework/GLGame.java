package com.ar_co.androidgames.rocketshooter.framework;

import android.opengl.GLSurfaceView;

import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.interfaces.Game;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class GLGame extends GameActivity implements Game, GLSurfaceView.Renderer{

    enum GLState{
        Initialized,
        Running,
        Paused,
        Finished,
        Idle
    }

    interface DisposalUnit{
        void dispose();
    }

    protected GLSurfaceView glView;
    private List<DisposalUnit> disposalUnits = new ArrayList<>();

    private Assets assets;
    private GLState state = GLState.Initialized;
    private final Object stateChanged = new Object();
    private long startTime = System.nanoTime();

    /*@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        GLRenderer renderer = new GLRenderer(this);
        stateHandler = renderer;

        glView = new GLSurfaceView(this);
        glView.setRenderer(renderer);
        addView(glView);
    }*/

    @Override
    public void setupGame(){
        assets = Assets.getInstance(this);

        glView = new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setEGLConfigChooser(8, 8, 8, 8, 0, 8);

        glView.setRenderer(this);
        addView(glView);
    }


    @Override
    public void onResume(){
        super.onResume();
        glView.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();
        synchronized(stateChanged){
            if(isFinishing()){
                state = GLState.Finished;
            }else{
                state = GLState.Paused;
            }

            while(true){
                try {
                    stateChanged.wait();
                    break;
                }catch(InterruptedException e){

                }
            }
        }
        glView.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(isFinishing()) {
            for(int i = 0; i < disposalUnits.size(); i++){
                disposalUnits.get(i).dispose();
            }
            disposalUnits.clear();
            disposalUnits = null;
            Assets.getInstance(this).unloadAssets(this);
            unloadGame();
        }
    }

    public void addDisposalUnit(DisposalUnit disposalUnit){
        if(disposalUnits.contains(disposalUnit)){
            return;
        }
        disposalUnits.add(disposalUnit);
    }

    public abstract GLGraphics getGLGraphics();

    //render vvvvv


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config){

        synchronized(stateChanged){
            if(state == GLState.Initialized){
                getStartScreen();
                loadGame();
                //TODO load inventory
            }else{
                getCurrentScreen().resume();
            }
            state = GLState.Running;
            startTime = System.nanoTime();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height){

    }

    @Override
    public void onDrawFrame(GL10 gl){
        GLState state = null;

        synchronized(stateChanged){
            state = this.state;
        }

        if(state == GLState.Running){
            float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
            startTime = System.nanoTime();

            getCurrentScreen().update(deltaTime);
            getCurrentScreen().present(deltaTime);
        }

        if(state == GLState.Paused){
            getCurrentScreen().pause();
            synchronized(stateChanged){
                this.state = GLState.Idle;
                stateChanged.notifyAll();
            }
        }

        if(state == GLState.Finished){
            getCurrentScreen().pause();
            getCurrentScreen().dispose();
            synchronized(stateChanged){
                this.state = GLState.Idle;
                stateChanged.notifyAll();
            }
        }
    }

    public abstract void loadGame();

    public abstract void unloadGame();
}
