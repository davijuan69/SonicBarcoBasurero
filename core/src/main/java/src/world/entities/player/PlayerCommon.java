package src.world.entities.player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import src.utils.animation.SheetCutter;
import src.utils.stateMachine.StateMachine;
import src.utils.stateMachine.StateMachine;
import src.world.entities.Entity;
//import src.world.entities.player.powers.*;
import src.world.entities.player.states.*;

import static src.utils.constants.Constants.PIXELS_IN_METER;

public abstract class PlayerCommon extends Entity {
    public float speed = 12;
    public float maxSpeed = 6;
    public float stunTime = DEFAULT_STUNT_TIME;
    public float brakeForce = DEFAULT_BRAKE_FORCE;
    public int dashDamage = DEFAULT_DASH_DAMAGE;

    // Variables para evitar que se pegue en curvas
    public float wallUnstickForce = 15f;
    public float wallDetectionDistance = 0.5f;
    public boolean isStuckOnWall = false;
    public float wallStuckTimer = 0f;
    public static final float WALL_STUCK_THRESHOLD = 0.5f; // Tiempo máximo que puede estar "pegado"

    public static final int DEFAULT_DASH_DAMAGE = 1;
    public static final float DEFAULT_STUNT_TIME = 1f;
    public static final float WALK_SPEED = 14f;
    public static final float WALK_MAX_SPEED = 5f;
    public static final float RUN_SPEED = 18f;
    public static final float RUN_MAX_SPEED = 6.5f;
    public static final float MAX_JUMP_TIME = 0.3f;
    public static final float JUMP_IMPULSE = 8f;
    public static final float JUMP_INAIR = 25f; // Se multiplica por deltaTime
    public static final float FLY_IMPULSE = 6f;
    public static final float DASH_IMPULSE = 15f;
    public static final float ABSORB_FORCE = 12f;
    public static final float DEFAULT_BRAKE_FORCE = 280f;

    public AssetManager assetManager;

    public enum StateType {
        IDLE,
        WALK,
        JUMP,
        FALL,
        DOWN,
        RUN,
        DASH,
        STUN,
    }
    protected StateType currentStateType;
    private final StateMachine stateMachine;
    protected IdleState idleState;
    protected JumpState jumpState;
    protected WalkState walkState;
    protected FallState fallState;
//    protected DownState downState;
//    protected DashState dashState;
    protected RunState runState;
//    protected StunState stunState;

    public enum AnimationType {
        IDLE,
        WALK,
        JUMP,
        FALL,
        FALLSIMPLE,
        DOWN,
        RUN,
        CHANGERUN,
        DASH,
        DAMAGE,
        MAXSPEED,
    }
    private AnimationType currentAnimationType;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> jumpAnimation;
    protected Animation<TextureRegion> fallAnimation;
    protected Animation<TextureRegion> fallSimpleAnimation;
    protected Animation<TextureRegion> downAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> changeRunAnimation;
    protected Animation<TextureRegion> dashAnimation;;
    protected Animation<TextureRegion> damageAnimation;;
    protected Animation<TextureRegion> maxSpeedAnimation;

    private final Sprite secondSprite;
    private Animation<TextureRegion> secondCurrentAnimation;

    protected float bodyWidth, bodyHeight;

    private Boolean paused = false;

    // Variables para transición suave de animaciones
    private float walkToRunTimer = 0f;
    private float runToMaxTimer = 0f;
    private boolean hasBoostedRun = false;
    private boolean hasBoostedMax = false;
    private static final float TRANSITION_TIME = 0.7f;
    private static final float RUN_THRESHOLD = 4f;
    private static final float MAX_THRESHOLD = 5f;
    private static final float RUN_BOOST = 10f;
    private static final float MAX_BOOST = 15f;

