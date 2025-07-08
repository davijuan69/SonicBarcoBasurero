package src.world.entities.player;

/**
 * Clase Player: implementación concreta del jugador jugable.
 * Gestiona comportamiento específico en juego, incluyendo poderes, colisiones, estados, sonidos y animaciones.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import src.screens.game.GameScreen;
import src.utils.Box2dUtils;
import src.utils.FrontRayCastCallback;
import src.utils.SoundPicthUp;
import src.utils.constants.CollisionFilters;
import src.utils.constants.PlayerControl;
import src.utils.sound.SingleSoundManager;
import src.utils.sound.SoundManager;
import src.world.ActorBox2d;
import src.world.entities.Entity;
import src.world.entities.player.states.*;
import java.util.ArrayList;
import java.util.Random;

public class Player extends PlayerCommon {
    // Cantidad de monedas que suelta al recibir daño
    public Integer coinDrop = 3;
    public static final Integer DEFAULT_COIN_DROP = 3;

    // Pantalla principal del juego que gestiona este jugador
    public final GameScreen game;

    // Color del jugador (visual)
    private final Color color;
    private Float invencibleTime; // Tiempo restante de invencibilidad
    private Boolean invencible;   // Si el jugador es invencible

    private final Random random; // Generador aleatorio para dispersión de monedas y poderes

    /**
     * Direcciones posibles para lanzar objetos.
     */
    public enum ThrowDirection {
        LEFT, RIGHT, UP, DOWN,
    }

    /**
     * Tipos de sonidos que puede reproducir el jugador.
     */
    public enum SoundType {
        DASH,          // Sonido reproducido al hacer un dash (embestida).
        FIREDAMAGE,    // Sonido reproducido cuando el jugador recibe daño por fuego o lava.
        NORMALDAMAGE,  // Sonido estándar de daño (cuando el jugador es golpeado por un enemigo o espinas).
        HEAVYFALL,     // Sonido reproducido al caer desde una gran altura (impacto fuerte).
        ITEM,          // Sonido al recolectar o interactuar con un objeto importante.
        JUMP,          // Sonido al saltar.
        POWER,         // Sonido al adquirir o activar un poder absorbido.
        SCORE,         // Sonido asociado a ganar puntos (por ejemplo, al terminar minijuegos).
        COIN,          // Sonido al recolectar monedas..


    }

    // Sonidos individuales del jugador
    private Sound dashSound, fireDamageSound,
        normalDamageSound, heavyFallSound, itemSound, jumpSound,
        powerSound, scoreSound;
    private SoundPicthUp coinSound;

    /**
     * Constructor principal del jugador.
     */
    public Player(World world, Float x, Float y, AssetManager assetManager, GameScreen game, Color color) {
        super(world, x, y, assetManager, -1);
        this.game = game;
        this.color = color;

        setColor(this.color);

        // Configurar filtros de colisión (ignora a otros jugadores)
        Filter filter = new Filter();
        filter.categoryBits = CollisionFilters.PLAYER;
        fixture.setFilterData(filter);

        initStates();
        setCurrentState(StateType.IDLE);

        //initSound();

        invencibleTime = 0f;
        invencible = false;

        random = new Random();
    }

    /**
     * Inicializa todos los estados posibles del jugador.
     */
    private void initStates() {
        idleState = new IdleState(this);
        walkState = new WalkState(this);
        runState = new RunState(this);
        jumpState = new JumpState(this);
        fallState = new FallState(this);
       /*

        downState = new DownState(this);
        dashState = new DashState(this);

        stunState = new StunState(this);

        */
    }

    /** Carga todos los sonidos del jugador desde el AssetManager. */

    /*private void initSound(){
        dashSound = assetManager.get("sound/kirby/kirbyDash.wav");
        fireDamageSound = assetManager.get("sound/kirby/kirbyFireDamage.wav");
        normalDamageSound = assetManager.get("sound/kirby/kirbyNormalDamage.wav");
        heavyFallSound = assetManager.get("sound/kirby/kirbyHeavyFall.wav");
        itemSound = assetManager.get("sound/kirby/kirbyItem.wav");
        jumpSound = assetManager.get("sound/kirby/kirbyJump.wav");
        powerSound = assetManager.get("sound/kirby/kirbyPower.wav");
        scoreSound = assetManager.get("sound/kirby/kirbyScore1.wav");
        coinSound = new SoundPicthUp(assetManager.get("sound/coin.wav"), 0.1f, 2f);
    }

     */

    /**
     * Establece animación y sincroniza con otros jugadores.
     */
    @Override
    public void setAnimation(AnimationType animationType) {
        super.setAnimation(animationType);

    }


    /**
     * Obtiene el sonido correspondiente al tipo.
     */
    private Sound getSound(SoundType type) {
        return switch (type) {
            case DASH -> dashSound;
            case FIREDAMAGE -> fireDamageSound;
            case NORMALDAMAGE -> normalDamageSound;
            case HEAVYFALL -> heavyFallSound;
            case ITEM -> itemSound;
            case JUMP -> jumpSound;
            case POWER -> powerSound;
            case SCORE -> scoreSound;
            case COIN -> coinSound;
        };
    }

    /**
     * Vuelve al jugador invencible por un periodo.
     */
    public void setInvencible(Float time) {
        invencible = true;
        setColor(Color.GOLD);
        invencibleTime = time;
    }

    public Boolean isInvencible() {
        return invencible;
    }

    public ArrayList<Fixture> detectFrontFixtures(float distance) {
        ArrayList<Fixture> hitFixtures = new ArrayList<>();
        Vector2 startPoint = body.getPosition();

        Vector2 endPoint = new Vector2(startPoint.x + distance, startPoint.y);
        FrontRayCastCallback callback = new FrontRayCastCallback();
        world.rayCast(callback, startPoint, endPoint);
        if (callback.getHitFixture() != null) {
            hitFixtures.add(callback.getHitFixture());
        }

        endPoint.set(startPoint.x + distance * MathUtils.cosDeg(35), startPoint.y + distance * MathUtils.sinDeg(35));
        callback = new FrontRayCastCallback();
        world.rayCast(callback, startPoint, endPoint);
        if (callback.getHitFixture() != null) {
            hitFixtures.add(callback.getHitFixture());
        }

        endPoint.set(startPoint.x + distance * MathUtils.cosDeg(-35), startPoint.y + distance * MathUtils.sinDeg(-35));
        callback = new FrontRayCastCallback();
        world.rayCast(callback, startPoint, endPoint);
        if (callback.getHitFixture() != null) {
            hitFixtures.add(callback.getHitFixture());
        }

        return hitFixtures;
    }

    /**
     * Aplica una fuerza al centro de una fixture en dirección hacia el jugador.
     *
     * @param fixture        Fixture objetivo
     * @param forceMagnitude Magnitud base de la fuerza aplicada
     */
    public void attractFixture(Fixture fixture, Float forceMagnitude) {
        Vector2 playerPosition = body.getPosition();
        Vector2 fixturePosition = fixture.getBody().getPosition();

        Vector2 direction = playerPosition.cpy().sub(fixturePosition).nor();
        float distance = playerPosition.dst(fixturePosition);
        Vector2 force = direction.scl(forceMagnitude * distance);
        fixture.getBody().applyForceToCenter(force, true);
    }

    /**
     * Lógica principal del jugador en cada frame (actualización por deltaTime).
     * Maneja invencibilidad, animación de sonido y frenado.
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        if (invencibleTime > 0) invencibleTime -= delta;
        else if (invencible) {
            setColor(color);
            invencible = false;
        }

        //coinSound.update(delta);

        Vector2 velocity = body.getLinearVelocity();
        if (getCurrentStateType() == StateType.DASH || getCurrentStateType() == StateType.STUN) return;
        if (!Gdx.input.isKeyPressed(PlayerControl.LEFT) && !Gdx.input.isKeyPressed(PlayerControl.RIGHT)) {
            body.applyForce(-velocity.x * brakeForce * delta, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
        }
    }
}

    /**
     * Reduce puntos al jugador (en forma de monedas) y lanza dichas monedas.
     * @param amount Cantidad de puntos a perder
     */

    /**
     * Elimina el poder actual y lo lanza al mundo como entidad.
     */
