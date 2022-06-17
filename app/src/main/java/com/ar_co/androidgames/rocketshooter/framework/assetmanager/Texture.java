package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;

import com.ar_co.androidgames.rocketshooter.framework.HitBox;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;

import java.io.IOException;
import java.io.InputStream;

public class Texture {


    private static BitmapFactory.Options options = new BitmapFactory.Options();

    public TextureAtlas atlas;
    public String file;

    public int x;
    public int y;
    public int width;
    public int height;

    private AssetManager assets;

    private HitBox body;

    static{
        options.inJustDecodeBounds = true;
    }

    public Texture(){

    }

    public Texture(GLGame game, String file){
        assets = game.getAssets();
        this.file = file;

        InputStream in = null;
        try{
            in = assets.open(file);
            BitmapFactory.decodeStream(in, null, options);
            width = options.outWidth;
            height = options.outHeight;
        }catch(IOException e){

        }finally{
            if(in != null){
                try{
                    in.close();
                }catch(IOException e){

                }
            }
        }
    }


    /*public void bind(){
        GL11 gl = glGraphics.getGL11();
        gl.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public void dispose(){
        GL11 gl = glGraphics.getGL11();
        gl.glBindTexture(GL11.GL_TEXTURE_2D, id);
        int[] textureIds = {id};
        gl.glDeleteTextures(1, textureIds, 0);

        assets = null;
        hitBox = null;
        glGraphics = null;
    }*/


}
