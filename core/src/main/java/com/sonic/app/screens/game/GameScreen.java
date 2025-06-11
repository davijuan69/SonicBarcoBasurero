package com.sonic.app.screens.game; // Declara el paquete donde se encuentra esta clase.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sonic.app.main.Main;
import com.sonic.app.utils.ThreadSecureWorld;
import src.net.packets.Packet;
import com.sonic.app.screens.components.LayersManager;
import com.sonic.app.screens.components.PowerView;
import com.sonic.app.screens.components.chat.Chat;
import com.sonic.app.screens.game.gameLayers.GameLayerManager;
import com.sonic.app.screens.uiScreens.UIScreen;
import com.sonic.app.utils.ScorePlayer;
import com.sonic.app.utils.SecondsTimer;
import com.sonic.app.utils.ThreadSecureWorld;
import com.sonic.app.utils.constants.ConsoleColor;
import com.sonic.app.utils.indicators.BorderIndicator;
import com.sonic.app.utils.indicators.IndicatorManager;
import com.sonic.app.utils.managers.CameraShakeManager;
import com.sonic.app.utils.managers.SpawnManager;
import com.sonic.app.utils.managers.TiledManager;
import com.sonic.app.utils.sound.SingleSoundManager;
import com.sonic.app.world.ActorBox2d;
import com.sonic.app.world.entities.Entity;
import com.sonic.app.world.entities.EntityFactory;
import com.sonic.app.world.entities.NoAutoPacketEntity;
import com.sonic.app.world.entities.blocks.Block;
import com.sonic.app.world.entities.blocks.BreakBlock;
import com.sonic.app.world.entities.enemies.Enemy;
import com.sonic.app.world.entities.mirror.Mirror;
import com.sonic.app.world.entities.otherPlayer.OtherPlayer;
import com.sonic.app.world.entities.player.Player;
import com.sonic.app.world.entities.player.PlayerCommon;
import com.sonic.app.world.entities.player.powers.PowerUp;
import com.sonic.app.world.particles.ParticleFactory;
import com.sonic.app.world.statics.StaticFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import static com.sonic.app.utils.constants.Constants.TIME_MINUTES_GAME;

/**
 * La clase `GameScreen` es la pantalla principal del juego.
 * Gestiona la lógica del juego, la renderización, la interacción con el mundo físico (Box2D),
 * la interfaz de usuario (Scene2d), la red y la gestión de entidades y sonidos.
 */
public class GameScreen extends UIScreen { //HOLAAAA, hay que modificar esta clase (super importante)
    // --- Atributos del Juego ---
    private final Stage stage; // El Stage principal para los actores del juego (jugadores, enemigos, etc.).
    private final World world; // El mundo físico de Box2D donde tienen lugar las simulaciones.
    public ThreadSecureWorld threadSecureWorld; // Un envoltorio para el mundo de Box2D para permitir modificaciones seguras para hilos.
    private Boolean isLoad; // Bandera para indicar si la pantalla del juego se ha cargado completamente.

    // --- Atributos del Mapa Tiled ---
    private final OrthogonalTiledMapRenderer tiledRenderer; // Renderizador para el mapa Tiled.
    private final TiledManager tiledManager; // Gestor para cargar y manipular el mapa Tiled.

    // --- Fábricas ---
    public final EntityFactory entityFactory; // Fábrica para crear diferentes tipos de entidades dinámicas.
    public final StaticFactory staticFactory; // Fábrica para crear objetos estáticos (como bloques).
    public final ParticleFactory particleFactory; // Fábrica para crear efectos de partículas.

    // --- Entidades del Juego ---
    private Player player; // La instancia del jugador principal.
    private final ArrayList<ActorBox2d> actors; // Una lista de todos los `ActorBox2d` en el Stage.
    private final HashMap<Integer, Entity> entities; // Un mapa que almacena todas las entidades del juego por su ID.

    // --- Atributos de Red ---
    private final Vector2 lastPosition; // La última posición conocida del jugador para la sincronización de red.
    private Float sendTime; // Temporizador para controlar la frecuencia de envío de paquetes de red.
    private final SecondsTimer timeGame; // Un temporizador para la duración total del juego.
    private final HashMap<Integer, ScorePlayer> scorePlayers; // Un mapa que almacena las puntuaciones de todos los jugadores (locales y remotos).

    // --- Atributos de Aparición (Spawn) ---
    private final Random random; // Generador de números aleatorios.
    public Vector2 lobbyPlayer; // Posición de aparición para el jugador en el lobby/área segura.
    public SpawnManager spawnMirror; // Gestor de puntos de aparición para los espejos.
    public ArrayList<Vector2> spawnPlayer; // Lista de posibles puntos de aparición para el jugador.

    // --- Interfaz de Usuario (UI) en el Juego ---
    private final CameraShakeManager cameraShakeManager; // Gestor para aplicar efectos de temblor a la cámara.
    private IndicatorManager mirrorIndicators; // Gestor para indicadores visuales que apuntan a los espejos.
    private BorderIndicator maxScoreIndicator; // Indicador visual que apunta al jugador con la puntuación más alta.
    private Integer idTargetMaxScore; // El ID del jugador con la puntuación más alta.

    private LayersManager layersManager; // Gestor de capas para organizar la UI del juego.
    private Label odsPointsLabel; // Etiqueta UI para mostrar los puntos de ODS (Objetivos de Desarrollo Sostenible).
    private Label gameTimeLabel; // Etiqueta UI para mostrar el tiempo restante del juego.
    private Chat chat; // Componente de chat para la comunicación en el juego.
    private PowerView imagePower; // Componente UI para mostrar el poder actual del jugador.

    private GameLayerManager gameLayerManager; // Gestor de capas específico para el juego (ej. menú de pausa).

    // --- Sonidos ---
    private Sound mirrorChangeSound; // Sonido que se reproduce cuando un espejo cambia de posición.

    // --- Depuración ---
    private final Box2DDebugRenderer debugRenderer; // Renderizador para dibujar las formas y cuerpos de Box2D (para depuración).