//    public void dropPower(){
//        if (currentpowerUptype == PowerUp.Type.NONE) return;
//        switch (currentpowerUptype){
//            case BOMB -> game.addEntity(Type.POWERBOMB, body.getPosition(), new Vector2(random.nextFloat(-3,3),random.nextFloat(-5,5)));
//            case WHEEL -> game.addEntity(Type.POWERWHEEL, body.getPosition(), new Vector2(random.nextFloat(-3,3),random.nextFloat(-5,5)));
//            case SWORD -> game.addEntity(Type.POWERSWORD, body.getPosition(), new Vector2(random.nextFloat(-3,3),random.nextFloat(-5,5)));
//        }
//        setCurrentPowerUp(PowerUp.Type.NONE);
//    }

    /**
     * Ejecuta la acción principal del jugador dependiendo del estado o poder.
     */
//    public void doAction(){
//        if (currentpowerUptype != PowerUp.Type.NONE) {
//            PowerUp power = getCurrentPowerUp();
//            if (getCurrentStateType() == StateType.IDLE || getCurrentStateType() == StateType.WALK ) power.actionIdle();
//            else if (getCurrentStateType() == StateType.RUN) power.actionMove();
//            else if (getCurrentStateType() == StateType.JUMP || getCurrentStateType() == StateType.FALL) power.actionAir();
//        }
//        else if (powerAbsorded == null) setCurrentState(Player.StateType.ABSORB);
//        else setCurrentState(Player.StateType.STAR);
//    }

    /**
     * Lanza una entidad desde la posición del jugador con una fuerza en X e Y.
     * @param type Tipo de entidad a lanzar
     * @param impulseX Velocidad en X
     * @param impulseY Velocidad en Y
     */