    public PlayerCommon(World world, Float x, Float y, AssetManager assetManager, Integer id) {
        super(world, new Rectangle(x,y,2.25f,2.25f), assetManager, id, null);
        bodyHeight = bodyWidth = 2.25f;
        this.assetManager = assetManager;
        stateMachine = new StateMachine();

        BodyDef def = new BodyDef();
        def.position.set(x + bodyWidth / 2, y + bodyHeight/ 2);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        CircleShape box = new CircleShape();
        box.setRadius(bodyWidth/6);
        fixture = body.createFixture(box, 1.9f);
        fixture.setUserData(this);
        box.dispose();
        body.setFixedRotation(true);

        setSpritePosModification(0f, getHeight()/3);

        initAnimations(assetManager);
        setAnimation(AnimationType.IDLE);

        secondSprite = new Sprite();
        secondSprite.setSize(bodyWidth * PIXELS_IN_METER, bodyHeight * PIXELS_IN_METER);
    }

//    private void initPowers(){
//        powerSleep = new PowerSleep(this);
//        powerSword = new PowerSword(this);
//        powerBomb = new PowerBomb(this);
//        powerWheel = new PowerWheel(this);
//    }

    private void initAnimations(AssetManager assetManager){
        walkAnimation = new Animation<>(0.11f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Sonic/Sonic_correr1.png"), 6));
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        idleAnimation = new Animation<>(0.5f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Sonic/Sonic_idle.png"), 4));
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Animaciones de salto y caída usando yoshi.png (1 frame)
        jumpAnimation = new Animation<>(0.1f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Sonic/Sonic_bolita.png"), 1));
        jumpAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        fallAnimation = new Animation<>(0.1f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Sonic/Sonic_cayendo.png"), 2));
        fallAnimation.setPlayMode(Animation.PlayMode.NORMAL);

//        fallSimpleAnimation = new Animation<>(0.04f,
//            SheetCutter.cutHorizontal(assetManager.get(""), 20));
//
        runAnimation = new Animation<>(0.04f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Sonic/Sonic_correr2.png"), 4));
        runAnimation.setPlayMode(Animation.PlayMode.LOOP);

        maxSpeedAnimation = new Animation<>(0.03f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Sonic/Sonic_correr3.png"), 4));
        maxSpeedAnimation.setPlayMode(Animation.PlayMode.LOOP);
//
//        changeRunAnimation = new Animation<>(1f,
//            SheetCutter.cutHorizontal(assetManager.get(""), 1));
//
//        dashAnimation = new Animation<>(0.04f,
//            SheetCutter.cutHorizontal(assetManager.get(""), 2));

    }

    public void setAnimation(AnimationType animationType){
        if (animationType == null) return;
        currentAnimationType = animationType;

//        if (currentPowerUp != null) {
//            setSecondCurrentAnimation(currentPowerUp.getSecondAnimation(animationType));
//            Animation<TextureRegion> anima = currentPowerUp.getAnimation(animationType);
//            setCurrentAnimation(anima);
//            if (anima != null) return;
//        }

        switch (animationType){
            case IDLE -> setCurrentAnimation(idleAnimation);
            case WALK -> setCurrentAnimation(walkAnimation);
            case JUMP -> setCurrentAnimation(jumpAnimation);
            case FALL -> setCurrentAnimation(fallAnimation);
            case FALLSIMPLE -> setCurrentAnimation(fallSimpleAnimation);
            case DOWN -> setCurrentAnimation(downAnimation);
            case RUN -> setCurrentAnimation(runAnimation);
            case CHANGERUN -> setCurrentAnimation(changeRunAnimation);
            case DASH -> setCurrentAnimation(dashAnimation);
            case DAMAGE -> setCurrentAnimation(damageAnimation);
            case MAXSPEED -> setCurrentAnimation(maxSpeedAnimation);
        }

    }

    public void setSecondCurrentAnimation(Animation<TextureRegion> secondCurrentAnimation) {
        this.secondCurrentAnimation = secondCurrentAnimation;
        resetAnimateTime();
    }

    public AnimationType getCurrentAnimationType() {
        return currentAnimationType;
    }

    public void setCurrentState(Player.StateType stateType){
        currentStateType = stateType;
        switch (stateType){
            case IDLE -> stateMachine.setState(idleState);
            case WALK -> stateMachine.setState(walkState);
            case JUMP -> stateMachine.setState(jumpState);
            case FALL -> stateMachine.setState(fallState);
//            case DOWN -> stateMachine.setState(downState);
            case RUN -> stateMachine.setState(runState);
//            case DASH -> stateMachine.setState(dashState);
//            case STUN -> stateMachine.setState(stunState);
        }
    }

    public StateType getCurrentStateType() {
        return currentStateType;
    }

//    public void setCurrentPowerUp(PowerUp.Type type){
//        currentpowerUptype = type;
//        if (currentPowerUp != null) currentPowerUp.end();
//        if (type == PowerUp.Type.NONE || type == null) {
//            currentPowerUp = null;
//            currentAnimationType = null;
//            return;
//        }
//        currentPowerUp = switch (type){
//            case BOMB -> powerBomb;
//            case SLEEP -> powerSleep;
//            case SWORD -> powerSword;
//            case WHEEL -> powerWheel;
//            default -> null;
//        };
//        currentPowerUp.start();
//    }
//
//    public PowerUp getCurrentPowerUp() {
//        return currentPowerUp;
//    }
//
//    public PowerUp.Type getCurrentpowerUptype() {
//        return currentpowerUptype;
//    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (secondCurrentAnimation != null) secondSprite.setRegion(secondCurrentAnimation.getKeyFrame(getAnimateTime(), false));
        else secondSprite.setTexture(null);
        if (secondSprite.getTexture() == null) return;
        secondSprite.setFlip(isFlipX(), false);
        secondSprite.setPosition(getX(), getY());
        secondSprite.setOriginCenter();
        secondSprite.draw(batch);
    }

    @Override
    public void act(float delta) {
        if (paused) return;
        stateMachine.update(delta);
        float vx = body.getLinearVelocity().x;
        float absVx = Math.abs(vx);
        boolean movingRight = vx > 0;
        // Transición de caminar a correr
        if (getCurrentStateType() == StateType.WALK || getCurrentStateType() == StateType.RUN) {
            if (absVx > MAX_THRESHOLD) {
                runToMaxTimer += delta;
                walkToRunTimer = 0f;
                if (runToMaxTimer > TRANSITION_TIME) {
                    if (getCurrentAnimationType() != AnimationType.MAXSPEED) {
                        setAnimation(AnimationType.MAXSPEED);
                        if (!hasBoostedMax) {
                            body.applyLinearImpulse((movingRight ? MAX_BOOST : -MAX_BOOST), 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
                            hasBoostedMax = true;
                        }
                    }
                }
            } else if (absVx > RUN_THRESHOLD) {
                walkToRunTimer += delta;
                runToMaxTimer = 0f;
                hasBoostedMax = false;
                if (walkToRunTimer > TRANSITION_TIME) {
                    if (getCurrentAnimationType() != AnimationType.RUN) {
                        setAnimation(AnimationType.RUN);
                        if (!hasBoostedRun) {
                            body.applyLinearImpulse((movingRight ? RUN_BOOST : -RUN_BOOST), 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
                            hasBoostedRun = true;
                        }
                    }
                }
            } else {
                walkToRunTimer = 0f;
                runToMaxTimer = 0f;
                hasBoostedRun = false;
                hasBoostedMax = false;
                if (getCurrentAnimationType() != AnimationType.WALK) {
                    setAnimation(AnimationType.WALK);
                }
            }
        } else {
            walkToRunTimer = 0f;
            runToMaxTimer = 0f;
            hasBoostedRun = false;
            hasBoostedMax = false;
        }
    }
}
