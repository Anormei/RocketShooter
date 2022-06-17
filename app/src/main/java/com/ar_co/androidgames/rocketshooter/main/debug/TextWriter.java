package com.ar_co.androidgames.rocketshooter.main.debug;

import android.util.SparseArray;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Pool;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TextWriter{

    private static final String COMMA = "comma";
    private static final String FULLSTOP = "fullstop";
    public static final String COLON = "colon";
    public static final String SLASH = "slash";
    public static final String ASTERISK = "asterisk";

    private static final char FORMAT_DECIMAL = 'd';
    private static final char FORMAT_STRING = 's';

    private SparseArray<Formatter> formatters = new SparseArray<>();
    private SparseArray<Texture> characterTextures = new SparseArray<>();

    private String directory;
    private List<Sprite> characters = new ArrayList<>();
    private float padding;
    private float spacing;
    private float fontSize;
    private Pool<Sprite> charPool;

    private Assets assets;

    private float charWidth;
    private float charHeight;

    private abstract class Formatter{

        Formatter(){

        }

        abstract float format(float x, float y, int index, Object arg);
    }

    private Formatter decimalFormatter = new Formatter(){

        @Override
        float format(float x, float y, int index, Object arg) {
            int val = (int) arg;
            int digit;
            int length;

            if(val > 0) {
                length = (int) (Math.log10(val) + 1);
            }else{
                length = 1;
            }

            while(length > 0) {
                digit = (val / (int)Math.pow(10, length - 1)) % 10;

                Texture texture = characterTextures.get((char)(48 + digit));

                Sprite character = charPool.newObject();
                character.width = texture.width * fontSize;
                character.height = texture.height * fontSize;
                character.texture = texture;
                character.defaultRegion();

                character.pos.set(x, y);
                x += character.width + padding;
                characters.add(character);

                length--;
            }

            return x;
        }
    };

    public TextWriter(final GLGame game, String directory, int maxCharacters){

        assets = Assets.getInstance(game);

        this.directory = directory;

        charPool = new Pool<>(new Pool.PoolObjectFactory<Sprite>() {
            @Override
            public Sprite createObject() {
                /* gridBlock.rotationMatrix = rotationMatrix;
                gridBlock.rX = x + (WIDTH / 2.0f);
                gridBlock.rY = y + (HEIGHT / 2.0f);
                gridBlock.alpha = 0.3f;*/
                return new Sprite(game, (Texture)null);
            }
        }, maxCharacters);

        for(int c = 65; c <= 90; c++) {
            Texture texture = assets.getImage(directory + "/" + (char)c);
            characterTextures.put(c, texture);
        }

       for(int c = 48; c <= 57; c++){
            Texture texture = assets.getImage(directory + "/" + (char)c);
            characterTextures.put(c, texture);
        }

        characterTextures.put(':', assets.getImage(directory + "/" + COLON));
        characterTextures.put('/', assets.getImage(directory + "/" + SLASH));
        characterTextures.put('*', assets.getImage(directory + "/" + ASTERISK));
        characterTextures.put(',', assets.getImage(directory + "/" + COMMA));
        characterTextures.put('.', assets.getImage(directory + "/" + FULLSTOP));

        formatters.put(FORMAT_DECIMAL, decimalFormatter);

        charWidth = characterTextures.get('0').width;
        charHeight = characterTextures.get('0').height;
    }

    public void startWriting(){
        fontSize = 4.0f;
        padding = 4.0f;
        spacing = 8.0f;

        for(Iterator<Sprite> iterator = characters.iterator(); iterator.hasNext();){
            Sprite character = iterator.next();

            charPool.free(character);
            iterator.remove();
        }

    }

    public void configText(float fontSize, float padding, float spacing){
        this.fontSize = fontSize;
        this.padding = padding;
        this.spacing = spacing;

    }

    public void writeText(float x, float y, String text){
        for(int i = 0; i < text.length(); i++){
            char c = Character.toUpperCase(text.charAt(i));

            Sprite character = charPool.newObject();

            Texture texture = characterTextures.get(c);

            if(c == ' '){
                x += spacing;
            }

            if(texture != null) {
                character.width = texture.width * fontSize;
                character.height = texture.height * fontSize;
                character.texture = texture;
                character.defaultRegion();

                character.pos.set(x, y);
                x += character.width + padding;
                characters.add(character);
            }else{
                charPool.free(character);
            }
        }
    }

    public void writeFormattedText(float x, float y, String text, Object... args){

        int j = 0;
        for(int i = 0; i < text.length(); i++){
            char c = Character.toUpperCase(text.charAt(i));

            Sprite character = charPool.newObject();

            Texture texture = characterTextures.get(c);

            if(c == ' '){
                x += spacing;
            }

            if(c == '%' && j < args.length){
                Formatter formatter = formatters.get(text.charAt(i + 1));
                if(formatter != null) {
                    x = formatter.format(x, y, i, args[j]);
                }
                j++;
                i++;
            }

            if(texture != null) {
                character.width = texture.width * fontSize;
                character.height = texture.height * fontSize;
                character.texture = texture;
                character.defaultRegion();

                character.pos.set(x, y);
                x += character.width + padding;
                characters.add(character);
            }else{
                charPool.free(character);
            }
        }
    }

    public float getTextWidth(String text){
        float width = charWidth * fontSize;
        float totalWidth = 0;

        for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) == ' '){
                totalWidth += spacing;
            }else if(text.charAt(i) == '%'){
                i++;
            }else{
                totalWidth += width;

                if(i != text.length() - 1){
                    totalWidth += padding;
                }
            }
        }

        return totalWidth;
    }

    public float getNumberWidth(int val){
        int length;

        if(val > 0) {
            length = (int) (Math.log10(val) + 1);
        }else{
            length = 1;
        }

        return (charWidth * fontSize + padding) * length - padding;
    }

    public float getTextHeight(){
        return assets.getImage(directory + "/" + "0").height * fontSize;
    }

    public void renderText(){
        for(int i = 0; i < characters.size(); i++){
            Sprite c = characters.get(i);
            c.render();
        }
    }
}