    /**
     * Constructor de la clase `GameScreen`.
     * Inicializa todos los componentes del juego, el mundo físico, las fábricas, los gestores de UI, etc.
     *
     * @param main La instancia principal de la aplicación `Main`.
     */
    public GameScreen(Main main){
        super(main); // Llama al constructor de la clase padre `UIScreen`.

        actors = new ArrayList<>(); // Inicializa la lista de `ActorBox2d`.
        entities = new HashMap<>(); // Inicializa el mapa de entidades.

        entityFactory = new EntityFactory(this); // Crea la fábrica de entidades.
        staticFactory = new StaticFactory(this); // Crea la fábrica de estáticos.
        particleFactory = new ParticleFactory(); // Crea la fábrica de partículas.

        stage = new Stage(new ScreenViewport()); // Crea el Stage principal con un viewport que se ajusta a la pantalla.
        world = new World(new Vector2(0, -30f), true); // Crea el mundo de Box2D con gravedad hacia abajo.
        threadSecureWorld = new ThreadSecureWorld(world); // Envuelve el mundo de Box2D para seguridad de hilos.

        tiledManager = new TiledManager(this); // Crea el gestor del mapa Tiled.
        tiledRenderer = tiledManager.setupMap("tiled/maps/gameMap.tmx"); // Configura y carga el mapa.

        world.setContactListener(new GameContactListener(this)); // Establece un listener de contacto para Box2D.
        lastPosition = new Vector2(); // Inicializa la última posición del jugador.
        sendTime = 0f; // Inicializa el temporizador de envío de paquetes.
        scorePlayers = new HashMap<>(); // Inicializa el mapa de puntuaciones de jugadores.
        timeGame = new SecondsTimer(TIME_MINUTES_GAME, 0); // Inicializa el temporizador del juego.

        random = new Random(); // Inicializa el generador de números aleatorios.
        spawnMirror = new SpawnManager(); // Inicializa el gestor de aparición de espejos.
        spawnPlayer = new ArrayList<>(); // Inicializa la lista de puntos de aparición de jugadores.

        idTargetMaxScore = -1; // Inicializa el ID del jugador con la puntuación máxima (ninguno al principio).

        initSounds(); // Carga los sonidos del juego.

        debugRenderer = new Box2DDebugRenderer(); // Inicializa el renderizador de depuración de Box2D.

        cameraShakeManager = new CameraShakeManager((OrthographicCamera) stage.getCamera()); // Inicializa el gestor de temblor de cámara.
        isLoad = false; // La pantalla aún no se ha cargado completamente.
    }

    /**
     * Carga los sonidos específicos de esta pantalla.
     */
    private void initSounds(){
        mirrorChangeSound = main.getAssetManager().get("sound/portalChange.wav", Sound.class); // Carga el sonido de cambio de espejo.
    }

    /**
     * Establece la puntuación del jugador local (ID -1) y envía un paquete de red para sincronizarla.
     *
     * @param score La nueva puntuación del jugador.
     */
    public void setScore(Integer score) {
        scorePlayers.get(-1).score = score; // Actualiza la puntuación del jugador local.
        sendPacket(Packet.actScore(-1, score)); // Envía la puntuación actualizada a través de la red.
    }

    /**
     * Obtiene la puntuación actual del jugador local.
     *
     * @return La puntuación del jugador local, o `null` si no se ha inicializado.
     */
    public Integer getScore() {
        ScorePlayer scorePlayer = scorePlayers.get(-1); // Obtiene el objeto ScorePlayer para el jugador local.
        if (scorePlayer != null) return scorePlayers.get(-1).score; // Si existe, devuelve su puntuación.
        else return null; // Si no, devuelve null.
    }

    /**
     * Obtiene el mapa de todas las puntuaciones de los jugadores.
     *
     * @return Un `HashMap` donde la clave es el ID del jugador y el valor es su `ScorePlayer`.
     */
    public HashMap<Integer, ScorePlayer> getScorePlayers() {
        return scorePlayers;
    }

    /**
     * Obtiene la instancia del mundo de Box2D.
     *
     * @return El objeto `World` de Box2D.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Obtiene la instancia del jugador principal.
     *
     * @return El objeto `Player` principal.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Obtiene el mapa de todas las entidades en el juego.
     *
     * @return Un `HashMap` donde la clave es el ID de la entidad y el valor es el objeto `Entity`.
     */
    public HashMap<Integer, Entity> getEntities() {
        return entities;
    }

    /**
     * Obtiene la instancia del componente de chat.
     *
     * @return El objeto `Chat`.
     */
    public Chat getChat() {
        return chat;
    }

    /**
     * Añade el jugador principal al juego.
     * Si el jugador ya existe, no hace nada.
     * Inicializa el jugador en un punto de aparición aleatorio y configura su `ScorePlayer`.
     */
    public void addMainPlayer(){
        if (player != null) return; // Si el jugador ya existe, sale del método.

        // Selecciona un punto de aparición aleatorio para el jugador.
        int index = random.nextInt(spawnPlayer.size());
        Vector2 position = new Vector2(spawnPlayer.get(index));

        // Crea una nueva instancia del jugador.
        player = new Player(world, position.x, position.y, main.getAssetManager(), this, main.playerColor);
        stage.addActor(player); // Añade el jugador al Stage.

        // Inicializa el ScorePlayer para el jugador local.
        if (main.client == null) scorePlayers.put(-1, new ScorePlayer(-1,"TU")); // Si no hay cliente (modo single-player), usa "TU".
        else scorePlayers.put(-1, new ScorePlayer(-1,main.client.getName())); // Si hay cliente, usa el nombre del cliente.
    }

    /**
     * Añade un objeto estático al mundo de Box2D.
     * La modificación se encola para ser aplicada de forma segura para hilos.
     *
     * @param type El tipo de objeto estático a crear.
     * @param bounds Los límites rectangulares del objeto estático.
     */
    public void addStatic(StaticFactory.Type type, Rectangle bounds){
        threadSecureWorld.addModification(() -> { // Encola la modificación.
            ActorBox2d actorBox2d = staticFactory.create(type, world, bounds); // Crea el objeto estático.
            addActor(actorBox2d); // Añade el ActorBox2d al Stage y a las listas de gestión.
        });
    }

    /**
     * Añade un `Actor` al `Stage` y, si es una `Entity` o `ActorBox2d`, lo gestiona en las colecciones internas.
     * También gestiona indicadores para `Mirror` y `OtherPlayer`.
     *
     * @param actor El `Actor` a añadir.
     */
    public void addActor(Actor actor){
        if (actor instanceof Entity e) entities.put(e.getId(), e); // Si es una Entidad, la añade al mapa de entidades.
        if (actor instanceof ActorBox2d a) actors.add(a); // Si es un ActorBox2d, lo añade a la lista de actores.
        if (actor instanceof OtherPlayer o) scorePlayers.put(o.getId(), new ScorePlayer(o.getId(),o.getName())); // Si es otro jugador, inicializa su puntuación.
        if (actor instanceof Mirror m) mirrorIndicators.add(m.getId(),m.getBody().getPosition()); // Si es un espejo, añade un indicador.

        stage.addActor(actor); // Añade el actor al Stage.
    }

