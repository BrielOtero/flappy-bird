package com.gabriel.game.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gabriel.game.Assets;
import com.gabriel.game.objects.Bird;
import com.gabriel.game.objects.Counter;
import com.gabriel.game.objects.Pipe;
import com.gabriel.game.screens.Screens;

public class WorldGame {
    final float WIDTH = Screens.WORLD_WIDTH;
    final float HEIGHT = Screens.WORLD_HEIGHT;

    static final int STATE_RUNNING = 0;
    static final int STATE_GAME_OVER = 1;
    public int state;

    // Time between pipes, if you increase this number the space between pipes will increase
    final float TIME_TO_SPAWN_PIPE = 1.5f;
    float timeToSpawnPipe;

    public World worldBox;
    public int score;

    //Save the information about the bird
    Bird birdInfo;

    //Save the information about the pipes
    Array<Pipe> pipes;

    //Save the information about the bodies (box2d). Includes: Birds, pipes & counter object
    Array<Body> bodies;

    public WorldGame() {
        worldBox = new World(new Vector2(0, -13.0f), true);
        worldBox.setContactListener(new Collisions());

        bodies = new Array<>();
        pipes = new Array<>();

        timeToSpawnPipe = 1.5f;

        createBird();
        createRoof();
        createFloor();

        state = STATE_RUNNING;
    }

    private void createBird() {
        birdInfo = new Bird(1.35f, 4.75f);

        BodyDef bd = new BodyDef();
        bd.position.x = birdInfo.position.x;
        bd.position.y = birdInfo.position.y;
        bd.type = BodyType.DynamicBody;

        Body body = worldBox.createBody(bd);

        CircleShape shape = new CircleShape();
        shape.setRadius(.25f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 8;
        body.createFixture(fixture);

        body.setFixedRotation(true);
        body.setUserData(birdInfo);
        body.setBullet(true);

        shape.dispose();
    }

    private void createRoof() {

        BodyDef bd = new BodyDef();
        bd.position.x = 0;
        bd.position.y = HEIGHT;
        bd.type = BodyDef.BodyType.StaticBody;

        Body body = worldBox.createBody(bd);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 0, WIDTH, 0);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;

        body.createFixture(fixture);
        shape.dispose();
    }

    private void createFloor() {

        BodyDef bd = new BodyDef();
        bd.position.x = 0;
        bd.position.y = 1.1f;
        bd.type = BodyDef.BodyType.StaticBody;

        Body body = worldBox.createBody(bd);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 0, WIDTH, 0);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;

