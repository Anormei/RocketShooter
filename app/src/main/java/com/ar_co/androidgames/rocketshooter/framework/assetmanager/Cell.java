package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import java.util.ArrayList;
import java.util.List;

public class Cell<T> {

    private List<T> cont = new ArrayList<>();

    public Cell(){

    }

    public Cell(T... objects){
        for(int i = 0; i < objects.length; i++){
            cont.add(objects[i]);
        }

    }

    public void add(T obj){
        cont.add(obj);
    }


    public void add(T... objects){
        for(int i = 0; i < objects.length; i++){
            cont.add(objects[i]);
        }
    }

    public List<T> getAll(){
        return cont;
    }
}