    /**
     * Lógica interna para crear una entidad. Esta función es llamada por los métodos `addEntity*`.
     * Verifica si la entidad ya existe y la encola para su creación segura en el mundo.
     *
     * @param type El tipo de entidad a crear.
     * @param position La posición inicial de la entidad.
     * @param force La fuerza lineal inicial a aplicar a la entidad.
     * @param id El ID único de la entidad.
     * @param flipX Si la entidad debe estar volteada horizontalmente.
     */
    private void createEntityLogic(Entity.Type type, Vector2 position, Vector2 force, Integer id, Boolean flipX){
        if (entities.get(id) != null) { // Verifica si una entidad con el mismo ID ya existe.
            System.out.println(ConsoleColor.RED + "Entity " + type + ":" + id + " ya existe en la lista" + ConsoleColor.RESET);
            return;
        }
        //System.out.println("Creando Entidad " + id + " Tipo: " + type); // Mensaje de depuración.
        threadSecureWorld.addModification(() -> { // Encola la modificación para que sea segura para hilos.
            Entity newEntity = entityFactory.create(type, world, position, id); // Crea la nueva entidad.
            newEntity.setFlipX(flipX); // Establece el volteo horizontal.
            newEntity.getBody().applyLinearImpulse(force, newEntity.getBody().getWorldCenter(), true); // Aplica una fuerza inicial.
            addActor(newEntity); // Añade la nueva entidad al Stage y a las listas de gestión.
        });
    }

    /**
     * Añade una entidad al juego sin enviar un paquete de red.
     * Utiliza un ID proporcionado y lo registra en el `Main` para la gestión de IDs.
     *
     * @param type El tipo de entidad.
     * @param position La posición.
     * @param force La fuerza inicial.
     * @param id El ID de la entidad.
     * @param flipX Si debe estar volteada.
     */
    public void addEntityNoPacket(Entity.Type type, Vector2 position, Vector2 force, Integer id, Boolean flipX){
        createEntityLogic(type, position, force, id, flipX); // Llama a la lógica de creación.
        main.setIds(id); // Registra el ID en la clase `Main`.
    }

    /**
     * Añade una entidad al juego sin enviar un paquete de red.
     * Obtiene un nuevo ID automáticamente del `Main`.
     *
     * @param type El tipo de entidad.
     * @param position La posición.
     * @param force La fuerza inicial.
     * @param flipX Si debe estar volteada.
     */
    public void addEntityNoPacket(Entity.Type type, Vector2 position, Vector2 force, Boolean flipX){
        int id = main.getIds(); // Obtiene un nuevo ID.
        createEntityLogic(type, position, force, id, flipX); // Llama a la lógica de creación.
    }

    /**
     * Añade una entidad al juego y envía un paquete de red para sincronizar su creación.
     * Obtiene un nuevo ID automáticamente del `Main`.
     *
     * @param type El tipo de entidad.
     * @param position La posición.
     * @param force La fuerza inicial.
     * @param flipX Si debe estar volteada.
     */
    public void addEntity(Entity.Type type, Vector2 position, Vector2 force, Boolean flipX){
        int id = main.getIds(); // Obtiene un nuevo ID.
        createEntityLogic(type, position, force, id, flipX); // Llama a la lógica de creación.
        sendPacket(Packet.newEntity(id, type, position.x, position.y, force.x, force.y, flipX)); // Envía el paquete.
    }

    /**
     * Añade una entidad al juego y envía un paquete de red, sin volteo horizontal.
     *
     * @param type El tipo de entidad.
     * @param position La posición.
     * @param force La fuerza inicial.
     */
    public void addEntity(Entity.Type type, Vector2 position, Vector2 force){
        addEntity(type, position, force, false); // Llama a la versión con `flipX` como `false`.
    }

    /**
     * Añade una entidad a un punto de aparición gestionado por un `SpawnManager` y envía un paquete de red.
     *
     * @param type El tipo de entidad.
     * @param force La fuerza inicial.
     * @param spawnManager El gestor de puntos de aparición a usar.
     */
    public void addEntitySpawn(Entity.Type type, Vector2 force, SpawnManager spawnManager){
        int id = main.getIds(); // Obtiene un nuevo ID.
        Vector2 position = spawnManager.takeSpawnPoint(id); // Toma un punto de aparición del gestor.
        createEntityLogic(type, position, force, id, false); // Llama a la lógica de creación.
        sendPacket(Packet.newEntity(id, type, position.x, position.y, force.x, force.y, false)); // Envía el paquete.
    }

    /**
     * Actualiza la animación, estado y poder de otro jugador (recibido por red).
     *
     * @param id El ID del otro jugador.
     * @param animationType El tipo de animación.
     * @param flipX Si el sprite debe estar volteado.
     * @param stateType El estado del jugador.
     * @param powerType El tipo de poder actual.
     */
    public void actOtherPlayerAnimation(Integer id, Player.AnimationType animationType, Boolean flipX, PlayerCommon.StateType stateType, PowerUp.Type powerType){
        Entity entity = entities.get(id); // Obtiene la entidad por ID.
        if (entity == null) {
            System.out.println("Animation OtherPlayer Entity " + id + " no encontrada en la lista");
            return;
        }
        if (!(entity instanceof OtherPlayer otherPlayer)) { // Verifica que sea un `OtherPlayer`.
            System.out.println("Animation OtherPlayer Entity " + id + " no es un OtherPlayer");
            return;
        }
        otherPlayer.setAnimation(animationType); // Establece la animación.
        otherPlayer.setCurrentState(stateType); // Establece el estado.
        otherPlayer.setCurrentPowerUp(powerType); // Establece el poder.
        otherPlayer.setFlipX(flipX); // Establece el volteo.
    }

    /**
     * Actualiza la posición y velocidad lineal de una entidad (recibido por red).
     * La modificación se encola para ser segura para hilos.
     *
     * @param id El ID de la entidad.
     * @param x La nueva posición X.
     * @param y La nueva posición Y.
     * @param fx La nueva velocidad lineal X.
     * @param fy La nueva velocidad lineal Y.
     */
    public void actEntityPos(Integer id, Float x, Float y, Float fx, Float fy){
        Entity entity = entities.get(id); // Obtiene la entidad.
        if (entity == null) {
            System.err.println("Entity " + id + " no encontrada en la lista para cambiar su posicion");
            return;
        }
        Body body = entity.getBody(); // Obtiene el cuerpo de Box2D de la entidad.
        threadSecureWorld.addModification(() -> { // Encola la modificación.
            body.setTransform(x, y, 0); // Establece la posición.
            body.setLinearVelocity(fx, fy); // Establece la velocidad lineal.
        });
    }

