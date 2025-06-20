package src.screens.uiScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import src.main.Main;
import src.screens.components.LayersManager;
import src.utils.sound.SingleSoundManager;

/**
 * La clase `MenuScreen` representa la pantalla del menú principal del juego.
 * Hereda de `UIScreen` para aprovechar la configuración básica de la UI (Stage, estilos de botones, etc.).
 * Esta pantalla muestra el fondo, el logo y los botones para jugar, configuración y salir.
 * También gestiona la disposición de estos elementos y la reproducción de la música del menú.
 */
public class MenuScreen extends UIScreen {
    /**
     * Constructor de la clase `MenuScreen`.
     * Configura la interfaz visual del menú, incluyendo la imagen de fondo, el logo
     * y los tres botones interactivos, asignando sus estilos y listeners.
     * @param main La instancia principal del juego, para acceder a activos y cambiar de pantalla.
     */
    public MenuScreen(Main main) {
        super(main); // Llama al constructor de la clase padre (UIScreen) para inicializar el Stage y estilos básicos.

        // --- Carga y configura la imagen de fondo ---
        // Asumo que tu imagen "ui/bg/img.png" es la que se ve en la captura
        Image bgImage = new Image(main.getAssetManager().get("ui/bg/img.png", Texture.class));


        // --- Carga y configura la imagen del logo (si es una imagen separada) ---
        // Si el logo está INCLUIDO en tu img.png, entonces puedes COMENTAR estas líneas y la capa del logo.
        Texture logoTexture = main.getAssetManager().get("logo.png", Texture.class);
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image logoImage = new Image(logoTexture);


        // --- Configuración de los botones principales (Jugar, Configuración, Salir) ---
        // Asumiendo que myImageTextbuttonStyle ya está bien definido en UIScreen

        // Botón "Jugar"
        ImageTextButton playButton = new ImageTextButton("Jugar", myImageTextbuttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.changeScreen(Main.Screens.GAME); // Cambia a la pantalla de juego
            }
        });
        playButton.addListener(hoverListener); // Añade el efecto de sonido al pasar el ratón

        // Botón "Configuración"
        ImageTextButton optionButton = new ImageTextButton("Configuración", myImageTextbuttonStyle);
        optionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.changeScreen(Main.Screens.OPTION); // Cambia a la pantalla de opciones
            }
        });
        optionButton.addListener(hoverListener); // Añade el efecto de sonido al pasar el ratón

        // Botón "Salir"
        ImageTextButton exitButton = new ImageTextButton("Salir", myImageTextbuttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Cierra la aplicación de LibGDX
            }
        });
        exitButton.addListener(hoverListener); // Añade el efecto de sonido al pasar el ratón


        // --- Disposición de los elementos de la UI usando LayersManager ---
        // Usaremos 3 capas principales: Fondo, Logo, y Botones.
        LayersManager layersManager = new LayersManager(stageUI, 3);

        // Capa 0 (más profunda): La imagen de fondo
        layersManager.setZindex(0);
        layersManager.getLayer().add(bgImage).grow(); // La imagen de fondo crecerá para llenar toda la pantalla

        // Capa 1: El logo
        // Si tu fondo (img.png) YA incluye el logo, COMENTA O ELIMINA esta capa.
        layersManager.setZindex(1);
        layersManager.getLayer().center().top().padTop(Gdx.graphics.getHeight() * 0.10f); // Posición del logo (ajusta padTop si es necesario)
        layersManager.getLayer().add(logoImage).size(Gdx.graphics.getWidth() * 0.4f); // Tamaño del logo (ajusta si es necesario)
        layersManager.getLayer().row(); // Pasa a la siguiente "fila" en esta capa para dejar espacio debajo del logo


        // Capa 2: Los botones (Jugar, Configuración, Salir)
        layersManager.setZindex(2);
        layersManager.getLayer().center(); // Centra el grupo de botones horizontalmente
        // Ajusta este padTop para mover los botones hacia arriba o abajo en relación al centro vertical
        layersManager.getLayer().padTop(Gdx.graphics.getHeight() * 0.45f); // Posición vertical de los botones
        layersManager.getLayer().add(playButton).width(Gdx.graphics.getWidth() * 0.25f).height(Gdx.graphics.getHeight() * 0.1f).pad(10).row();
        layersManager.getLayer().add(optionButton).width(Gdx.graphics.getWidth() * 0.25f).height(Gdx.graphics.getHeight() * 0.1f).pad(10).row();
        layersManager.getLayer().add(exitButton).width(Gdx.graphics.getWidth() * 0.25f).height(Gdx.graphics.getHeight() * 0.1f).pad(10).row();
        // Los tamaños de los botones se ajustan a un porcentaje del ancho/alto de la pantalla para ser responsivos
    }

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla activa de `Game`.
     * Además de la funcionalidad de la superclase, establece la pista de sonido del menú.
     */
    @Override
    public void show() {
        super.show(); // Llama al método show de la superclase (UIScreen) para configurar el InputProcessor.
        // Establece la pista de sonido activa del SoundManager a la pista de menú.
        SingleSoundManager.getInstance().setSoundTracks(Main.SoundTrackType.MENU);
    }
}
