package com.ar_co.androidgames.rocketshooter.framework;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.ar_co.androidgames.rocketshooter.framework.assetmanager.ShaderProgram;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.TextureAtlas;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class GLGraphics {

    private GLSurfaceView glView;

    public List<TextureAtlas> atlases = new ArrayList<>();

    public GLGraphics(GLSurfaceView glView){
        this.glView = glView;
    }

    public int getWidth(){
        return glView.getWidth();
    }

    public int getHeight(){
        return glView.getHeight();
    }

    public void loadAtlases(){
        for(TextureAtlas atlas : atlases){
            atlas.loadTextures();
        }
    }

    public void unloadAtlases(){
        for(TextureAtlas atlas : atlases){
            atlas.dispose();
        }
    }

    public void startRender(){
        for (TextureAtlas atlas : atlases) {
            atlas.batcher.startBatch();
        }
    }

    public void finishRender(ShaderProgram shaderProgram){
        for(TextureAtlas atlas : atlases){
            atlas.batcher.endBatch(atlas, shaderProgram);
        }
    }

}