    /**
     * Actualiza el estado de un enemigo (recibido por red).
     *
     * @param id El ID del enemigo.
     * @param state El nuevo estado del enemigo.
     * @param cronno El valor de `actCrono` para sincronización.
     * @param flipX Si el sprite debe estar volteado.
     */
    public void actEnemy(Integer id, Enemy.StateType state, Float cronno, Boolean flipX){
        Enemy enemy = (Enemy) entities.get(id); // Obtiene el enemigo.
        if (enemy == null) {
            System.out.println("Entity " + id + " no encontrada en la lista");
            return;
        }
        if (enemy.getCurrentStateType() == Enemy.StateType.ATTACK) return; // Si está atacando, no sobrescribe.
        enemy.setState(state); // Establece el estado.
        enemy.setFlipX(flipX); // Establece el volteo.
        enemy.setActCrono(cronno); // Sincroniza el cronómetro de acción.
    }

    /**
     * Aplica daño a un enemigo y lo empuja (con sincronización de red).
     *
     * @param receiverId El ID del enemigo que recibe el daño.
     * @param attacker El cuerpo de Box2D del atacante.
     * @param damage La cantidad de daño a aplicar.
     * @param knockback La fuerza de empuje.
     */
    public void actDamageEnemy(Integer receiverId, Body attacker, Integer damage, Float knockback) {
        if (!entities.containsKey(receiverId)) { // Verifica si el enemigo existe.
            System.out.println("Entity " + receiverId + " no encontrada en la lista para actualizar dano");
            return;
        }
        Body receiver = entities.get(receiverId).getBody(); // Obtiene el cuerpo del receptor.
        // Calcula la dirección del empuje.
        Vector2 pushDirection = attacker.getPosition().cpy().sub(receiver.getPosition()).nor();

        actDamageEnemyNoPacket(receiverId, damage, pushDirection.x, pushDirection.y, knockback); // Aplica el daño localmente.

        sendPacket(Packet.actDamageEnemy(receiverId, damage, pushDirection.x, pushDirection.y, knockback)); // Envía el paquete de daño.
    }

    /**
     * Aplica daño a un enemigo y lo empuja (sin sincronización de red).
     * La modificación se encola para ser segura para hilos.
     *
     * @param id El ID del enemigo.
     * @param damage La cantidad de daño.
     * @param forceX La fuerza de empuje en X.
     * @param forceY La fuerza de empuje en Y.
     * @param knockback La magnitud del empuje.
     */
    public void actDamageEnemyNoPacket(Integer id, Integer damage, Float forceX, Float forceY, Float knockback){
        if (!entities.containsKey(id)) { // Verifica si el enemigo existe.
            System.out.println("Entity " + id + " no encontrada en la lista para actualizar dano");
            return;
        }
        Enemy enemy = (Enemy) entities.get(id); // Obtiene el enemigo.
        if (enemy.getCurrentStateType() == Enemy.StateType.DAMAGE) return; // Si ya está en estado de daño, no aplica más.
        threadSecureWorld.addModification(() -> { // Encola la modificación.
            enemy.takeDamage(damage); // Aplica el daño.
            enemy.getBody().setLinearVelocity(0,0); // Detiene el movimiento actual.
            // Aplica la fuerza de empuje.
            enemy.getBody().applyLinearImpulse(forceX* -knockback, forceY* -knockback, enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);
            // Aplica una fuerza adicional hacia arriba para un efecto de "golpe".
            enemy.getBody().applyLinearImpulse(0,knockback, enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);
        });
    }

    /**
     * Actualiza el estado de un bloque (recibido por red).
     *
     * @param id El ID del bloque.
     * @param stateType El nuevo estado del bloque.
     */
    public void actBlock(Integer id, Block.StateType stateType){
        Block block = (Block) entities.get(id); // Obtiene el bloque.
        if (block == null) {
            System.out.println("Entity " + id + " no encontrada en la lista");
            return;
        }
        block.setStateNoPacket(stateType); // Establece el estado del bloque sin enviar paquete.
    }

    /**
     * Actualiza la puntuación de un jugador y gestiona el indicador de la puntuación máxima.
     *
     * @param id El ID del jugador cuya puntuación se actualiza.
     * @param score La nueva puntuación.
     */
    public void actScore(Integer id, Integer score){
        ScorePlayer scorePlayer = scorePlayers.get(id); // Obtiene el ScorePlayer.
        if (scorePlayer == null){
            System.out.println("ScorePlayer " + id + " no encontrada en la lista");
            return;
        }
        scorePlayer.score = score; // Actualiza la puntuación.

        // Encuentra el jugador con la puntuación máxima.
        ScorePlayer maxScorePlayer = scorePlayers.values().stream()
            .max(Comparator.comparingInt(s -> s.score))
            .orElse(null);

        if (!isLoad) return; // Si la pantalla aún no está cargada, no actualiza el indicador.

        // Actualiza la visibilidad y el objetivo del indicador de puntuación máxima.
        if (maxScorePlayer != null && maxScorePlayer.id != -1) {
            idTargetMaxScore = maxScorePlayer.id; // Establece el ID del objetivo.
            maxScoreIndicator.setVisible(true); // Hace visible el indicador.
        }
        else maxScoreIndicator.setVisible(false); // Si no hay jugador con puntuación máxima (o es el jugador local), oculta el indicador.
    }

    /**
     * Cambia el color de una entidad (recibido por red).
     *
     * @param id El ID de la entidad.
     * @param r Componente rojo.
     * @param g Componente verde.
     * @param b Componente azul.
     * @param a Componente alfa.
     */
    public void actEntityColor(Integer id, float r, float g, float b, float a){
        Entity entity = entities.get(id); // Obtiene la entidad.
        if (entity == null) {
            System.out.println("Entity " + id + " no encontrada en la lista para cambio de color");
            return;
        }
        entity.setColor(r,g,b,a); // Establece el color de la entidad.
    }

