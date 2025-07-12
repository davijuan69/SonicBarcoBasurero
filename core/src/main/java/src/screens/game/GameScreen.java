package src.screens.game;

// Importaciones necesarias para la funcionalidad de la pantalla de juego.
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import src.main.Main;
import src.world.entities.EntityFactory;
import src.utils.ThreadSecureWorld;
import src.screens.components.LayersManager;
import src.screens.components.PowerView;
import src.screens.components.chat.Chat;
import src.screens.game.gameLayers.GameLayerManager;
import src.screens.uiScreens.UIScreen;
import src.utils.ScorePlayer;
import src.utils.SecondsTimer;
import src.utils.constants.ConsoleColor;
import src.utils.indicators.BorderIndicator;
import src.utils.indicators.IndicatorManager;
import src.utils.managers.CameraShakeManager;
import src.utils.managers.SpawnManager;
import src.utils.sound.SingleSoundManager;
import src.world.ActorBox2d;
import src.world.entities.enemies.Enemy;
import src.world.entities.player.Player;
import src.world.entities.player.PlayerCommon;
import src.world.entities.Entity;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import src.utils.managers.TiledManager;
import com.badlogic.gdx.utils.viewport.FitViewport;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//import static src.utils.constants.Constants.TIME_MINUTES_GAME;

/**
 * GameScreen representa la pantalla principal del juego.
 * Se encarga de gestionar el mundo físico, los actores, la interfaz gráfica, el chat, sonidos y otros aspectos.
 * Esta clase actúa como un punto de entrada para el ciclo de vida del juego (inicialización, actualización, renderizado y limpieza).
 */
public class GameScreen extends UIScreen {
    // === Componentes del juego ===
    private final Stage stage; // Stage principal que contiene los actores físicos del juego.
    private final World world; // Mundo de físicas basado en Box2D.
    public ThreadSecureWorld threadSecureWorld; // Mundo de físicas con soporte para hilos seguros.
    private Boolean isLoad; // Bandera que indica si la pantalla se ha cargado completamente.

    private final OrthogonalTiledMapRenderer tiledRenderer;
    private final TiledManager tiledManager;

    private Player player;
    private final ArrayList<ActorBox2d> actors;
    private final HashMap<Integer, Entity> entities;

    public Vector2 lobbyPlayer;
    public SpawnManager spawnMirror;
    public ArrayList<Vector2> spawnPlayer;

    private int nextEntityId = 1000; // Contador para generar IDs únicos para nuevas entidades

    // === Red y tiempo ===
    private final Vector2 lastPosition; // Última posición conocida del jugador, usada para sincronización.
    private Float sendTime; // Temporizador para limitar la frecuencia de envío de datos por red.
    //private final SecondsTimer timeGame; // Temporizador que mide la duración de la partida.
    private final HashMap<Integer, ScorePlayer> scorePlayers; // Puntuaciones de los jugadores indexadas por ID.

    // === Interfaz de Usuario (UI) ===
    private LayersManager layersManager; // Gestor de capas para organizar los elementos UI superpuestos.
    private Label odsPointsLabel, gameTimeLabel; // Etiquetas que muestran puntos obtenidos y tiempo restante.
    private Chat chat; // Componente de chat para mensajería entre jugadores.
    private PowerView imagePower; // Indicador visual del poder activo del jugador.
    private GameLayerManager gameLayerManager; // Gestor de capas para menús o ventanas emergentes.

    //Factories
    public final EntityFactory entityFactory;


    // === Indicadores visuales ===
    private final CameraShakeManager cameraShakeManager; // Administrador de efecto de vibración de cámara.
    private IndicatorManager mirrorIndicators; // Indicadores visuales que señalan la posición de espejos.
    private BorderIndicator maxScoreIndicator; // Indicador del jugador con mayor puntuación.
    private Integer idTargetMaxScore; // ID del jugador que actualmente tiene la puntuación más alta.

