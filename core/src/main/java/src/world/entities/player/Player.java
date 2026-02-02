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
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.Eggman.Eggman;
import src.world.entities.items.EndRing;
import src.world.entities.items.Item;
import src.world.entities.items.Mount;
import src.world.entities.items.Rings;
import src.world.entities.player.states.*;
import java.util.ArrayList;
import java.util.Random;

public class Player extends PlayerCommon {
    // Sistema de salud
    private int maxHealth = 3;
    private int currentHealth = 3;
    private int coinCount = 0;
    public static final int COINS_FOR_EXTRA_LIFE = 10;
    
    // Cantidad de monedas que suelta al recibir daño
    public Integer coinDrop = 3;
    public static final Integer DEFAULT_COIN_DROP = 3;

    // Pantalla principal del juego que gestiona este jugador
    public final GameScreen game;

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
        if (getCurrentStateType() == StateType.RUN || PlayerControl.isRunPressed()) return;
        if (!Gdx.input.isKeyPressed(PlayerControl.LEFT) && !Gdx.input.isKeyPressed(PlayerControl.RIGHT)) {
            // No frenar si está en animación MAXSPEED o si la velocidad es mayor a 9.0f
            if (getCurrentAnimationType() != AnimationType.MAXSPEED && Math.abs(velocity.x) < 9.0f) {
                body.applyForce(-velocity.x * brakeForce * delta, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
            }
        }
    }
    public void beginContactWith(ActorBox2d actor, GameScreen game) {
        if (actor instanceof Enemy enemy) {
            if((getCurrentStateType() == StateType.FALL) && (enemy instanceof Eggman)){
                Box2dUtils.knockbackBody(body, enemy.getBody(), 5f);
                enemy.takeDamage(1);
                enemy.setState(Enemy.StateType.DAMAGE);
            }
            else if (getCurrentStateType() == StateType.FALL) {
                enemy.throwEntity(Type.RING,0f,2f);
                game.removeEntity(enemy.getId());
                setCurrentState(Player.StateType.IDLE);
                return;
            }
            if (getCurrentStateType() == StateType.DASH && enemy.getCurrentStateType() != Enemy.StateType.DAMAGE) {
                Box2dUtils.knockbackBody(body, enemy.getBody(), 5f);
                game.actDamageEnemy(enemy.getId(), body, dashDamage, 2f);
                setInvencible(0.5f);
                setCurrentState(StateType.FALL);
                return;
            }

            if (getCurrentStateType() == StateType.STUN || invencible || enemy.getCurrentStateType() == Enemy.StateType.DAMAGE)
                return;
            
            // Aplicar daño al jugador
            boolean playerDied = takeDamage(1);
            
            if (playerDied) {
                // El jugador murió, manejar la lógica de muerte
                return;
            }
            
            setCurrentState(Player.StateType.STUN);
            Box2dUtils.knockbackBody(body, enemy.getBody(), 10f);
            setCurrentState(StateType.FALL);

        } else if (actor instanceof Rings coin) {
            addCoin();
            coin.despawn();
        }
        else if(actor instanceof Mount mount){
            mount.throwEntity(Type.RING, 0f,4f);
            mount.despawn();
        }
        else if(actor instanceof EndRing ring){
            game.endGame();
        }
    }
    
    // ===== MÉTODOS DEL SISTEMA DE SALUD =====
    
    /**
     * Obtiene la vida máxima del jugador.
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Obtiene la vida actual del jugador.
     */
    public int getCurrentHealth() {
        return currentHealth;
    }
    
    /**
     * Obtiene el porcentaje de vida actual (0.0 a 1.0).
     */
    public float getHealthPercentage() {
        return (float) currentHealth / maxHealth;
    }
    
    /**
     * Obtiene el número de monedas recolectadas.
     */
    public int getCoinCount() {
        return coinCount;
    }
    
    /**
     * Obtiene el progreso hacia la siguiente vida extra (0.0 a 1.0).
     */
    public float getExtraLifeProgress() {
        return (float) coinCount / COINS_FOR_EXTRA_LIFE;
    }
    
    /**
     * Añade monedas al jugador. Si alcanza 10 monedas, gana una vida extra.
     */
    public void addCoins(int amount) {
        coinCount += amount;
        if (coinCount >= COINS_FOR_EXTRA_LIFE) {
            addHealth(1);
            coinCount = 0; // Resetear contador de monedas
        }
    }
    
    /**
     * Añade vida al jugador.
     */
    public void addHealth(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }
    
    /**
     * Reduce la vida del jugador por daño.
     * @return true si el jugador murió, false si sobrevivió
     */
    public boolean takeDamage(int damage) {
        if (invencible) return false; // No recibir daño si es invencible
        
        currentHealth = Math.max(currentHealth - damage, 0);
        if (currentHealth <= 0) {
            // El jugador ha muerto
            die();
            return true;
        } else {
            // Hacer invencible por un tiempo después del daño
            setInvencible(2.0f);
            return false;
        }
    }
    
    /**
     * Maneja la muerte del jugador.
     */
    private void die() {
        System.out.println("¡El jugador ha muerto!");
        game.main.setScreen(new src.screens.uiScreens.MenuScreen(game.main));
    }

    // Llama a este método cuando el jugador recolecta una moneda
    public void addCoin() {
        coinCount++;
        if (coinCount >= COINS_FOR_EXTRA_LIFE) {
            coinCount = 0;
            if (currentHealth < maxHealth) currentHealth++;
        }
    }

    public void resetStats() {
        currentHealth = maxHealth;
        coinCount = 0;
    }
}