    /**
     * Elimina un `Actor` del Stage. Si es un `Mirror`, también elimina su indicador.
     *
     * @param actor El `Actor` a eliminar.
     */
    public void removeActor(Actor actor){
        if (actor instanceof Mirror m) mirrorIndicators.remove(m.getId()); // Si es un Mirror, elimina su indicador.
        stage.getActors().removeValue(actor, true); // Elimina el actor del Stage.
    }

    /**
     * Elimina una entidad del juego y envía un paquete de red para sincronizarlo.
     * @param id Id de la entidad a eliminar.
     */
    public void removeEntity(Integer id){
        removeEntityNoPacket(id); // Elimina la entidad localmente.
        sendPacket(Packet.removeEntity(id)); // Envía el paquete de eliminación.
    }

    /**
     * Elimina una entidad del juego sin enviar un paquete de red.
     * La modificación se encola para ser segura para hilos.
     * @param id Id de la entidad a eliminar.
     */
    public void removeEntityNoPacket(Integer id){
        threadSecureWorld.addModification(() -> { // Encola la modificación.
            Entity entity = entities.get(id); // Obtiene la entidad.
            if (entity == null) {
                System.out.println(ConsoleColor.RED + "Entity " + id + " no se pudo eliminar ,no encontrada en la lista" + ConsoleColor.RESET);
                return;
            }
            entities.remove(entity.getId()); // Elimina la entidad del mapa.
            actors.remove(entity); // Elimina la entidad de la lista de actores.
            removeActor(entity); // Elimina el actor del Stage y sus indicadores.
            entity.detach(); // Desvincula el cuerpo de Box2D de la entidad.
        });
    }

    /**
     * Añade un efecto de partícula al juego.
     * La modificación se encola para ser segura para hilos.
     *
     * @param type El tipo de partícula a crear.
     * @param position La posición donde aparecerá la partícula.
     */
    public void addParticle(ParticleFactory.Type type, Vector2 position){
        threadSecureWorld.addModification(() -> { // Encola la modificación.
            addActor(particleFactory.create(type, position, this)); // Crea y añade la partícula como un actor.
        });
    }

    /**
     * Limpia todos los elementos del juego: cuerpos de Box2D, actores de Scene2d, y colecciones internas.
     */
    public void clearAll(){
        for (ActorBox2d actor : actors) actor.detach(); // Desvincula los cuerpos de Box2D de todos los ActorBox2d.
        if (player != null) player.detach(); // Desvincula el cuerpo del jugador si existe.
        player = null; // Elimina la referencia al jugador.

        stage.clear(); // Limpia todos los actores del Stage principal.
        stageUI.clear(); // Limpia todos los actores del Stage de UI.
        actors.clear(); // Limpia la lista de actores.
        entities.clear(); // Limpia el mapa de entidades.
        spawnMirror.clear(); // Limpia los puntos de aparición de espejos.
    }

    /**
     * Finaliza el juego.
     * Limpia todas las modificaciones pendientes, cierra las conexiones de red,
     * limpia el estado del juego, reinicia el temporizador y cambia a la pantalla de fin de juego.
     */
    public void endGame(){
        threadSecureWorld.clearModifications(); // Limpia todas las modificaciones pendientes.
        main.closeClient(); // Cierra el cliente de red.
        main.closeServer(); // Cierra el servidor de red.
        threadSecureWorld.addModification(() -> { // Encola una modificación para limpiar y cambiar de pantalla.
            clearAll(); // Limpia todos los elementos del juego.
            timeGame.resetTimer(); // Reinicia el temporizador del juego.
            main.changeScreen(Main.Screens.ENDGAME); // Cambia a la pantalla de fin de juego.
            isLoad = false; // Marca la pantalla como no cargada.
        });
    }

    /**
     * Inicia un minijuego aleatorio.
     * Mueve al jugador a la posición del lobby y lo pausa.
     */
    public void playMinigame(){
        getPlayer().getBody().setTransform(lobbyPlayer.x, lobbyPlayer.y, 0); // Mueve al jugador al lobby.
        player.setPaused(true); // Pausa al jugador.
        int select = random.nextInt(2); // Selecciona un minijuego aleatorio (0 o 1).

        switch (select){
            case 0:
                main.changeScreen(Main.Screens.MINIFIRE); // Cambia a la pantalla del minijuego de fuego.
                break;
            case 1:
                main.changeScreen(Main.Screens.MINIODSPLEASE); // Cambia a la pantalla del minijuego ODS.
                break;
            /*case 2: // Opción comentada para un minijuego de patos.
                main.changeScreen(Main.Screens.MINIDUCK);
                break;*/
        }
    }