    // === Sonidos ===
    private Sound mirrorChangeSound; // Sonido que se reproduce cuando un espejo cambia de posición.

    // === Depuración ===
    private final Box2DDebugRenderer debugRenderer; // Herramienta visual para depurar el mundo físico Box2D.

    /**
     * Constructor de la clase GameScreen.
     * Inicializa todos los componentes fundamentales del juego incluyendo física, lógica, UI y temporizadores.
     */
    public GameScreen(Main main) {
        super(main);
        actors = new ArrayList<>();
        entities = new HashMap<>();

        this.entityFactory = new EntityFactory(this); // Inicializa la fábrica de entidades


        stage = new Stage(new FitViewport(480, 270));
        world = new World(new Vector2(0, -30f), true); // Gravedad descendente.
        threadSecureWorld = new ThreadSecureWorld(world);
        world.setContactListener(new GameContactListener(this));

        tiledManager = new TiledManager(this);
        tiledRenderer = tiledManager.setupMap("tiled/maps/mapa_sonic (2).tmx");

        world.setContactListener(new GameContactListener(this));
        lastPosition = new Vector2();
        sendTime = 0f;
        scorePlayers = new HashMap<>();
        //timeGame = new SecondsTimer(TIME_MINUTES_GAME, 0);

        spawnMirror = new SpawnManager();
        spawnPlayer = new ArrayList<>();
        spawnPlayer.add(new Vector2(40, 50)); // Coordenadas de ejemplo, ajusta según necesites

        idTargetMaxScore = -1;

        initSounds();
        debugRenderer = new Box2DDebugRenderer();
        cameraShakeManager = new CameraShakeManager((OrthographicCamera) stage.getCamera());
        isLoad = false;
    }

    /**
     * Inicializa y carga los efectos de sonido necesarios.
     */
    private void initSounds() {
        // Aquí puedes cargar sonidos usando el assetManager si es necesario.
    }

    /**
     * Retorna el mapa de puntuaciones de los jugadores.
     */
    public HashMap<Integer, ScorePlayer> getScorePlayers() {
        return scorePlayers;
    }

    /**
     * Retorna el mundo físico Box2D del juego.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Retorna el componente de chat usado por los jugadores.
     */
    public Chat getChat() {
        return chat;
    }

    public Player getPlayer() {
        return player;
    }

    public void addMainPlayer(){
        if (player != null) return;
        Vector2 position = new Vector2(spawnPlayer.get(0));

        player = new Player(world, position.x, position.y, main.getAssetManager(), this, main.playerColor);
        stage.addActor(player);
    }

    public void addActor(Actor actor){
        if (actor instanceof Entity e) entities.put(e.getId(), e);
        if (actor instanceof ActorBox2d a) actors.add(a);

        stage.addActor(actor);
    }

    /**
     * Elimina todos los actores, cuerpos y estructuras asociadas del juego.
     */
    public void clearAll() {
        for (ActorBox2d actor : actors) actor.detach();
        if (player != null) player.detach();
        player = null;
        stage.clear();
        stageUI.clear();
        actors.clear();
        spawnMirror.clear();
    }

    /**
     * Finaliza la partida y cambia a la pantalla de fin del juego.
     */
    public void endGame() {
        threadSecureWorld.clearModifications();
        threadSecureWorld.addModification(() -> {
            clearAll();
            //timeGame.resetTimer();
            //main.changeScreen(Main.Screens.ENDGAME);
            isLoad = false;
        });
    }

