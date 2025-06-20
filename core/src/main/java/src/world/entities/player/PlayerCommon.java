package src.world.entities.player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import src.world.entities.Entity;
import com.badlogic.gdx.graphics.Texture;

public abstract class PlayerCommon extends Entity {

    public enum State {
        IDLE,
        RUNNING,
        JUMPING,
        FALLING
    }

    private static final float PIXELS_IN_METER = 100f;

    private State currentState = State.IDLE;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;

    private float stateTime = 0f;
    private Sprite sprite;

    private float speed = 10f;
    private boolean facingRight = true;

    public PlayerCommon(World world, float x, float y, AssetManager assets, int id) {
        super(world, new Rectangle(x, y, 1.5f, 1.5f), assets, id, null);

        // Crear cuerpo físico simple Box2D
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x + 0.75f, y + 0.75f);  // centro del cuerpo
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.75f, 0.75f);
        fixture = body.createFixture(shape, 1f);
        fixture.setUserData(this);
        shape.dispose();
        body.setFixedRotation(true);

        // Cargar animaciones (asume que las texturas están en assets)
        idleAnimation = new Animation<>(0.1f,
            TextureRegion.split(assets.get("sanicQuieto.webp", Texture.class), 64, 64)[0]);
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        runAnimation = new Animation<>(0.08f,
            TextureRegion.split(assets.get("sanicCorriendo.webp", Texture.class), 64, 64)[0]);
        runAnimation.setPlayMode(Animation.PlayMode.LOOP);

        jumpAnimation = new Animation<>(0.1f,
            TextureRegion.split(assets.get("sanicSaltando.jpg", Texture.class), 64, 64)[0]);

        sprite = new Sprite();
        sprite.setSize(1.5f * PIXELS_IN_METER, 1.5f * PIXELS_IN_METER);


    }

    public void update(float delta) {
        stateTime += delta;

        // Actualizar estado según velocidad y posición vertical
        float vy = body.getLinearVelocity().y;
        if (vy > 0.1f) {
            currentState = State.JUMPING;
        } else if (vy < -0.1f) {
            currentState = State.FALLING;
        } else {
            float vx = body.getLinearVelocity().x;
            if (Math.abs(vx) > 0.1f) {
                currentState = State.RUNNING;
                facingRight = vx > 0;
            } else {
                currentState = State.IDLE;
            }
        }

        // Actualizar sprite según animación y estado
        TextureRegion frame;
        switch (currentState) {
            case RUNNING:
                frame = runAnimation.getKeyFrame(stateTime, true);
                break;
            case JUMPING:
                frame = jumpAnimation.getKeyFrame(stateTime, false);
                break;
            case FALLING:
                frame = jumpAnimation.getKeyFrame(stateTime, false);  // usa la misma que salto
                break;
            case IDLE:
            default:
                frame = idleAnimation.getKeyFrame(stateTime, true);
                break;
        }

        sprite.setRegion(frame);
        sprite.setFlip(!facingRight, false);
        sprite.setPosition((body.getPosition().x - 0.75f) * PIXELS_IN_METER,
            (body.getPosition().y - 0.75f) * PIXELS_IN_METER);
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    // Métodos para controlar movimiento simples

    public void moveRight() {
        body.setLinearVelocity(speed, body.getLinearVelocity().y);
    }

    public void moveLeft() {
        body.setLinearVelocity(-speed, body.getLinearVelocity().y);
    }

    public void jump() {
        if (currentState != State.JUMPING && currentState != State.FALLING) {
            body.applyLinearImpulse(0, 8f, body.getWorldCenter().x, body.getWorldCenter().y, true);
        }
    }
}
