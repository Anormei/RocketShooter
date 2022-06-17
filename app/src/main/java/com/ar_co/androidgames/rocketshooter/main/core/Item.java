package com.ar_co.androidgames.rocketshooter.main.core;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dataconstructor.DataStructure;
import dataconstructor.DataStructureReader;

public class Item {

    public int ID;
    //public Sprite sprite;
    public String iconImage;
    //public int index;

    private int quantity;
    private transient DataStructure dataStructure;
    private Pool<Object> itemPool;

    private Class[] fieldTypes;

    public Item(){
        //quantity = 0;

    }

    /*public Item(int ID){
        this();
        this.ID = ID;
    }

    public Item(int ID, String imageFile){
        this();
        this.ID = ID;
        this.iconImage = imageFile;

    }*/

    public Item(final GLGame game, final DataStructure dataStructure){
        //this();
        quantity = 5;
        this.dataStructure = dataStructure;
        fieldTypes = new Class[dataStructure.size()];
        ID = (int)dataStructure.values[0];
        iconImage = (String)dataStructure.values[1];

        itemPool = new Pool<>(new Pool.PoolObjectFactory<Object>() {
            @Override
            public Object createObject() {
                DataStructureReader object = null;

                try{
                    Class c = Class.forName(dataStructure.className);
                    object = (DataStructureReader) c.getConstructor(GLGame.class).newInstance(game);
                    object.readDataStructure(dataStructure);
                }catch(ClassNotFoundException e){
                    e.printStackTrace();
                }catch(IllegalAccessException e){
                    e.printStackTrace();
                }catch(InstantiationException e){
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                return object;
            }
        }, 100);
    }

    public Object retrieve(){
        if(quantity <= 0){
            return null;
        }

        quantity--;

        return itemPool.newObject();

        // dataStructure;
    }

    public void putAway(Object item){
        quantity++;
        itemPool.free(item);
    }

    public void addQuantity(int value){
        quantity += value;
    }

    public void subtractQuantity(int value){
        if(quantity < value) {
            quantity = 0;
            return;
        }

        quantity -= value;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public int getQuantity(){
        return quantity;
    }

}
