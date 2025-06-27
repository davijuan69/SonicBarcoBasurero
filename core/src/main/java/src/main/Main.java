package src.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import src.screens.game.GameScreen;
import src.screens.uiScreens.*;
import src.screens.uiScreens.MenuScreen;
import src.utils.Fonts;
import src.utils.sound.SingleSoundManager;
import src.utils.sound.SoundManager;

import java.util.ArrayList;

/**
 * La clase `Main` es el punto de entrada principal del juego LibGDX.
 * Extiende `com.badlogic.gdx.Game`, lo que permite gestionar múltiples pantallas (`Screen`).
 * Se encarga de la inicialización global del juego, la carga de recursos (`AssetManager`),
 * la gestión de sonidos, la creación de pantallas y el manejo de la lógica de red (cliente/servidor).
 */
public class Main extends Game {
    private AssetManager assetManager;
    private ArrayList<Screen> screensList;
    private Skin skin;

    /**
     * Enumeración que define todos los tipos de pantallas disponibles en el juego.
     * Facilita el cambio entre pantallas de forma legible.
     */
    // En Main.java
    public enum Screens {
        MENU,           // Ahora es el Índice 0
        OPTION,         // Índice 1
        //INFO,           // Índice 2
        GAME,           // Índice 3
        ENDGAME,        // Índice 4
    }

    public Color playerColor;
    private String playerName;

    public Fonts fonts;

    private SoundManager soundManager;

    /**
     * Enumeración para definir diferentes tipos de pistas de sonido/música.
     */
    public enum SoundTrackType {
        MENU,
        GAME,
    }

    /**
     * Método `create` se llama una vez al inicio de la aplicación.
     * Inicializa todos los componentes esenciales del juego: Skin, activos,
     * gestores de sonido, fuentes, pantallas y establece la pantalla inicial.
     */
    @Override
    public void create() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        playerColor = new Color(Color.WHITE);

        // ¡IMPORTANTE! Cargar activos ANTES de inicializar pantallas que los usan.
        initAssets();

        soundManager = SingleSoundManager.getInstance();
        soundManager.setVolumeMusic(0.1f);
        initSounds();
        fonts = new Fonts();
        initScreens(); // Ahora creará pantallas que ya tienen sus assets cargados

        // Cambia a la pantalla del menú. Si quieres una intro, asegúrate de tenerla en initScreens.
        changeScreen(Screens.MENU);
        setScreen(screensList.get(Screens.MENU.ordinal()));
    }

    /**
     * Carga todos los recursos (texturas, etc.) del juego utilizando `AssetManager`.
     * Este método bloquea hasta que todos los assets han sido cargados (`finishLoading()`).
     */
    private void initAssets(){
        assetManager = new AssetManager();

        // Carga de texturas y sonidos necesarios para el menú y UIScreen base
        assetManager.load("ui/bg/sonic-frontiers.jpg" , Texture.class);
        assetManager.load("ui/bg/image3.jpg", Texture.class);
        assetManager.load("ui/bg/image2.jpg", Texture.class);
        assetManager.load("ui/bg/images.jpg", Texture.class);
        assetManager.load("ui/bg/img.png", Texture.class); // Fondo del menú
        assetManager.load("logo.png", Texture.class);         // Logo del juego
        assetManager.load("ui/buttons/info.png", Texture.class);
        assetManager.load("ui/buttons/infoHover.png", Texture.class);
        assetManager.load("ui/buttons/exit.png", Texture.class);
        assetManager.load("ui/buttons/exitHover.png", Texture.class);
        assetManager.load("ui/buttons/button.png", Texture.class);
        assetManager.load("ui/buttons/buttonHover.png", Texture.class);
        assetManager.load("ui/buttons/input.png", Texture.class);
        assetManager.load("ui/bg/img.png", Texture.class);
        assetManager.load("ui/uiskin.json", Skin.class);
        assetManager.load("yoshi.jpg", Texture.class);


        System.out.println("Cargando activos...");
        assetManager.finishLoading(); // Espera a que todos los activos se carguen
        System.out.println("Activos cargados.");
    }

    /**
     * Inicializa las pistas de sonido y añade la música correspondiente a cada una.
     * Utiliza el `SoundManager` para gestionar la reproducción de música de fondo.
     */
    private void initSounds(){
        soundManager.addSoundTrack(SoundTrackType.MENU);
        soundManager.addSoundTrack(SoundTrackType.GAME);
        // Aquí podrías cargar y añadir tus archivos de música
    }

    /**
     * Inicializa todas las instancias de las pantallas del juego y las añade a la `screensList`.
     * Las pantallas se crean una vez y se reutilizan para evitar sobrecarga.
     */
    private void initScreens(){
        screensList  = new ArrayList<>();
        // Asegúrate de que las pantallas se añaden en el mismo orden que en el enum Screens.
        screensList.add(new MenuScreen(this));
        screensList.add(new OptionScreen(this));
        screensList.add(new GameScreen(this));

    }

    /**
     * Establece el nombre del jugador. Si el nombre proporcionado está vacío, se establece como "Sin nombre".
     * @param name El nombre a establecer para el jugador.
     */
    public void setPlayerName(String name) {
        if (name.isEmpty()) this.playerName = "Sin nombre";
        else this.playerName = name;
    }

    /**
     * Obtiene el nombre actual del jugador.
     * @return El nombre del jugador.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Obtiene el AssetManager del juego, permitiendo el acceso a los recursos cargados.
     * @return La instancia de AssetManager.
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Obtiene el Skin de la UI del juego.
     * @return La instancia de Skin.
     */
    public Skin getSkin() {
        return skin;
    }

    /**
     * Cambia la pantalla actual del juego a la pantalla especificada por la enumeración `Screens`.
     * @param screen La pantalla a la que se desea cambiar.
     */
    public void changeScreen(Screens screen){
        setScreen(screensList.get(screen.ordinal()));
    }

    @Override
    public void render() {
        try{
            super.render();
        }catch (GdxRuntimeException e){
            System.out.println(e.getMessage());
            dispose();
        }
    }

    /**
     * Método `dispose` se llama cuando la aplicación está a punto de cerrarse.
     * Es crucial para liberar todos los recursos cargados y evitar fugas de memoria.
     * Libera assets, skins, pantallas, sonidos y fuentes.
     */
    @Override
    public void dispose() {
        assetManager.dispose();
        skin.dispose();
        for (Screen screen : screensList) {
            screen.dispose();
        }
        soundManager.dispose();
        fonts.dispose();
    }
}
