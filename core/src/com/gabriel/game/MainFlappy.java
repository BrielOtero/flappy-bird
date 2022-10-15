package com.gabriel.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gabriel.game.game.GameScreen;

public class MainFlappy extends Game {


	@Override
	public void create() {
	Assets.load();
	setScreen(new GameScreen(this));
	}
}
