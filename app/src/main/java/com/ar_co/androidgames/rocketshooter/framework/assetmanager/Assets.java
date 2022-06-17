package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import android.content.res.AssetManager;
import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.Body;
import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.interfaces.Audio;
import com.ar_co.androidgames.rocketshooter.interfaces.Sound;
import com.ar_co.androidgames.rocketshooter.main.core.Inventory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets {

    private static final String IMAGE_FOLDER = "graphics";
    private static final String AUDIO_FOLDER = "audio";
    private static final String SHADER_FOLDER = "shaders";

    private static Assets assets;
    private boolean loaded;

    private AssetManager assetManager;

    private Map<String, Texture> imageMap;
    private Map<String, Sound> audioMap;
    private Map<String, String> shaders;
    private Map<String, Body> hitBoxes;

    private Texture emptyTexture;

    private AssetLoader assetLoader;

    public static Assets getInstance(GLGame game){
        if(assets == null) {
            assets = new Assets(game);
        }
        return assets;
    }

    private Assets(GLGame game){
        assetManager = game.getAssets();

        imageMap = new HashMap<>();
        audioMap = new HashMap<>();
        shaders = new HashMap<>();
        hitBoxes = new HashMap<>();
    }

    public void loadAllAssets(GLGame game){
        if(loaded){
            return;
        }

        Audio audio = game.getAudio();

        /*try { //Lists all files and directories in "assets".
            String[] arr = assetManager.list("");
            for(String file : arr){
                Log.i("Assets", "File = " + file);
            }
        }catch(IOException | NullPointerException e){
            e.printStackTrace();
        }*/
        assetLoader = new AssetLoader(game){
            @Override
            public void init(){
                loadFolder(AssetLoader.TYPE_SHADER, "shaders", true);
                loadFolder(AssetLoader.TYPE_IMAGE, "graphics", true);
                loadFolder(AssetLoader.TYPE_AUDIO, "audio", true);
                loadFolder(AssetLoader.TYPE_HITBOX, "hitboxes", true);
                commit();
                //loadFolder(AssetLoader.TYPE_HITBOX, "hitboxes", true);
            }
        };

        //String[] dirChildren = getDirChildren(IMAGE_FOLDER);
        List<String> files = new ArrayList<>();
        List<Texture> textures = new ArrayList<>();

        String path;
        int index;
        String fileName;


        files = assetLoader.getFiles(AssetLoader.TYPE_SHADER);
        for(int i = 0; i < files.size(); i++){
            path = files.get(i);
            fileName = path;
            index = fileName.lastIndexOf('.');

            if(index >= 0){
                fileName = path.substring(0, index);
            }

            String text = "";

            try{
                text = getText(path);
            }catch(IOException e){
                e.printStackTrace();
            }

            shaders.put(fileName, text);
        }

        List<List<String>> sections = assetLoader.getFiles(AssetLoader.TYPE_IMAGE);
        //TextureAtlas.createEmptyAtlas(game, (emptyTexture = new Texture()));
        files.clear();
        for(int i = 0; i < sections.size(); i++) {
            files.clear();
            textures.clear();
            files = sections.get(i);
            for (int j = 0; j < files.size(); j++) {
                path = files.get(j);
                fileName = path;
                index = fileName.lastIndexOf('.');

                if (index >= 0) {
                    fileName = path.substring(0, index);
                }
                Texture texture = new Texture(game, path);
                textures.add(texture);
                imageMap.put(fileName, texture);
            }

            if(textures.size() > 0) {
                TextureAtlas. createTextureAtlas(game, textures);
            }
        }

        game.getGLGraphics().loadAtlases();

        /*for(int i = 0; i < game.getGLGraphics().atlases.size(); i++){
            TextureAtlas atlas = game.getGLGraphics().atlases.get(i);

            Log.i("Assets", "Atlas: " + "width = " + atlas.width + ", height = " + atlas.height);
        }*/

        files.clear();

        files = assetLoader.getFiles(AssetLoader.TYPE_AUDIO);

        for(int i = 0; i < files.size(); i++){
            path = files.get(i);
            fileName = path;
            index = fileName.lastIndexOf('.');

            if(index >= 0){
                fileName = path.substring(0, index);
            }
            audioMap.put(fileName, audio.newSound(path));
        }

        files.clear();
        files = assetLoader.getFiles(AssetLoader.TYPE_HITBOX);

        for(int i = 0; i < files.size(); i++){
            path = files.get(i);
            fileName = path;
            index = fileName.lastIndexOf('.');

            if(index >= 0){
                fileName = path.substring(0, index);
            }
            Log.i("Assets", fileName);

            hitBoxes.put(fileName, new Body(game, path));
        }

        Inventory.getInstance(game); //start inventory

        loaded = true;
    }

    public void loadAssets(GLGame game, AssetLoader assetLoader){

    }

    public void recreateContext(GLGame game){
        if(!loaded){
            return;
        }

        /*for(String file : imageMap.keySet()){
            imageMap.get(file).reload();
        }

        for(int i = 0; i < atlasList.size(); i++){
            atlasList.get(i).loadTextures();
        }*/

        game.getGLGraphics().loadAtlases();

    }

    public void unloadAssets(GLGame game){
        if(!loaded){
            return;
        }

        /*for(int i = 0; i < atlasList.size(); i++){
            atlasList.get(i).dispose();
        }*/

        game.getGLGraphics().unloadAtlases();

        imageMap.clear();

        game.getAudio().dispose();
        audioMap.clear();
        shaders.clear();
        hitBoxes.clear();

        imageMap = null;
        audioMap = null;
        shaders = null;
        hitBoxes = null;
        assetManager = null;

        loaded = false;
        assets = null;


        Inventory.getInstance(game).cleanUp();

    }

    public Texture getImage(String file){
        return imageMap.get(file);
    }

    public String getShaderCode(String file){
        return shaders.get(file);
    }

    public Body getHitBox(String file){
        return hitBoxes.get(file);
    }

    public Texture getEmptyTexture(){
        return emptyTexture;
    }

    public void playSound(String file) {
        audioMap.get(file).play(1);
    }


    /*private boolean getAssetFiles(String path, List<String> list){
        String[] arr;
        try{
            arr = assetManager.list(path);
            if(arr.length > 0){
                for(String file : arr){

                    if(!getAssetFiles(path + (!path.equals("") ? "/" : "") + file, list)){
                        Log.i("Assets", "False called at " + (path + (!path.equals("") ? "/" : "") + file));
                        return false;
                    }
                }
            }else{
                //Log.i("Assets", path);
                list.add(path);
            }
        }catch (IOException e){
            return false;
        }
        return true;
    }

    private String[] getDirChildren(String path){
        try{
            String[] files = assetManager.list(path);
            for(int i = 0; i < files.length; i++){
                files[i] = path + "/" +  files[i];
            }
            return files;
        }catch(IOException e){
            return null;
        }
    }*/

    private String getText(String path) throws IOException {
        assetManager.open(path);

        InputStream in = null;

        try {
            in = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Writer writer = new StringWriter();
        char[] buffer = new char[2048];

        try {
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            in.close();
        }

        return writer.toString();

       /* AssetFileDescriptor descriptor = assetManager.openFd(path);
        BufferedReader br = new BufferedReader(new FileReader(descriptor.getFileDescriptor()));

        try{
            StringBuilder text = new StringBuilder();
            String line = br.readLine();

            while(line != null){
                text.append(line);
                text.append("\n");
                line = br.readLine();
            }

            return text.toString();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            br.close();
        }

        return "";
    }*/
    }

}