    /**
     * Configura el estado inicial al mostrar la pantalla.
     * Incluye el procesador de entradas y la banda sonora.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stageUI);
        SingleSoundManager.getInstance().setSoundTracks(Main.SoundTrackType.GAME);

        if (player != null) {
            player.setPaused(false);
            threadSecureWorld.addModification(() -> {
                Vector2 position = spawnPlayer.get(0);
                player.getBody().setTransform(position.x, position.y, 0);
                player.getBody().setLinearVelocity(0,0);
                player.setCurrentState(Player.StateType.IDLE);
            });
        }else{
            tiledManager.makeMap();
            addMainPlayer();
            //setScore(3);
            initUI();
            //gameLayerManager.setVisible(false);
            isLoad = true;
        }
        spawnBasicEnemyNearPlayer();
        spawnThrowerEnemyNearPlayer();
        spawnTrashNearPlayer();
        spawnTrashUnderPlayer();
        spawnTrowerUnderPlayer();
        spawnEggmanPlayer();
    }

    /**
     * Inicializa todos los elementos de la interfaz de usuario.
     */
    private void initUI() {
        // [...documentación ya presente en el bloque original...]
    }

    /**
     * Ejecuta la lógica principal del juego (tiempo, físicas, actores).
     */
    public void actLogic(float delta) {
//        if (timeGame.isFinished()) endGame();
//        timeGame.update(delta);
        stage.act();
        threadSecureWorld.step(delta, 6, 2);
    }

    /**
     * Actualiza los elementos visuales de la interfaz de usuario.
     */
    private void actUI() {
        // Actualización de indicadores, etiquetas, puntajes...
    }

    /**
     * Método principal de renderizado. Se ejecuta en cada frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.5f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isLoad) return;

        OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
        OrthographicCamera cameraUI = (OrthographicCamera) stageUI.getCamera();

        camera.position.x = MathUtils.lerp(camera.position.x, player.getX() + (player.isFlipX() ? -32 : 32), 0.10f);
        camera.position.y = MathUtils.lerp(camera.position.y, player.getY(), 0.3f);

        tiledRenderer.setView(camera);

        tiledRenderer.render();

        cameraUI.position.x = camera.position.x;
        cameraUI.position.y = camera.position.y;

        //cameraUI.position.set(camera.position);

        cameraShakeManager.update(delta);
        camera.update();
        cameraUI.update();

        //layersManager.setCenterPosition(camera.position.x, camera.position.y);
        //gameLayerManager.setCenterPosition(cameraUI.position.x, cameraUI.position.y);

        actUI();
        stage.draw();
        stageUI.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameLayerManager.setVisibleWithSound(!gameLayerManager.isVisible());
        }

        actLogic(delta);
    }

    /**
     * Ajusta las dimensiones de la pantalla y actualiza los viewports.
     */
    @Override
    public void resize(int width, int height) {
        Gdx.app.postRunnable(() -> {
            float cameraZoom = 1280.0f / width;
            if (cameraZoom > 1.3f) cameraZoom = 1.3f;
            OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
            camera.zoom = cameraZoom;
            stage.getViewport().update(width, height, false);
            stageUI.getViewport().update(width, height, false);

            if (player == null) return;

            camera.position.x = player.getX() + (player.isFlipX() ? -32 : 32);
            camera.position.y = player.getY();
        });
    }

    /**
     * Agrega efecto de vibración a la cámara principal.
     */
    public void addCameraShake(Float time, Float force) {
        cameraShakeManager.addShake(time, force);
    }

    /**
     * Vibración basada en proximidad del jugador a un evento.
     */
    public void addCameraShakeProximity(Vector2 position, float maxDistance, float time, float maxForce) {
        // Lógica para temblor por proximidad (puede usarse para explosiones, etc).
    }

    /**
     * Libera los recursos utilizados por esta pantalla.
     */
    @Override
    public void dispose() {
        clearAll();
        world.dispose();
    }

    /**
     * Listener para detectar colisiones físicas entre objetos en Box2D.
     */
    private static class GameContactListener implements ContactListener {
        private final GameScreen game;

        public GameContactListener(GameScreen gameScreen) {
            this.game = gameScreen;
        }

        @Override
        public void beginContact(Contact contact) {
            ActorBox2d actorA = (ActorBox2d) contact.getFixtureA().getUserData();
            ActorBox2d actorB = (ActorBox2d) contact.getFixtureB().getUserData();
            if (actorA == null || actorB == null) return;
            actorA.beginContactWith(actorB, game);
            actorB.beginContactWith(actorA, game);
        }

