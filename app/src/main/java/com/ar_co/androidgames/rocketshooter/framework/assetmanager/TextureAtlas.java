package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.GLGraphics;
import com.ar_co.androidgames.rocketshooter.framework.SpriteBatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public class TextureAtlas{

    public static final int DEFAULT_TEXTURE_LENGTH = 1000;
    public static final int DEFAULT_PADDING = 2;

    private static final int MIN_FILTER = GL11.GL_NEAREST;
    private static final int MAG_FILTER = GL11.GL_NEAREST;

    private static final Canvas canvas = new Canvas();
    private static final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private static final int MIN_SIZE = 512;
    private static final int MAX_SIZE = 2048;
    private static Rect dst = new Rect();



    private int id = 0;

    public final int padding;
    public int width;
    public int height;

    private List<Texture> textures = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();
    private List<Node> activeNodes = new ArrayList<>();

    private AssetManager assetManager;
    private GLGraphics glGraphics;

    public SpriteBatcher batcher;

    private TextureAtlas(GLGame game, int padding) {
        this.width = MIN_SIZE;
        this.height = MIN_SIZE;
        this.padding = padding;

        assetManager = game.getAssets();
        glGraphics = game.getGLGraphics();


    }

    public static void createTextureAtlas(GLGame game, List<Texture> textures, int length, int padding){
        List<TextureAtlas> atlases = game.getGLGraphics().atlases;
        if(textures == null){
            TextureAtlas emptyAtlas = new TextureAtlas(game, padding);
            emptyAtlas.batcher = new SpriteBatcher(game, DEFAULT_TEXTURE_LENGTH, true, false);
            atlases.add(emptyAtlas);
            return;
        }

        Collections.sort(textures, new Comparator<Texture>(){


            @Override
            public int compare(Texture t1, Texture t2) {
                return (t2.width * t2.height) - (t1.width * t1.height);
            }
        });

        TextureAtlas atlas = new TextureAtlas(game, padding);

        for(int i = 0; i < textures.size(); i++){
            if(!atlas.insertTexture(textures.get(i))){
                if(atlas.expandAtlas()) {
                    i--;
                }else{
                    atlas.batcher = new SpriteBatcher(game, DEFAULT_TEXTURE_LENGTH);
                    atlases.add(atlas);
                    atlas = new TextureAtlas(game, padding);
                }
            }
        }

        atlas.batcher = new SpriteBatcher(game, DEFAULT_TEXTURE_LENGTH);
        atlases.add(atlas);
    }

    public static void createTextureAtlas(GLGame game, List<Texture> textures){
        createTextureAtlas(game, textures, DEFAULT_TEXTURE_LENGTH, DEFAULT_PADDING);
    }

    public static void createEmptyAtlas(GLGame game, Texture link){
        List<TextureAtlas> atlases = game.getGLGraphics().atlases;
        TextureAtlas emptyAtlas = new TextureAtlas(game, 0);
        link.x = 0;
        link.y = 0;
        link.width = 0;
        link.height = 0;
        link.atlas = emptyAtlas;
        emptyAtlas.batcher = new SpriteBatcher(game, DEFAULT_TEXTURE_LENGTH, true, false);
        atlases.add(emptyAtlas);
    }

    public boolean insertTexture(Texture t) {
        Node node;

        if(nodes.isEmpty()) {
            node = new Node(0, 0, width, height);
            nodes.add(node);

            Node[] child = node.insert(t, padding);

            nodes.add(child[0]);
            nodes.add(child[1]);

            activeNodes.add(child[0]);
            activeNodes.add(child[1]);
            t.atlas = this;
            textures.add(t);
            return true;
        }else{
            for(Iterator<Node> iterator = activeNodes.iterator(); iterator.hasNext();) {
                node = iterator.next();
                if(node.isFilled()) {
                    iterator.remove();
                }else {

                    Node[] child = node.insert(t, padding);

                    if(child != null) {
                        nodes.add(child[0]);
                        nodes.add(child[1]);

                        activeNodes.add(child[0]);
                        activeNodes.add(child[1]);
                        t.atlas = this;
                        textures.add(t);
                        return true;
                    }
                }
            }

        }

        return false;
    }

    public void loadTextures(){
        if(textures.isEmpty()){
            return;
        }

        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        id = textureIds[0];

        int furthestX = 0;
        int furthestY = 0;
        for(int i = 0; i < activeNodes.size(); i++){
            Node node = activeNodes.get(i);
            if(node.r.left > furthestX){
                furthestX = node.r.left;
            }
            if(node.r.top > furthestY){
                furthestY = node.r.top;
            }
        }

        width = 2;
        height = 2;

        while(width < furthestX && width < MAX_SIZE) {
            width *= 2;
        }

        while(height < furthestY && height < MAX_SIZE){
            height *= 2;
        }

        Bitmap atlas = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(atlas);

        Bitmap bitmap;
        InputStream in = null;

        for(int i = 0; i < textures.size(); i++){
            Texture t = textures.get(i);
            t.atlas = this;
            try{
                in = assetManager.open(t.file);
                bitmap = BitmapFactory.decodeStream(in);
                dst.set(t.x, t.y, t.x + t.width, t.y + t.height);
                canvas.drawBitmap(bitmap, null, dst, paint);

                bitmap.recycle();
                bitmap = null;
            }catch(IOException e){
                throw new RuntimeException("Couldn't load texture '" + t.file + "'", e);
            }finally{
                if(in != null){
                    try{
                        in.close();
                    }catch(IOException e){

                    }
                }
            }
        }

        GLES20.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, atlas, 0);
        canvas.setBitmap(null);
        atlas.recycle();
        atlas = null;

        GLES20.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, MIN_FILTER);
        GLES20.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, MAG_FILTER);

        GLES20.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void dispose(){
        if(id == 0){
            return;
        }
        GLES20.glBindTexture(GL11.GL_TEXTURE_2D, id);
        int[] textureIds = {id};
        GLES20.glDeleteTextures(1, textureIds, 0);

        assetManager = null;
        glGraphics = null;
    }

    public void bind(){
        GLES20.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    private boolean expandAtlas(){
        if(width >= MAX_SIZE) {
            return false;
        }

        int newSize = width * 2;
        for(int i = 0; i < activeNodes.size(); i++){
            Node node = activeNodes.get(i);
            if(node.r.right == width){
                node.r.right = newSize;
            }

            if(node.r.bottom == height){
                node.r.bottom = newSize;
            }
        }

        width = newSize;
        height = newSize;

        return true;
    }

}