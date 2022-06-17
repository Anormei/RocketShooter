package com.ar_co.androidgames.rocketshooter.main.ui;

import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;

public class FormattedTextLabel {

    private String text;
    private TextWriter textWriter;

    public FormattedTextLabel(TextWriter textWriter, String text){
        this.text = text;
        this.textWriter = textWriter;
    }

    public void write(float x, float y, Object... args){
        textWriter.writeFormattedText(x, y, text, args);
    }

    public float width(){
        return textWriter.getTextWidth(text);
    }

    public float height(){
        return textWriter.getTextHeight();
    }
}