        @Override public void endContact(Contact contact) {}
        @Override public void preSolve(Contact contact, Manifold oldManifold) {}
        @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
    }

    private void createEntityLogic(Entity.Type type, Vector2 position, Vector2 force, Integer id, Boolean flipX){
        if (entities.get(id) != null) {
            System.out.println(ConsoleColor.RED + "Entity " + type + ":" + id + " ya existe en la lista" + ConsoleColor.RESET);
            return;
        }
        //System.out.println("Creando Entidad " + id + " Tipo: " + type);
        threadSecureWorld.addModification(() -> {
            Entity newEntity = entityFactory.create(type, world, position, id);
            newEntity.setFlipX(flipX);
            newEntity.getBody().applyLinearImpulse(force, newEntity.getBody().getWorldCenter(), true);
            addActor(newEntity);
        });
    }

    public void addEntityNoPacket(Entity.Type type, Vector2 position, Vector2 force, Integer id, Boolean flipX){
        createEntityLogic(type, position, force, id, flipX);
        main.setIds(id);
    }
    public void addEntityNoPacket(Entity.Type type, Vector2 position, Vector2 force, Boolean flipX){
        int id = main.getIds();
        createEntityLogic(type, position, force, id, flipX);
    }

    public void addEntity(Entity.Type type, Vector2 position, Vector2 force, Boolean flipX){
        int id = main.getIds();
        createEntityLogic(type, position, force, id, flipX);

    }
    public void addEntity(Entity.Type type, Vector2 position, Vector2 force){
        addEntity(type, position, force, false);
    }

    public void addEntitySpawn(Entity.Type type, Vector2 force, SpawnManager spawnManager){
        int id = main.getIds();
        Vector2 position = spawnManager.takeSpawnPoint(id);
        createEntityLogic(type, position, force, id, false);

    }

    public void actEntityPos(Integer id, Float x, Float y, Float fx, Float fy){
        Entity entity = entities.get(id);
        if (entity == null) {
            System.err.println("Entity " + id + " no encontrada en la lista para cambiar su posicion");
            return;
        }
        Body body = entity.getBody();
        threadSecureWorld.addModification(() -> {
            body.setTransform(x, y, 0);
            body.setLinearVelocity(fx, fy);
        });
    }

    public void addEntity(Entity entity) {
        entities.put(entity.getId(), entity); // Añade la entidad al mapa.
        stage.addActor(entity); // Añade la entidad como actor al Stage.
    }

    public void actEnemy(Integer id, Enemy.StateType state, Float cronno, Boolean flipX){
        Enemy enemy = (Enemy) entities.get(id);
        if (enemy == null) {
            System.out.println("Entity " + id + " no encontrada en la lista");
            return;
        }
        if (enemy.getCurrentStateType() == Enemy.StateType.ATTACK) return;
        enemy.setState(state);
        enemy.setFlipX(flipX);
        enemy.setActCrono(cronno);
    }

    public void actDamageEnemy(Integer receiverId, Body attacker, Integer damage, Float knockback) {
        if (!entities.containsKey(receiverId)) {
            System.out.println("Entity " + receiverId + " no encontrada en la lista para actualizar dano");
            return;
        }
        Body receiver = entities.get(receiverId).getBody();
        Vector2 pushDirection = attacker.getPosition().cpy().sub(receiver.getPosition()).nor();

        actDamageEnemyNoPacket(receiverId, damage, pushDirection.x, pushDirection.y, knockback);


    }

    public void removeEntityNoPacket(Integer id){
        threadSecureWorld.addModification(() -> {
            Entity entity = entities.get(id);
            if (entity == null) {
                System.out.println(ConsoleColor.RED + "Entity " + id + " no se pudo eliminar ,no encontrada en la lista" + ConsoleColor.RESET);
                return;
            }
            entities.remove(entity.getId());
            actors.remove(entity);
            removeActor(entity);
            entity.detach();
        });
    }