    /**
     * Método del ciclo de vida de la pantalla: `show()`.
     * Se llama cuando esta pantalla se convierte en la pantalla activa.
     * Configura el procesador de entrada, reproduce la música del juego, y si el juego no está cargado,
     * inicializa el mapa, añade el jugador principal y las entidades iniciales.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void show() {
        Gdx.input.setInputProcessor(stageUI); // Establece el Stage de UI como el procesador de entrada principal.
        SingleSoundManager.getInstance().setSoundTracks(Main.SoundTrackType.GAME); // Establece la banda sonora del juego.

        if (player != null) { // Si el jugador ya existe (ej. volviendo de un minijuego).
            player.setPaused(false); // Despausa al jugador.
            threadSecureWorld.addModification(() -> { // Encola una modificación.
                // Mueve al jugador a un punto de aparición aleatorio y detiene su movimiento.
                Vector2 position = spawnPlayer.get(random.nextInt(spawnPlayer.size()));
                player.getBody().setTransform(position.x, position.y, 0);
                player.getBody().setLinearVelocity(0,0);
                player.setCurrentState(Player.StateType.IDLE); // Establece el estado del jugador a inactivo.
            });
        }else{ // Si el juego se está cargando por primera vez.
            tiledManager.makeMap(); // Crea el mapa.
            addMainPlayer(); // Añade el jugador principal.
            setScore(3); // Establece la puntuación inicial.
            initUI(); // Inicializa la interfaz de usuario.
            gameLayerManager.setVisible(false); // Inicialmente oculta el GameLayerManager (menú de pausa).
            if (main.server != null || main.client == null){ // Si es el servidor o modo single-player.
                tiledManager.makeEntities(); // Crea las entidades del mapa.
                addEntitySpawn(Entity.Type.MIRROR, new Vector2(0,0), spawnMirror); // Añade un espejo.
            }
            isLoad = true; // Marca la pantalla como cargada.
        }
    }

    /**
     * Inicializa todos los componentes de la interfaz de usuario para la pantalla del juego.
     * Esto incluye etiquetas de tiempo y puntuación, chat, indicadores de espejos y poder.
     */
    private void initUI(){
        layersManager = new LayersManager(stageUI, 6); // Crea un gestor de capas para la UI.

        // --- UI para el tiempo de juego ---
        Image timeImage = new Image(main.getAssetManager().get("ui/icons/clock.png", Texture.class)); // Icono de reloj.
        gameTimeLabel = new Label(timeGame.toString(), new Label.LabelStyle(main.fonts.briBorderFont, null)); // Etiqueta de tiempo.
        gameTimeLabel.setAlignment(Align.left); // Alinea el texto a la izquierda.
        gameTimeLabel.setFontScale(1); // Escala de la fuente.

        // --- UI para los puntos ODS (puntuación) ---
        Image coinImage = new Image(main.getAssetManager().get("ui/icons/coinIcon.png", Texture.class)); // Icono de moneda.
        coinImage.setScaling(Scaling.fit); // Ajusta el tamaño de la imagen.
        odsPointsLabel = new Label("0", new Label.LabelStyle(main.fonts.briBorderFont, null)); // Etiqueta de puntuación.
        odsPointsLabel.setAlignment(Align.left); // Alinea el texto a la izquierda.
        odsPointsLabel.setFontScale(0.8f); // Escala de la fuente.

        chat = new Chat(new Label.LabelStyle(main.fonts.interFont, null)); // Inicializa el componente de chat.

        // --- Indicadores ---
        mirrorIndicators = new IndicatorManager(main.getAssetManager().get("ui/indicators/mirrorIndicator.png", Texture.class)); // Gestor de indicadores de espejos.
        maxScoreIndicator = new BorderIndicator(main.getAssetManager().get("ui/indicators/maxScoreIndicator.png", Texture.class), new Vector2(0,0)); // Indicador de puntuación máxima.
        maxScoreIndicator.setVisible(false); // Oculta el indicador inicialmente.

        imagePower = new PowerView(main.getAssetManager()); // Vista del poder del jugador.

        stage.addActor(mirrorIndicators); // Añade los indicadores de espejos al Stage.
        stage.addActor(maxScoreIndicator); // Añade el indicador de puntuación máxima al Stage.

        // --- Diseño de la UI con LayersManager ---
        layersManager.setZindex(0); // Selecciona la primera capa (la más baja en z-index si se añaden de atrás hacia adelante).
        layersManager.getLayer().top().pad(10); // Alinea la capa en la parte superior y añade padding.
        layersManager.getLayer().add(timeImage).padRight(5).size(64); // Añade el icono de tiempo.
        layersManager.getLayer().add(gameTimeLabel); // Añade la etiqueta de tiempo.
        layersManager.getLayer().add().expandX(); // Añade una celda vacía que se expande para empujar los elementos a la izquierda.
        layersManager.getLayer().row().padTop(5); // Pasa a la siguiente fila con padding superior.
        layersManager.getLayer().add(coinImage).padRight(5).size(48); // Añade el icono de moneda.
        layersManager.getLayer().add(odsPointsLabel).left(); // Añade la etiqueta de puntos ODS.

        layersManager.setZindex(1); // Selecciona la segunda capa (para el chat).
        layersManager.getLayer().add(chat).grow(); // Añade el chat, haciéndolo crecer para llenar la celda.

        layersManager.setZindex(2); // Selecciona la tercera capa (para el poder).
        layersManager.getLayer().bottom(); // Alinea la capa en la parte inferior.
        layersManager.getLayer().add().expandX(); // Celda vacía que se expande.
        layersManager.getLayer().add(imagePower).width(182).height(50).row(); // Añade la vista del poder.

        gameLayerManager = new GameLayerManager(this, stageUI); // Inicializa el gestor de capas específico del juego.
    }

    /**
     * Ejecuta la lógica del juego en segundo plano.
     * Incluye la gestión del tiempo de juego, la sincronización de red de posiciones de jugadores y entidades,
     * y el paso de la simulación de Box2D.
     *
     * @param delta Tiempo en segundos desde el último renderizado.
     */
    public void actLogic(float delta){
        if (timeGame.isFinished()) endGame(); // Si el temporizador del juego termina, finaliza el juego.

        if (main.client != null){ // Si hay un cliente de red conectado.
            sendTime += delta; // Incrementa el temporizador de envío.

            Vector2 currentPosition = player.getBody().getPosition(); // Obtiene la posición actual del jugador.
            // Si la posición del jugador ha cambiado significativamente, envía un paquete de actualización.
            if (!currentPosition.epsilonEquals(lastPosition, 0.05f)) {
                sendPacket(Packet.actEntityPosition(-1,currentPosition.x, currentPosition.y)); // Envía la nueva posición.
                lastPosition.set(currentPosition); // Actualiza la última posición conocida.
            }

            if (!main.client.isRunning()) endGame(); // Si el cliente no está corriendo, finaliza el juego (desconexión).

            if (main.server != null){ // Si también es el servidor (modo host).
                if (sendTime >= 1f) { // Cada segundo, envía actualizaciones de posición y estado de las entidades.
                    for (Entity e: entities.values()){
                        if (e instanceof NoAutoPacketEntity) continue; // Ignora entidades que no envían paquetes automáticamente.
                        Body body = e.getBody();
                        // Envía la posición y velocidad de la entidad.
                        sendPacket(Packet.actEntityPosition(e.getId(), body.getPosition().x, body.getPosition().y,
                            body.getLinearVelocity().x , body.getLinearVelocity().y));
                        if (!(e instanceof Enemy enemy)) continue; // Si no es un enemigo, continúa.
                        // Si el enemigo está inactivo o en estado de daño, no envía actualizaciones de estado (solo de posición).
                        if (enemy.getCurrentStateType() == Enemy.StateType.IDLE || enemy.getCurrentStateType() == Enemy.StateType.DAMAGE) continue;
                        // Si el enemigo ha cambiado de estado, envía un paquete de actualización de estado.
                        if (enemy.checkChangeState()) sendPacket(Packet.actEnemy(e.getId(), enemy.getCurrentStateType(), enemy.getActCrono(), enemy.isFlipX()));
                    }
                    sendTime = 0f; // Reinicia el temporizador de envío.
                }
            }
        }
        timeGame.update(delta); // Actualiza el temporizador del juego.
        stage.act(); // Actualiza la lógica de todos los actores en el Stage principal.
        threadSecureWorld.step(delta, 6, 2); // Realiza un paso de simulación en el mundo de Box2D y aplica las modificaciones encoladas.
    }

