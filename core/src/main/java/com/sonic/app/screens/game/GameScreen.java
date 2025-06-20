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
//import src.net.packets.Packet;
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
import com.sun.jdi.Mirror;

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

    // --- Fábricas ---

    // --- Entidades del Juego ---
    private final ArrayList<ActorBox2d> actors; // Una lista de todos los `ActorBox2d` en el Stage.

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

        stage = new Stage(new ScreenViewport()); // Crea el Stage principal con un viewport que se ajusta a la pantalla.
        world = new World(new Vector2(0, -30f), true); // Crea el mundo de Box2D con gravedad hacia abajo.
        threadSecureWorld = new ThreadSecureWorld(world); // Envuelve el mundo de Box2D para seguridad de hilos.


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
     * Obtiene la instancia del componente de chat.
     *
     * @return El objeto `Chat`.
     */
    public Chat getChat() {
        return chat;
    }




    /**
     * Añade un `Actor` al `Stage` y, si es una `Entity` o `ActorBox2d`, lo gestiona en las colecciones internas.
     * También gestiona indicadores para `Mirror` y `OtherPlayer`.
     *
     * @param actor El `Actor` a añadir.
     */

    /**
     * Limpia todos los elementos del juego: cuerpos de Box2D, actores de Scene2d, y colecciones internas.
     */
    public void clearAll(){
        for (ActorBox2d actor : actors) actor.detach(); // Desvincula los cuerpos de Box2D de todos los ActorBox2d.

        stage.clear(); // Limpia todos los actores del Stage principal.
        stageUI.clear(); // Limpia todos los actores del Stage de UI.
        actors.clear(); // Limpia la lista de actores.
        spawnMirror.clear(); // Limpia los puntos de aparición de espejos.
    }

    /**
     * Finaliza el juego.
     * Limpia todas las modificaciones pendientes, cierra las conexiones de red,
     * limpia el estado del juego, reinicia el temporizador y cambia a la pantalla de fin de juego.
     */
    public void endGame(){
        threadSecureWorld.clearModifications(); // Limpia todas las modificaciones pendientes.
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

        // Sincroniza la posición de la cámara de la UI con la cámara del juego.
        cameraUI.position.x = camera.position.x;
        cameraUI.position.y = camera.position.y;

        cameraShakeManager.update(delta); // Actualiza el gestor de temblor de cámara.
        camera.update(); // Actualiza la cámara del juego.
        cameraUI.update(); // Actualiza la cámara de la UI.

        layersManager.setCenterPosition(camera.position.x, camera.position.y); // Centra las capas de UI con la cámara del juego.
        gameLayerManager.setCenterPosition(cameraUI.position.x, cameraUI.position.y); // Centra las capas del menú de juego.
        actUI(); // Actualiza y prepara los elementos de la UI para el dibujo.
        stage.draw(); // Dibuja todos los actores del Stage principal.
        stageUI.draw(); // Dibuja todos los actores del Stage de UI.
        //debugRenderer.render(world, camera.combined.scale(PIXELS_IN_METER, PIXELS_IN_METER, 1)); // Renderiza las formas de Box2D para depuración (comentado).

        // Manejo de la entrada del teclado (tecla ESCAPE para mostrar/ocultar el menú de pausa).
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameLayerManager.setVisibleWithSound(!gameLayerManager.isVisible()); // Alterna la visibilidad del menú de pausa.
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

            }
        });
    }

    /**
     * Reubica un espejo a un nuevo punto de aparición aleatorio y reproduce un sonido.
     * Si está en modo multijugador, envía un paquete de sincronización.
     * También provoca la reaparición de los enemigos.
     *
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
