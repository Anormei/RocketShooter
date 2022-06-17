package com.ar_co.androidgames.rocketshooter.main.ui;

import com.ar_co.androidgames.rocketshooter.framework.Vector2;
import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;

public class TextLabel {

    private String text;
    private TextWriter textWriter;

    public TextLabel(TextWriter textWriter, String text){
        this.text = text;
        this.textWriter = textWriter;
    }

    public void write(float x, float y){
        textWriter.writeText(x, y, text);
    }

    public float width(){
        return textWriter.getTextWidth(text);
    }

    public float height(){
        return textWriter.getTextHeight();
    }
}
