package com.ar_co.androidgames.rocketshooter.main.core;

import dataconstructor.DataStructure;
import dataconstructor.DataStructureReader;

public class ItemMetaData implements DataStructureReader {

    public int leftBoundary;
    public int rightBoundary;

    public int size(){
        return rightBoundary - leftBoundary;
    }

    @Override
    public void readDataStructure(DataStructure dataStructure) {
        //this.name = (String)dataStructure.values[0];
        this.leftBoundary = (int)dataStructure.values[0];
        this.rightBoundary = (int)dataStructure.values[1];
    }

}