        body.createFixture(fixture);
        shape.dispose();
    }

    private void addPipe() {
        float x = WIDTH + 2.5f;
        float y = MathUtils.random() * (1.5f) + .4f;

        // Add the top pipe
        addPipe(x, y + 2f + Pipe.HEIGHT, true);

        // Add the bottom pipe
        addPipe(x, y, false);

        //add counter object (between the two pipes)
        addCounter(x, y + Counter.HEIGHT / 2f + Pipe.HEIGHT / 2f + .1f);
    }

    private void addPipe(float x, float y, boolean isTopPipe) {

        Pipe obj;

        if (isTopPipe) {
            obj = new Pipe(x, y, Pipe.TYPE_UP);
        } else {
            obj = new Pipe(x, y, Pipe.TYPE_DOWN);
        }

        BodyDef bd = new BodyDef();
        bd.position.x = x;
        bd.position.y = y;
        bd.type = BodyDef.BodyType.KinematicBody;

        Body body = worldBox.createBody(bd);
        body.setLinearVelocity(Pipe.SPEED_X, 0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Pipe.WIDTH / 2f, Pipe.HEIGHT / 2f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;

        body.createFixture(fixture);
        body.setFixedRotation(true);
        body.setUserData(obj);

        pipes.add(obj);

        shape.dispose();
    }

    private void addCounter(float x, float y) {
        Counter obj = new Counter();

        BodyDef bd = new BodyDef();
        bd.position.x = x;
        bd.position.y = y;
        bd.type = BodyDef.BodyType.KinematicBody;

        Body body = worldBox.createBody(bd);
        body.setLinearVelocity(Counter.SPEED_X, 0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Counter.WIDTH / 2f, Counter.HEIGHT / 2f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.isSensor = true;

        body.createFixture(fixture);
        body.setFixedRotation(true);
        body.setUserData(obj);

        shape.dispose();
    }

    public void update(float delta, boolean jump) {
        worldBox.step(delta, 8, 4);

        deleteObjects();

        timeToSpawnPipe += delta;

        if (timeToSpawnPipe >= TIME_TO_SPAWN_PIPE) {
            timeToSpawnPipe -= TIME_TO_SPAWN_PIPE;
            addPipe();
        }

        worldBox.getBodies(bodies);

        for (Body body : bodies) {
            if (body.getUserData() instanceof Bird) {
                updateBird(body, delta, jump);
            } else if (body.getUserData() instanceof Pipe) {
                updatePipes(body);
            } else if (body.getUserData() instanceof Counter) {
                updateCounter(body);
            }
        }

        if (birdInfo.state == Bird.STATE_DEAD) {
            state = STATE_GAME_OVER;
        }
    }

    private void deleteObjects() {
        worldBox.getBodies(bodies);
        for (Body body : bodies) {

            if (!worldBox.isLocked()) {
                if (body.getUserData() instanceof Pipe) {
                    Pipe obj = (Pipe) body.getUserData();
                    if (obj.state == Pipe.STATE_REMOVE) {
                        pipes.removeValue(obj, true);
                        worldBox.destroyBody(body);
                    }
                } else if (body.getUserData() instanceof Counter) {
                    Counter obj = (Counter) body.getUserData();
                    if (obj.state == Counter.STATE_REMOVE) {
                        worldBox.destroyBody(body);
                    }
                }
            }
        }
    }

    private void updateCounter(Body body) {
        if (birdInfo.state == Bird.STATE_NORMAL) {
            Counter obj = (Counter) body.getUserData();

            obj.update(body);

            if (obj.position.x <= -5) {
                obj.state = Counter.STATE_REMOVE;
            }
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    private void updatePipes(Body body) {
        if (birdInfo.state == Bird.STATE_NORMAL) {
            Pipe obj = (Pipe) body.getUserData();

            obj.update(body);

            if (obj.position.x <= -5) {
                obj.state = Pipe.STATE_REMOVE;
            }
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    private void updateBird(Body body, float delta, boolean jump) {

        birdInfo.update(delta, body);

        if (jump && birdInfo.state == Bird.STATE_NORMAL) {
            body.setLinearVelocity(0, Bird.JUMP_SPEED);
            Assets.jumpSound.play();
        }
    }

    class Collisions implements ContactListener {

        @Override
        public void beginContact(Contact contact) {

            Fixture a = contact.getFixtureA();
            Fixture b = contact.getFixtureB();

            if (a.getBody().getUserData() instanceof Bird) {
                beginContactBird(a, b);
            } else if (b.getBody().getUserData() instanceof Bird) {
                beginContactBird(b, a);

            }
        }

        private void beginContactBird(Fixture bird, Fixture fixElse) {

            Object somethingElse = fixElse.getBody().getUserData();

            if (somethingElse instanceof Counter) {
                Counter obj = (Counter) somethingElse;

                if (obj.state == Counter.STATE_NORMAL) {
                    obj.state = Counter.STATE_REMOVE;
                    score++;
                    if (score > GameScreen.maxScore) {
                        GameScreen.maxScore = score;
                        GameScreen.prefs.putInteger("maxScore",score);
                        GameScreen.prefs.flush();

                    }
                    Assets.pointSound.play();
                }
            } else {
                if (birdInfo.state == Bird.STATE_NORMAL) {
                    birdInfo.hurt();
                    Assets.loseSound.play();

                }
            }
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }


}
