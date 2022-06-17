package com.ar_co.androidgames.rocketshooter.main.core;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.SparseArray;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataconstructor.DataStructure;

public class Inventory {

    public static final String PARTS = "parts";

    private static final String INVENTORY_FOLDER = "core/items";

    private static Inventory inventory;

    private SparseArray<Item> items = new SparseArray<>();
    private AssetManager assetManager;
    private Map<String, ItemMetaData> itemMetaDatabase;

    private Inventory(final GLGame game){
        /*Item[] arr = new Item[]{
                new Item<Part>(game, Block_Level1.ID, "graphics/gameplay/parts/square_a"){
                    @Override
                    public Part extract(){
                        return new Block_Level1(game);
                    }
                }
        };
*/
        assetManager = game.getAssets();
        List<String> paths = new ArrayList<>();
        getAssetFiles("core/items", paths);
        itemMetaDatabase = new HashMap<>();


        String[] l = null;
        try {
            l = assetManager.list("core/items/parts");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < l.length; i++){
            Log.i("Inventory", "Paths = " + l[i]);
        }

        for(int i = 0; i < paths.size(); i++){
            Item item = null;
            //Log.i("Inventory", "index: " + i + ", path = " + paths.get(i));
            try{
                InputStream inputStream = assetManager.open(paths.get(i));
                ObjectInputStream in = new ObjectInputStream(inputStream);
                item = new Item(game, (DataStructure)in.readObject());
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }

            items.append(item.ID, item);
            //item.index = items.indexOfKey(item.ID);
        }

        DataStructure metadata = null;

        try{
            InputStream inputStream = assetManager.open("core/metadata/InventoryMetaData.data");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            metadata = (DataStructure)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < metadata.size(); i++){
            ItemMetaData itemMetaData = new ItemMetaData();
            itemMetaData.readDataStructure((DataStructure)metadata.values[i]);
            itemMetaDatabase.put(metadata.names[i], itemMetaData);
        }
    }

    public static Inventory getInstance(GLGame game){
        if(inventory == null){
            inventory = new Inventory(game);
        }
        return inventory;
    }

    /*public ItemData getItemByIndex(int index){
        return items.valueAt(index);
    }*/

    public Item selectItem(int ID){

        return items.get(ID);
    }

    /*public int getItemIndex(int ID){
        return items.indexOfKey((ID));
    }*/

    public int size(){
        return items.size();
    }

    public void cleanUp(){
        inventory = null;
    }

    private boolean getAssetFiles(String path, List<String> list){
        String[] arr;

        //Log.i("AssetLoader", "Start loading folder");
        try{
            arr = assetManager.list(path); //Find and add paths to string array
            if(arr.length > 0){ //If directory...
                for(String file : arr){ //Check through each file of the directory
                    String filePath = path + (!path.equals("") ? "/" : "") + file;
                    //Log.i("Inventory", "filePath in getAssetFiles() = " + filePath);
                    //Look at other files/directories
                    if(!getAssetFiles(filePath, list)){ //If an exception returns... return false (and recursion returns false).
                        //Log.i("Inventory", "Yep return false");
                        return false;
                    }
                }
            }else{ //Otherwise it is a file, and add the file to the list
                //Log.i("AssetLoader", path);

                list.add(path);
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true; //Success
    }

    public ItemMetaData getMetaData(String name){
        return itemMetaDatabase.get(name);
    }
}
