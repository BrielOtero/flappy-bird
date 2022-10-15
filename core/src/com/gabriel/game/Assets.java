package com.gabriel.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Assets {
    public static BitmapFont font;
    private static final GlyphLayout glyphLayout = new GlyphLayout();

    public static Animation<AtlasRegion> bird;

    public static TextureRegion background;
    public static TextureRegion gameOver;
    public static TextureRegion getReady;
    public static TextureRegion tap;
    public static TextureRegion pipeDown;
    public static TextureRegion pipeUp;

    public static void load() {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("atlasMap.txt"));

        background = atlas.findRegion("background");
        gameOver = atlas.findRegion("gameOver");
        getReady = atlas.findRegion("getReady");
        tap = atlas.findRegion("tap");
        pipeDown = atlas.findRegion("pipeDown");
        pipeUp = atlas.findRegion("pipeUp");

        bird = new Animation<TextureAtlas.AtlasRegion>(.3f,
                atlas.findRegion("bird1"),
                atlas.findRegion("bird2"),
                atlas.findRegion("bird3"));

        // Use default libGDX font
        font = new BitmapFont();
        font.getData().scale(7f);
    }

    //Get the text width in order to center in the screen
    public static float getTextWidth(String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }

}
