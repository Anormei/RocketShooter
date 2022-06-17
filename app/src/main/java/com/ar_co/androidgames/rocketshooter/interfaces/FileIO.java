package com.ar_co.androidgames.rocketshooter.interfaces;

import java.io.IOException;

public interface FileIO {
    Object readObject(String filename) throws IOException;
    void writeObject(Object object, String filename) throws IOException;
}
