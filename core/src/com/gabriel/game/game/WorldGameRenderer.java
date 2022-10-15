package com.gabriel.game.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gabriel.game.Assets;
import com.gabriel.game.objects.Bird;
import com.gabriel.game.objects.Pipe;
import com.gabriel.game.screens.Screens;

public class WorldGameRenderer {
    final float WIDTH = Screens.WORLD_WIDTH;
    final float HEIGHT = Screens.WORLD_HEIGHT;

    SpriteBatch spriteBatch;
    WorldGame worldGame;
    OrthographicCamera camera;

    Box2DDebugRenderer renderBox;

    public WorldGameRenderer(SpriteBatch spriteBatch, WorldGame worldGame) {

        this.camera = new OrthographicCamera(WIDTH, HEIGHT);
        this.camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        this.spriteBatch = spriteBatch;
        this.worldGame = worldGame;
        renderBox = new Box2DDebugRenderer();
    }

    public void render(float delta) {

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.disableBlending();
        drawBackground(delta);
        spriteBatch.enableBlending();
        drawPipes(delta);
        drawBird(delta);

        spriteBatch.end();

//        renderBox.render(worldGame.worldBox, camera.combined);
    }

    private void drawBird(float delta) {
        Bird obj = worldGame.birdInfo;
        TextureRegion keyFrame;

        if (obj.state == Bird.STATE_NORMAL) {
            keyFrame = Assets.bird.getKeyFrame(obj.stateTime, true);
        } else {
            keyFrame = Assets.bird.getKeyFrame(obj.stateTime, false);
        }

        spriteBatch.draw(keyFrame, obj.position.x - .3f, obj.position.y - .25f, .6f, .5f);
    }

    private void drawPipes(float delta) {
        for (Pipe obj : worldGame.pipes) {

            if (obj.type == Pipe.TYPE_UP) {
                spriteBatch.draw(Assets.pipeUp,
                        obj.position.x - .5f,
                        obj.position.y - 2f,
                        1, 4);
            } else {
                spriteBatch.draw(Assets.pipeDown,
                        obj.position.x - .5f,
                        obj.position.y - 2f,
                        1, 4);
            }
        }
    }

    private void drawBackground(float delta) {
        spriteBatch.draw(Assets.background, 0, 0, WIDTH, HEIGHT);
    }

}
