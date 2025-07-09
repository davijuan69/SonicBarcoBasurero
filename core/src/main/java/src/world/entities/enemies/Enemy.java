package src.world.entities.enemies; // Declara el paquete donde se encuentra la clase Enemy.

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import src.screens.game.GameScreen;
import src.utils.stateMachine.StateMachine;
import src.world.ActorBox2d;
import src.world.entities.Entity;

// Clase abstracta Enemy que extiende de Entity, sirviendo como base para todos los tipos de enemigos en el juego.
public abstract class Enemy extends Entity {
    private Float actCrono; // Un cronómetro o acumulador de tiempo para controlar acciones temporizadas.

    protected StateMachine stateMachine; // Máquina de estados que gestiona el comportamiento del enemigo.
    // Enumeración de los diferentes tipos de estados que puede tener un enemigo.
    public enum StateType {
        IDLE, // Estado de reposo o inactividad.
        WALK, // Estado de movimiento.
        ATTACK, // Estado de ataque.
        DAMAGE // Estado al recibir daño.
    }
    private StateType currentStateType; // El tipo de estado actual del enemigo.
    protected StateEnemy<?> idleState; // Instancia del estado de reposo.
    protected StateEnemy<?> walkState; // Instancia del estado de movimiento.
    protected StateEnemy<?> attackState; // Instancia del estado de ataque.
    protected StateEnemy<?> damageState; // Instancia del estado de daño.
    private Boolean changeState; // Bandera para indicar si ha habido un cambio de estado.

    public GameScreen game; // Referencia a la GameScreen para interactuar con el juego.

    public Float speed; // Velocidad de movimiento del enemigo.
    private Integer live; // Puntos de vida del enemigo.

    private Sound damageSound; // Sonido que se reproduce cuando el enemigo recibe daño.
    private Sound deadSound; // Sonido que se reproduce cuando el enemigo es derrotado.

    /**
     * Constructor de la clase Enemy.
     * @param world El mundo Box2D en el que existirá el enemigo.
     * @param shape La forma rectangular que define el cuerpo físico del enemigo.
     * @param assetManager El gestor de recursos para cargar sonidos.
     * @param id Un identificador único para esta instancia de enemigo.
     * @param game La GameScreen asociada.
     * @param type El tipo de entidad de este enemigo (del enum Entity.Type).
     * @param live Los puntos de vida iniciales del enemigo.
     */
    public Enemy(World world, Rectangle shape, AssetManager assetManager, Integer id, GameScreen game, Type type, Integer live) {
        super(world, shape, assetManager,id, type); // Llama al constructor de la clase padre (Entity).
        this.game = game; // Asigna la instancia de GameScreen.

        this.actCrono = 0f; // Inicializa el cronómetro.
        stateMachine = new StateMachine(); // Inicializa la máquina de estados.
        speed = 3f; // Establece la velocidad por defecto.
        changeState = false; // Inicializa la bandera de cambio de estado.
        this.live = live; // Asigna los puntos de vida.

        // Carga los sonidos de daño y muerte del enemigo desde el AssetManager.

    }

    /**
     * Establece el valor del cronómetro de acción.
     * @param actCrono El nuevo valor para el cronómetro.
     */
    public void setActCrono(Float actCrono) {
        this.actCrono = actCrono;
    }

    /**
     * Obtiene el tipo de potenciador que suelta el enemigo.
     * @return El tipo de potenciador.
     */


    /**
     * Obtiene el valor actual del cronómetro de acción.
     * @return El valor del cronómetro.
     */
    public Float getActCrono() {
        return actCrono;
    }

    /**
     * Establece el estado actual del enemigo y actualiza la máquina de estados.
     * @param state El nuevo tipo de estado.
     */
    public void setState(StateType state){
        currentStateType = state; // Actualiza el tipo de estado actual.
        changeState = true; // Marca que ha habido un cambio de estado.
        // Cambia el estado en la máquina de estados según el tipo.
        switch (state){
            case IDLE -> stateMachine.setState(idleState);
            case WALK -> stateMachine.setState(walkState);
            case ATTACK -> stateMachine.setState(attackState);
            case DAMAGE -> stateMachine.setState(damageState);
        }
    }

    /**
     * Obtiene el tipo de estado actual del enemigo.
     * @return El tipo de estado actual.
     */
    public StateType getCurrentStateType() {
        return currentStateType;
    }

    /**
     * Verifica si ha habido un cambio de estado y resetea la bandera.
     * @return true si el estado ha cambiado, false en caso contrario.
     */
    public Boolean checkChangeState() {
        if (changeState) { // Si la bandera está en true.
            changeState = false; // Resetea la bandera a false.
            return true; // Retorna true indicando que hubo un cambio.
        }
        return false; // Retorna false si no hubo cambio.
    }

    /**
     * Lanza una entidad desde la posición del enemigo, ajustando la dirección y velocidad.
     * La velocidad en X de la entidad lanzada se suma a la velocidad actual del enemigo.
     * @param type Tipo de entidad a lanzar.
     * @param impulseX Velocidad inicial en X, se le suma la velocidad lineal X del cuerpo del enemigo.
     * @param impulseY Velocidad inicial en Y.
     */
    public void throwEntity(Type type, Float impulseX, Float impulseY){
        float linearX = Math.abs(body.getLinearVelocity().x); // Obtiene el valor absoluto de la velocidad lineal X del cuerpo.
        game.addEntityNoPacket(type, // Añade una nueva entidad al juego.
            body.getPosition().add(isFlipX() ? -2.2f : 1.2f,-0.5f), // Calcula la posición de lanzamiento ajustada a la dirección del enemigo.
            new Vector2((isFlipX() ? -impulseX - linearX : impulseX + linearX),impulseY), // Calcula el vector de impulso, ajustando por la dirección y velocidad del enemigo.
            isFlipX() // Pasa la dirección de giro (flip) al crear la entidad.
        );
    }



    /**
     * Aplica daño al enemigo.
     * @param damage La cantidad de daño a aplicar.
     */
    public void takeDamage(Integer damage) {
        if (damage <= 0) return; // Si el daño es cero o negativo, no hace nada.
        live -= damage; // Reduce los puntos de vida del enemigo.
        setState(StateType.DAMAGE); // Cambia el estado del enemigo a 'DAMAGE'.
    }

    /**
     * Verifica si el enemigo está muerto.
     * @return true si los puntos de vida son menores o iguales a cero, false en caso contrario.
     */
    public Boolean isDead(){
        return live <= 0;
    }

    /**
     * Método de actualización del enemigo, llamado cada fotograma.
     * @param delta El tiempo transcurrido desde el último fotograma.
     */
    @Override
    public void act(float delta) {
        actCrono += delta; // Incrementa el cronómetro de acción.
        stateMachine.update(delta); // Actualiza el estado actual de la máquina de estados.
    }

    /**
     * Método llamado cuando este enemigo entra en contacto con otro ActorBox2d.
     * @param actor El otro ActorBox2d con el que se contactó.
     * @param game La GameScreen actual.
     */
    @Override
    public void beginContactWith(ActorBox2d actor, GameScreen game) {

    }
}