    public void removeEntity(Integer id){
        removeEntityNoPacket(id);
    }

    public void removeActor(Actor actor){
        stage.getActors().removeValue(actor, true);
    }

    public void actDamageEnemyNoPacket(Integer id, Integer damage, Float forceX, Float forceY, Float knockback){
        if (!entities.containsKey(id)) {
            System.out.println("Entity " + id + " no encontrada en la lista para actualizar dano");
            return;
        }
        Enemy enemy = (Enemy) entities.get(id);
        if (enemy.getCurrentStateType() == Enemy.StateType.DAMAGE) return;
        threadSecureWorld.addModification(() -> {
            enemy.takeDamage(damage);
            enemy.getBody().setLinearVelocity(0,0);
            enemy.getBody().applyLinearImpulse(forceX* -knockback, forceY* -knockback, enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);
            enemy.getBody().applyLinearImpulse(0,knockback, enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);
        });
    }

    public void spawnBasicEnemyNearPlayer() {
        if (player == null || world == null) {
            System.out.println("No se puede generar un enemigo: el jugador o el mundo no están inicializados.");
            return;
        }


        for (int i = 0; i < 20; i++) { // Bucle para generar 10 enemigos
            Vector2 playerPosition = player.getBody().getPosition();
            // Calcula una posición aleatoria cerca del jugador
            float offsetX = 13f + 30f*i; // Entre -0.5 y -5.5 unidades a la izquierda
            float offsetY = 0f; // Pequeña variación en Y

            Vector2 spawnPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y - offsetY);

            Integer newEnemyId = getNextEntityId();
            Entity enemy = entityFactory.create(Entity.Type.BASIC, world, spawnPosition, newEnemyId);

            if (enemy != null) {
                addEntity(enemy);
                System.out.println("Enemigo ThrowerEnemy con ID " + newEnemyId + " añadido en la posición " + spawnPosition.x + ", " + spawnPosition.y);
            } else {
                System.out.println("Error: No se pudo crear el ThrowerEnemy.");
            }
        }
    }

    public void spawnThrowerEnemyNearPlayer() {
        if (player == null || world == null) {
            System.out.println("No se puede generar un enemigo: el jugador o el mundo no están inicializados.");
            return;
        }


        for (int i = 0; i < 20; i++) { // Bucle para generar 10 enemigos
            Vector2 playerPosition = player.getBody().getPosition();
            // Calcula una posición aleatoria cerca del jugador
            float offsetX = 2f + 30f*i; // Entre -0.5 y -5.5 unidades a la izquierda
            float offsetY = 0f; // Pequeña variación en Y

            Vector2 spawnPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y - offsetY);

            Integer newEnemyId = getNextEntityId();
            Entity enemy = entityFactory.create(Entity.Type.THROWER, world, spawnPosition, newEnemyId);

            if (enemy != null) {
                addEntity(enemy);
                System.out.println("Enemigo ThrowerEnemy con ID " + newEnemyId + " añadido en la posición " + spawnPosition.x + ", " + spawnPosition.y);
            } else {
                System.out.println("Error: No se pudo crear el ThrowerEnemy.");
            }
        }
    }

    public void spawnTrashNearPlayer() {
        if (player == null || world == null) {
            System.out.println("No se puede generar un enemigo: el jugador o el mundo no están inicializados.");
            return;
        }


        for (int i = 0; i < 20; i++) { // Bucle para generar 10 enemigos
            Vector2 playerPosition = player.getBody().getPosition();
            // Calcula una posición aleatoria cerca del jugador
            float offsetX = 5f + 30f*i; // Entre -0.5 y -5.5 unidades a la izquierda
            float offsetY = 0f; // Pequeña variación en Y

            Vector2 spawnPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y - offsetY);

            Integer newEnemyId = getNextEntityId();
            Entity enemy = entityFactory.create(Entity.Type.MOUNT, world, spawnPosition, newEnemyId);

            if (enemy != null) {
                addEntity(enemy);
                System.out.println("Enemigo ThrowerEnemy con ID " + newEnemyId + " añadido en la posición " + spawnPosition.x + ", " + spawnPosition.y);
            } else {
                System.out.println("Error: No se pudo crear el ThrowerEnemy.");
            }
        }
    }
    public void spawnTrashUnderPlayer() {
        if (player == null || world == null) {
            System.out.println("No se puede generar un enemigo: el jugador o el mundo no están inicializados.");
            return;
        }


        for (int i = 0; i < 20; i++) { // Bucle para generar 10 enemigos
            Vector2 playerPosition = player.getBody().getPosition();
            // Calcula una posición aleatoria cerca del jugador
            float offsetX = 8f + 30f*i; // Entre -0.5 y -5.5 unidades a la izquierda
            float offsetY = 50f; // Pequeña variación en Y

            Vector2 spawnPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y - offsetY);

            Integer newEnemyId = getNextEntityId();
            Entity enemy = entityFactory.create(Entity.Type.MOUNT, world, spawnPosition, newEnemyId);

            if (enemy != null) {
                addEntity(enemy);
                System.out.println("Enemigo ThrowerEnemy con ID " + newEnemyId + " añadido en la posición " + spawnPosition.x + ", " + spawnPosition.y);
            } else {
                System.out.println("Error: No se pudo crear el ThrowerEnemy.");
            }
        }
    }

    public void spawnTrowerUnderPlayer() {
        if (player == null || world == null) {
            System.out.println("No se puede generar un enemigo: el jugador o el mundo no están inicializados.");
            return;
        }


        for (int i = 0; i < 20; i++) { // Bucle para generar 10 enemigos
            Vector2 playerPosition = player.getBody().getPosition();
            // Calcula una posición aleatoria cerca del jugador
            float offsetX = 3f + 30f*i; // Entre -0.5 y -5.5 unidades a la izquierda
            float offsetY = 50f; // Pequeña variación en Y

            Vector2 spawnPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y - offsetY);

            Integer newEnemyId = getNextEntityId();
            Entity enemy = entityFactory.create(Entity.Type.THROWER, world, spawnPosition, newEnemyId);

            if (enemy != null) {
                addEntity(enemy);
                System.out.println("Enemigo ThrowerEnemy con ID " + newEnemyId + " añadido en la posición " + spawnPosition.x + ", " + spawnPosition.y);
            } else {
                System.out.println("Error: No se pudo crear el ThrowerEnemy.");
            }
        }
    }

    public void spawnEggmanPlayer() {
        if (player == null || world == null) {
            System.out.println("No se puede generar un enemigo: el jugador o el mundo no están inicializados.");
            return;
        }
        Vector2 playerPosition = player.getBody().getPosition();
        // Calcula una posición aleatoria cerca del jugador
        float offsetX = 500f; // Entre -0.5 y -5.5 unidades a la izquierda
        float offsetY = 10f; // Pequeña variación en Y

            Vector2 spawnPosition = new Vector2(playerPosition.x + offsetX, playerPosition.y - offsetY);

            Integer newEnemyId = getNextEntityId();
            Entity enemy = entityFactory.create(Entity.Type.THROWER, world, spawnPosition, newEnemyId);

            if (enemy != null) {
                addEntity(enemy);
                System.out.println("Enemigo ThrowerEnemy con ID " + newEnemyId + " añadido en la posición " + spawnPosition.x + ", " + spawnPosition.y);
            } else {
                System.out.println("Error: No se pudo crear el ThrowerEnemy.");
            }

    }


    /**
     * Método para obtener un ID único para una nueva entidad.
     * Incrementa el contador y lo devuelve.
     */
    private int getNextEntityId() {
        return nextEntityId++;
    }







}