//    public void throwEntity(Entity.Type type, Float impulseX, Float impulseY){
//        float linearX = Math.abs(body.getLinearVelocity().x);
//        game.addEntity(type,
//            new Vector2( body.getPosition().add(isFlipX() ? -2.2f : 1.2f,-0.5f)),
//            new Vector2((isFlipX() ? -impulseX - linearX : impulseX + linearX),impulseY),
//            isFlipX()
//        );
//    }

    /**
     * Lanza una entidad en la dirección especificada con fuerza determinada.
     * @param type Tipo de entidad
     * @param impulse Magnitud de impulso
     * @param direction Dirección de lanzamiento
     */
//    public void throwEntity(Entity.Type type, Float impulse, ThrowDirection direction){
//        float linearX = Math.abs(body.getLinearVelocity().x);
//        Vector2 spawnPos = new Vector2( switch (direction) {
//            case LEFT -> body.getPosition().add(-2.2f,-0.5f);
//            case RIGHT -> body.getPosition().add(1.2f,-0.5f);
//            case UP -> body.getPosition().add(0,1.7f);
//            case DOWN -> body.getPosition().add(0,-1.7f);
//        });
//        Vector2 impulseVector = switch (direction) {
//            case LEFT -> new Vector2(-impulse + linearX,0);
//            case RIGHT -> new Vector2(impulse + linearX,0);
//            case UP -> new Vector2(0,impulse);
//            case DOWN -> new Vector2(0,-impulse);
//        };
//        game.addEntity(type,
//            spawnPos,
//            impulseVector,
//            direction == ThrowDirection.LEFT
//        );
//    }

    /**
     * Lógica de colisión con otros actores del mundo.
     * @param actor Actor con el que colisiona
     * @param game Pantalla actual del juego
     */
//    @Override
//    public void beginContactWith(ActorBox2d actor, GameScreen game) {
//        if (actor instanceof Enemy enemy) {
//            if (getCurrentStateType() == StateType.ABSORB){
//                powerAbsorded = enemy.getPowerType();
//                game.removeEntity(enemy.getId());
//                setCurrentState(Player.StateType.IDLE);
//                return;
//            }
//
//            if (getCurrentStateType() == StateType.DASH && enemy.getCurrentStateType() != Enemy.StateType.DAMAGE){
//                Box2dUtils.knockbackBody(body, enemy.getBody(), 5f);
//                game.actDamageEnemy(enemy.getId(), body, dashDamage, 2f);
//                setInvencible(0.5f);
//                setCurrentState(StateType.FALL);
//                return;
//            }
//
//            if (getCurrentStateType() == StateType.STUN || invencible || enemy.getCurrentStateType() == Enemy.StateType.DAMAGE) return;
//            setCurrentState(Player.StateType.STUN);
//            playSound(SoundType.NORMALDAMAGE);
//            Box2dUtils.knockbackBody(body, enemy.getBody(), 10f);
//
//        } else if (actor instanceof Mirror m) {
//            game.threadSecureWorld.addModification(() -> {
//                game.playMinigame();
//                game.randomMirror(m.getId());
//            });
//        } else if (actor instanceof PowerItem power){
//            if (getCurrentStateType() == StateType.STUN || invencible || getCurrentStateType() != StateType.ABSORB) return;
//            powerAbsorded = power.getPowerType();
//            setCurrentState(Player.StateType.IDLE);
//            power.despawn();
//        } else if (actor instanceof CoinOdsPoint coin){
//            if (getCurrentStateType() == StateType.STUN || invencible) return;
//            playSound(SoundType.COIN);
//            coin.despawn();
//            game.setScore(game.getScore() + 1);
//        } else if (actor instanceof Spike spike) {
//            if (getCurrentStateType() == StateType.STUN || invencible) return;
//            stunTime = 0.5f;
//            setCurrentState(Player.StateType.STUN);
//            playSound(SoundType.NORMALDAMAGE);
//            Box2dUtils.knockbackBody(body, spike.getBody(), 10f);
//        } else if (actor instanceof Lava lava) {
//            if (getCurrentStateType() == StateType.STUN || invencible) return;
//            stunTime = 0.5f;
//            setCurrentState(Player.StateType.STUN);
//            playSound(SoundType.FIREDAMAGE);
//            Box2dUtils.knockbackBody(body, lava.getBody(), 10f);
//        }else if (actor instanceof OtherPlayer other){
//            if (getCurrentStateType() == StateType.STUN || invencible || other.getCurrentStateType() != StateType.DASH) return;
//            setCurrentState(Player.StateType.STUN);
//            Box2dUtils.knockbackBody(body, other.getBody(), 10f);
//        }
//    }
//
//}



