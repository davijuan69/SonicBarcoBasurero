package src.screens.uiScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
        Image bgImage = new Image(main.getAssetManager().get("ui/bg/sonicFondo.jpg", Texture.class));
        bgImage.setScaling(com.badlogic.gdx.utils.Scaling.stretch); // Hace que el fondo se estire
        bgImage.setFillParent(true); // Hace que ocupe todo el Stage
        stageUI.addActor(bgImage); // Agrega el fondo directamente al Stage antes que cualquier otro actor

        // --- Configuración de los botones principales (Jugar, Configuración, Salir) ---
        // Asumiendo que myImageTextbuttonStyle ya está bien definido en UIScreen
        TextureRegionDrawable drawableUp = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/info.png", Texture.class));
        TextureRegionDrawable drawableHover = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/infoHover.png", Texture.class));
        drawableHover.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        drawableUp.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
        imageButtonStyle.imageUp = drawableUp;
        imageButtonStyle.imageOver = drawableHover;

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


        // Botón "Acerca De"
        ImageButton infoButton = new ImageButton(imageButtonStyle);
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.changeScreen(Main.Screens.INFO);
            }
        });
        infoButton.addListener(hoverListener);




        // --- Disposición de los elementos de la UI usando LayersManager ---
        LayersManager layersManager = new LayersManager(stageUI, 2);

        // Capa 0: Los botones (Jugar, Configuración, Salir)
        layersManager.setZindex(0);
        layersManager.getLayer().setFillParent(true);
        layersManager.getLayer().bottom().padBottom(50); // Botones pegados abajo con un pequeño margen
        layersManager.getLayer().add(playButton).width(300).height(80).pad(10).row();
        layersManager.getLayer().add(optionButton).width(300).height(80).pad(10).row();
        layersManager.getLayer().add(exitButton).width(300).height(80).pad(10).row();

        // Los tamaños de los botones se ajustan a un porcentaje del ancho/alto de la pantalla para ser responsivos

        layersManager.setZindex(1);
        layersManager.getLayer().setFillParent(true);
        layersManager.getLayer().top().right().pad(20); // Posiciona en la esquina superior derecha con un margen
        layersManager.getLayer().add(infoButton).width(80).height(80);
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

    @Override
    public void resize(int width, int height) {
        stageUI.getViewport().update(width, height, true);
    }
}