    /**
     * Actualiza los elementos de la interfaz de usuario (UI).
     * Esto incluye el indicador de puntuación máxima, los indicadores de espejos,
     * la puntuación ODS, el tiempo de juego y el icono del poder del jugador.
     */
    private void actUI(){
        // Actualiza el indicador de puntuación máxima si el objetivo existe.
        if (idTargetMaxScore != -1 && entities.containsKey(idTargetMaxScore)) {
            maxScoreIndicator.setTargetPosition(entities.get(idTargetMaxScore).getBody().getPosition());
        }
        else maxScoreIndicator.setVisible(false); // Oculta el indicador si el objetivo no existe.

        maxScoreIndicator.setCenterPosition(player.getBody().getPosition()); // Centra el indicador de puntuación máxima en el jugador.
        mirrorIndicators.setCenterPositions(player.getBody().getPosition()); // Centra los indicadores de espejo en el jugador.

        odsPointsLabel.setText(getScore()); // Actualiza la etiqueta de puntuación ODS con la puntuación actual.
        gameTimeLabel.setText(timeGame.toString()); // Actualiza la etiqueta de tiempo con el tiempo restante.
        imagePower.setPower(player.getCurrentpowerUptype()); // Actualiza el ícono del poder del jugador.
    }

    /**
     * Método del ciclo de vida de la pantalla: `render(float delta)`.
     * Se llama cada fotograma para dibujar el juego y la interfaz de usuario.
     *
     * @param delta El tiempo transcurrido en segundos desde el último fotograma.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f); // Establece el color de fondo de la pantalla.
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Borra la pantalla con el color de fondo.
        if (!isLoad) return; // Si la pantalla no está cargada, no renderiza nada.

        OrthographicCamera camera = (OrthographicCamera) stage.getCamera(); // Cámara del juego.
        OrthographicCamera cameraUI = (OrthographicCamera) stageUI.getCamera(); // Cámara de la UI.

        // Mueve la cámara del juego suavemente para seguir al jugador.
        camera.position.x = MathUtils.lerp(camera.position.x, player.getX() + (player.isFlipX() ? -32 : 32), 0.10f);
        camera.position.y = MathUtils.lerp(camera.position.y, player.getY(), 0.3f);

        // Sincroniza la posición de la cámara de la UI con la cámara del juego.
        cameraUI.position.x = camera.position.x;
        cameraUI.position.y = camera.position.y;

        cameraShakeManager.update(delta); // Actualiza el gestor de temblor de cámara.
        camera.update(); // Actualiza la cámara del juego.
        cameraUI.update(); // Actualiza la cámara de la UI.

        layersManager.setCenterPosition(camera.position.x, camera.position.y); // Centra las capas de UI con la cámara del juego.
        gameLayerManager.setCenterPosition(cameraUI.position.x, cameraUI.position.y); // Centra las capas del menú de juego.
        tiledRenderer.setView(camera); // Establece la vista del renderizador de mapas Tiled a la cámara del juego.

        tiledRenderer.render(); // Renderiza el mapa Tiled.
        actUI(); // Actualiza y prepara los elementos de la UI para el dibujo.
        stage.draw(); // Dibuja todos los actores del Stage principal.
        stageUI.draw(); // Dibuja todos los actores del Stage de UI.
        //debugRenderer.render(world, camera.combined.scale(PIXELS_IN_METER, PIXELS_IN_METER, 1)); // Renderiza las formas de Box2D para depuración (comentado).

        // Manejo de la entrada del teclado (tecla ESCAPE para mostrar/ocultar el menú de pausa).
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameLayerManager.setVisibleWithSound(!gameLayerManager.isVisible()); // Alterna la visibilidad del menú de pausa.
        }

        // Lógica de interacción del jugador con bloques rompibles.
        for (ActorBox2d actor : actors) { // Itera sobre todos los ActorBox2d.
            if (actor instanceof BreakBlock breakBlock) { // Si el actor es un BreakBlock.
                if (player.getCurrentStateType() == Player.StateType.DASH) { // Y el jugador está en estado de DASH.
                    // Y el rectángulo delimitador del jugador se superpone con el del bloque rompible.
                    if (player.getSprite().getBoundingRectangle().overlaps(breakBlock.getSprite().getBoundingRectangle())) {
                        breakBlock.setState(BreakBlock.StateType.BREAK); // Marca el bloque para romperse.
                    }
                }
            }
        }

        actLogic(delta); // Ejecuta la lógica del juego (actualizaciones, red, Box2D).
    }

    /**
     * Método del ciclo de vida de la pantalla: `resize(int width, int height)`.
     * Se llama cuando la ventana del juego cambia de tamaño. Ajusta el viewport de las cámaras y la posición inicial.
     *
     * @param width El nuevo ancho de la ventana.
     * @param height La nueva altura de la ventana.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void resize(int width, int height) {
        // Ejecuta el redimensionamiento en el hilo principal de la aplicación para evitar problemas de concurrencia.
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                float cameraZoom = 1280.0f / width; // Calcula un factor de zoom inicial basado en el ancho.
                if (cameraZoom > 1.3f) cameraZoom = 1.3f; // Limita el zoom máximo.
                OrthographicCamera camera = (OrthographicCamera) stage.getCamera(); // Obtiene la cámara del juego.
                camera.zoom = cameraZoom; // Aplica el zoom a la cámara.

                stage.getViewport().update(width, height, false); // Actualiza el viewport del Stage principal.
                stageUI.getViewport().update(width, height, false); // Actualiza el viewport del Stage de UI.

                if (player == null) return; // Si el jugador no existe, sale (aún no se ha cargado el juego).

                // Centra la cámara en el jugador después del redimensionamiento.
                camera.position.x = player.getX() + (player.isFlipX() ? -32 : 32);
                camera.position.y = player.getY();
            }
        });
    }

    /**
     * Reubica un espejo a un nuevo punto de aparición aleatorio y reproduce un sonido.
     * Si está en modo multijugador, envía un paquete de sincronización.
     * También provoca la reaparición de los enemigos.
     *
     * @param id El ID del espejo a reubicar.
     */
    public void randomMirror(Integer id){
        Vector2 position = spawnMirror.reSpawn(id); // Obtiene una nueva posición de aparición para el espejo.
        mirrorChangeSound.play(); // Reproduce el sonido de cambio de espejo.
        if (main.client != null) sendPacket(Packet.message("Servidor",main.client.getName() + " ha entrado a un espejo.")); // Envía un mensaje de chat si hay cliente.

        actEntityPos(id, position.x, position.y, 0f, 0f); // Actualiza la posición del espejo (localmente).
        sendPacket(Packet.actEntityPosition(id, position.x, position.y)); // Envía la actualización de posición a la red.
        mirrorIndicators.changeTargetPosition(id ,position); // Actualiza la posición objetivo del indicador del espejo.

        respawnEnemy(); // Hace que los enemigos reaparezcan.
    }

