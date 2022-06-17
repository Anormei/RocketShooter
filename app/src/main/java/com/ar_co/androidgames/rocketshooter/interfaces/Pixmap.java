package com.ar_co.androidgames.rocketshooter.interfaces;

import android.graphics.Bitmap;

import com.ar_co.androidgames.rocketshooter.interfaces.Graphics.PixmapFormat;

public interface Pixmap {

    int getWidth();

    int getHeight();

    PixmapFormat getFormat();

    Bitmap getBitmap();

    void dispose();
}
