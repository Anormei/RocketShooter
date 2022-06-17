package com.ar_co.androidgames.rocketshooter.main.garage;

import com.ar_co.androidgames.rocketshooter.framework.GLGame;
import com.ar_co.androidgames.rocketshooter.framework.Sprite;
import com.ar_co.androidgames.rocketshooter.framework.assetmanager.Assets;
import com.ar_co.androidgames.rocketshooter.main.debug.TextWriter;
import com.ar_co.androidgames.rocketshooter.main.ui.FormattedTextLabel;
import com.ar_co.androidgames.rocketshooter.main.ui.TextLabel;

public class Menu {

    public static final int INDEX_HP = 0;
    public static final int INDEX_DMG = 1;
    public static final int INDEX_SDMG = 2;
    public static final int INDEX_MOB = 3;
    public static final int INDEX_SPD = 4;

    private static final int STAT_SIZE = 5;

    private static final float STAT_X = 312.0f;
    private static final float STAT_Y = 156.0f;
    private static final float STAT_Y_SPACE = 20.0f;

    private Sprite window;
    private TextWriter textWriter;

    private Sprite menuButton;
    private Sprite infoBox;
    private Sprite capacityBorder;

    private TextLabel capacityLabel;

    private TextLabel statisticsLabel;

    private TextLabel hpLabel;
    private TextLabel dmgLabel;
    private TextLabel sdmgLabel;
    private TextLabel mobLabel;
    private TextLabel spdLabel;

    private FormattedTextLabel[] textValues = new FormattedTextLabel[STAT_SIZE];
    private float[] values = new float[STAT_SIZE];

    public Menu(final GLGame game, float x, float y){
        Assets assets = Assets.getInstance(game);

        window = new Sprite(game, assets.getImage("graphics/gameplay/garage/menu-window"), 4.0f);
        window.pos.set(x, y);

        textWriter = new TextWriter(game, "graphics/gameplay/debug/charset", 100);

        menuButton = new Sprite(game, assets.getImage("graphics/gameplay/garage/menu-button"), 4.0f);
        menuButton.pos.set(0, 0);

        infoBox = new Sprite(game, assets.getImage("graphics/gameplay/garage/info-border"), 4.0f);
        infoBox.pos.set(368.0f, 116.0f);

        capacityLabel = new TextLabel(textWriter, "Capacity");

        capacityBorder = new Sprite(game, assets.getImage("graphics/gameplay/garage/capacity-border"), 4.0f);
        capacityBorder.pos.set(368.0f, 48.0f);

        statisticsLabel = new TextLabel(textWriter, "Statistics");
        hpLabel = new TextLabel(textWriter, "HP");
        dmgLabel = new TextLabel(textWriter, "DMG");
        sdmgLabel = new TextLabel(textWriter, "S.DMG");
        mobLabel = new TextLabel(textWriter, "MOB");
        spdLabel = new TextLabel(textWriter, "SPD");

        for(int i = 0; i < textValues.length; i++){
            textValues[i] = new FormattedTextLabel(textWriter, "%d");
            values[i] = 0.0f;
        }
    }

    public void update(float dt){
        textWriter.startWriting();

        capacityLabel.write(388.0f, 20.0f);

        statisticsLabel.write(96.0f, 116.0f);

        hpLabel.write(24.0f, 156.0f);
        dmgLabel.write(24.0f, 196.0f);
        sdmgLabel.write(24.0f, 236.0f);
        mobLabel.write(24.0f, 276.0f);
        spdLabel.write(24.0f, 316.0f);

        for(int i = 0; i < textValues.length; i++){
            textValues[i].write(STAT_X, STAT_Y + (textValues[i].height() + STAT_Y_SPACE) * i, (int)Math.ceil(values[i]));
        }
    }

    public void render(){
        window.render();
        menuButton.render();
        capacityBorder.render();
        infoBox.render();

        textWriter.renderText();
    }


}