    /**
     * Envía un paquete de red al servidor (si el cliente está conectado).
     *
     * @param packet El array de objetos que representa el paquete a enviar.
     */
    public void sendPacket(Object[] packet) {
        if (main.client != null) main.client.send(packet); // Si el cliente existe, envía el paquete.
    }

    /**
     * Elimina todos los enemigos existentes y luego los vuelve a generar en el mapa.
     */
    public void respawnEnemy(){
        for (Entity e: entities.values()){ // Itera sobre todas las entidades.
            if (e instanceof Enemy){ // Si la entidad es un enemigo.
                removeEntity(e.getId()); // Elimina el enemigo (localmente y por red).
            }
        }
        tiledManager.makeEnemy(); // Vuelve a crear los enemigos basándose en el mapa Tiled.
    }

    /**
     * Reproduce un sonido con un volumen que disminuye con la distancia al jugador.
     *
     * @param sound El sonido a reproducir.
     * @param soundPosition La posición del origen del sonido en el mundo.
     * @param maxDistance La distancia máxima a la que el sonido es audible.
     */
    public void playProximitySound(Sound sound, Vector2 soundPosition, float maxDistance) {
        float distance = soundPosition.dst(player.getBody().getPosition()); // Calcula la distancia del jugador al sonido.
        float volume = Math.max(0, 1 - (distance / maxDistance)); // Calcula el volumen (disminuye con la distancia).
        SingleSoundManager.getInstance().playSound(sound, 1f, volume); // Reproduce el sonido con el volumen calculado.
    }

    /**
     * Añade un efecto de temblor a la cámara.
     *
     * @param time La duración del temblor.
     * @param force La fuerza del temblor.
     */
    public void addCameraShake(Float time, Float force){
        cameraShakeManager.addShake(time,force); // Añade el temblor a la cámara.
    }

    /**
     * Añade un efecto de temblor a la cámara con una fuerza que disminuye con la distancia del jugador al origen del temblor.
     *
     * @param position La posición del origen del temblor.
     * @param maxDistance La distancia máxima a la que el temblor tiene efecto.
     * @param time La duración del temblor.
     * @param maxForce La fuerza máxima del temblor.
     */
    public void addCameraShakeProximity(Vector2 position, float maxDistance, float time, float maxForce) {
        float distance = position.dst(player.getBody().getPosition()); // Calcula la distancia del jugador al origen.
        float force = Math.max(0, 1 - (distance / maxDistance)); // Calcula la fuerza basada en la distancia.
        cameraShakeManager.addShake(time, maxForce * force); // Añade el temblor con la fuerza calculada.
    }

    /**
     * Método del ciclo de vida de la pantalla: `dispose()`.
     * Se llama cuando la pantalla ya no es necesaria y sus recursos deben ser liberados.
     * Es crucial para evitar fugas de memoria.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void dispose() {
        clearAll(); // Libera todos los recursos del juego.
        world.dispose(); // Libera los recursos del mundo de Box2D.
        tiledManager.dispose(); // Libera los recursos del gestor de mapas Tiled.
        // Nota: Los Stages (stage y stageUI) se suelen liberar en la clase Main o al final del ciclo de vida de la aplicación.
        // Si no se liberan aquí, se asume que `Main` se encargará de ello al finalizar el juego.
    }

    /**
     * Clase interna estática `GameContactListener` que implementa la interfaz `ContactListener` de Box2D.
     * Se encarga de manejar las colisiones entre los cuerpos físicos en el mundo del juego.
     */
    private static class GameContactListener implements ContactListener {
        private final GameScreen game; // Una referencia a la instancia de `GameScreen` para acceder a sus métodos.

        /**
         * Constructor para `GameContactListener`.
         * @param gameScreen La instancia de `GameScreen` a la que está asociado este listener.
         */
        public GameContactListener(GameScreen gameScreen) {
            this.game = gameScreen;
        }

        /**
         * Se llama cuando dos cuerpos de Box2D comienzan a contactar.
         * Despacha la llamada a los métodos `beginContactWith` de los `ActorBox2d` involucrados.
         *
         * @param contact El objeto `Contact` que representa la colisión.
         */
        @Override // Indica que este método sobrescribe un método de la interfaz ContactListener.
        public void beginContact(Contact contact) {
            // Obtiene los `ActorBox2d` asociados a los "fixtures" que colisionaron.
            ActorBox2d actorA = (ActorBox2d) contact.getFixtureA().getUserData();
            ActorBox2d actorB = (ActorBox2d) contact.getFixtureB().getUserData();

            if (actorA == null || actorB == null) return; // Si alguno de los actores es nulo, no hace nada.

            // Notifica a ambos actores que han comenzado un contacto con el otro actor.
            actorA.beginContactWith(actorB, game);
            actorB.beginContactWith(actorA, game);
        }

        /**
         * Se llama cuando dos cuerpos de Box2D dejan de contactar.
         * Este método está vacío por defecto, pero podría usarse para manejar el fin de las colisiones.
         *
         * @param contact El objeto `Contact` que representa la colisión que terminó.
         */
        @Override // Indica que este método sobrescribe un método de la interfaz ContactListener.
        public void endContact(Contact contact) {
            // Implementación vacía por defecto.
        }

        /**
         * Se llama justo antes de que se resuelva la colisión, permitiendo modificar el contacto.
         *
         * @param contact El objeto `Contact` que representa la colisión.
         * @param oldManifold El `Manifold` anterior.
         */
        @Override // Indica que este método sobrescribe un método de la interfaz ContactListener.
        public void preSolve(Contact contact, Manifold oldManifold) { }

        /**
         * Se llama después de que se ha resuelto la colisión, permitiendo acceder a los impulsos aplicados.
         *
         * @param contact El objeto `Contact` que representa la colisión.
         * @param impulse El `ContactImpulse` resultante de la colisión.
         */
        @Override // Indica que este método sobrescribe un método de la interfaz ContactListener.
        public void postSolve(Contact contact, ContactImpulse impulse) { }
    }
}
