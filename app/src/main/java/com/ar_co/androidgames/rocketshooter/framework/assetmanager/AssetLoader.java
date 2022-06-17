package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import android.content.res.AssetManager;
import android.util.Log;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AssetLoader {

    public static final int TYPE_SHADER = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_HITBOX = 3;
    public static final int TYPE_GAMEDATA = 4;

    private List<String> shaders;
    private List<String> imageSection;
    private List<String> audio;

    private List<String> hitBoxes;
    private List<String> gameData;
    private List<String>[] files;

    private List<List<String>> images;

    private AssetManager assetManager;

    public AssetLoader(GLGame game){
        assetManager = game.getAssets();

        shaders = new ArrayList<>();
        imageSection = new ArrayList<>();
        audio = new ArrayList<>();
        hitBoxes= new ArrayList<>();
        gameData= new ArrayList<>();

        files = new List[]{shaders, imageSection, audio, hitBoxes, gameData};
        images = new ArrayList<>();
        init();
    }

    public abstract void init();

    public void loadFolder(int type, String path, boolean readSubFolders){
        getAssetFiles(path, files[type], readSubFolders);
    }

    public void newImageSection(){
        images.add(imageSection);
        imageSection = new ArrayList<>();
        files[1] = imageSection;
    }

    public void commit(){
        images.add(imageSection);
    }

    public List getFiles(int type){
        if(type == TYPE_IMAGE){
            return images;
        }
        return files[type];
    }
    private boolean getAssetFiles(String path, List<String> list, boolean readSubFolders){
        String[] arr;

        //Log.i("AssetLoader", "Start loading folder");
        try{
            arr = assetManager.list(path); //Find and add paths to string array
            if(arr.length > 0){ //If directory...
                for(String file : arr){ //Check through each file of the directory
                    String filePath = path + (!path.equals("") ? "/" : "") + file;

                    //Look at other files/directories
                    if(readSubFolders && !getAssetFiles(filePath, list, true)){ //If an exception returns... return false (and recursion returns false).
                        return false;
                    }else if(!readSubFolders){
                        if(assetManager.list(filePath).length == 0){
                            list.add(filePath);
                        }
                    }
                }
            }else{ //Otherwise it is a file, and add the file to the list
                //Log.i("AssetLoader", path);
                list.add(path);
            }
        }catch (IOException e){
            return false;
        }
        return true; //Success
    }

    //Gets child directories of parent directory (//TODO Does not discriminate files)
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
    }

}
