package com.sonic.app.screens.uiScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sonic.app.main.Main;
import com.sonic.app.screens.BaseScreen;
import com.sonic.app.utils.constants.MyColors;
import com.sonic.app.utils.sound.SingleSoundManager;

/**
 * Clase abstracta que sirve como base para todas las pantallas de interfaz de usuario (UI)
 * en el juego. Extiende `BaseScreen` y configura un `Stage` de Scene2D
 * para gestionar elementos de UI como botones y campos de texto.
 * Define estilos comunes para estos elementos y maneja la entrada y el renderizado
 * específicos de la UI, incluyendo efectos de sonido para interacciones.
 */
public abstract class UIScreen extends BaseScreen {
    protected final Stage stageUI; // El Stage de Scene2D donde se añadirán todos los elementos de la UI.

    public final ImageTextButton.ImageTextButtonStyle myImageTextbuttonStyle; // Estilo predefinido para botones de imagen con texto.
    public final TextField.TextFieldStyle myTextFieldStyle; // Estilo predefinido para campos de texto.

    public final InputListener hoverListener; // Listener de entrada para manejar eventos de "hover" (entrada del puntero).

    /**
     * Constructor de la clase UIScreen.
     * Inicializa el Stage de UI, carga las texturas necesarias para los estilos de botones
     * y campos de texto, define esos estilos y configura un listener para el efecto de sonido al pasar el ratón.
     * @param main La instancia principal del juego (Main), para acceder a recursos y assets.
     */
    public UIScreen(Main main) {
        super(main); // Llama al constructor de la clase padre (BaseScreen).
        Skin skin = main.getSkin(); // Obtiene el Skin global del juego, que contiene drawables y estilos predefinidos.
        stageUI = new Stage(new ScreenViewport()); // Crea un nuevo Stage con un ScreenViewport para una gestión de pantalla adaptable.

        // Carga las texturas para los estados normal y de hover de los botones.
        TextureRegionDrawable drawableUp = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/button.png", Texture.class));
        TextureRegionDrawable drawableHover = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/buttonHover.png", Texture.class));

        // Configura el estilo del botón de imagen con texto (ImageTextButton).
        myImageTextbuttonStyle = new ImageTextButton.ImageTextButtonStyle();
        myImageTextbuttonStyle.up = drawableUp; // Drawable para el estado normal (sin presionar).
        myImageTextbuttonStyle.font = main.fonts.briFont; // Fuente a usar para el texto del botón.
        myImageTextbuttonStyle.over = drawableHover; // Drawable para el estado de hover (ratón sobre el botón).
        myImageTextbuttonStyle.overFontColor = MyColors.BLUE; // Color del texto cuando el ratón está sobre el botón.

        // Carga la textura para el fondo de los campos de texto.
        Drawable drawableBg = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/input.png", Texture.class));

        // Configura el estilo del campo de texto (TextField).
        myTextFieldStyle = new TextField.TextFieldStyle();
        myTextFieldStyle.font = main.fonts.interFont; // Fuente para el texto dentro del campo.
        myTextFieldStyle.fontColor = MyColors.BLUE; // Color del texto dentro del campo.
        myTextFieldStyle.background = drawableBg; // Drawable para el fondo del campo de texto.
        // Carga drawables para el cursor y la selección de texto desde el Skin.
        myTextFieldStyle.cursor = skin.getDrawable("textFieldCursor");
        myTextFieldStyle.selection = skin.getDrawable("selection");


        // Define un InputListener para el efecto de "hover".
        hoverListener = new InputListener() {
            /**
             * Método invocado cuando el puntero del ratón entra en el área de un Actor.
             * Reproduce un sonido de click si el puntero no es -1 (lo que indica un puntero táctil o no válido).
             * @param event El evento de entrada.
             * @param x La coordenada X local del evento.
             * @param y La coordenada Y local del evento.
             * @param pointer El índice del puntero (e.g., dedo o botón del ratón).
             * @param fromActor El actor del que proviene el puntero, si aplica.
             */
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // Solo reproduce el sonido si el puntero es válido (no -1).
            }

            /**
             * Método invocado cuando el puntero del ratón sale del área de un Actor.
             * No realiza ninguna acción en esta implementación.
             * @param event El evento de entrada.
             * @param x La coordenada X local del evento.
             * @param y La coordenada Y local del evento.
             * @param pointer El índice del puntero.
             * @param toActor El actor al que se dirige el puntero, si aplica.
             */
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // No hay implementación aquí para el evento de salida (hover off).
            }
        };
    }

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla activa de `Game`.
     * Configura el `Stage` de la UI como el procesador de entrada principal de LibGDX.
     */
    @Override
    public void show(){
        Gdx.input.setInputProcessor(stageUI);
    }

    /**
     * Se llama cuando esta pantalla ya no es la pantalla activa de `Game`.
     * Desactiva el procesamiento de entrada, estableciéndolo en `null`.
     */
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Método de renderizado principal de la pantalla.
     * Limpia la pantalla, actualiza la lógica de los actores en el Stage y los dibuja.
     * @param delta El tiempo transcurrido desde el último frame en segundos.
     */
    @Override
    public void render(float delta) {
        // Limpia la pantalla con un color blanco sólido.
        Gdx.gl.glClearColor(0f,1f,0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualiza la lógica de los actores en el Stage (ej. animaciones, movimientos).
        stageUI.act(delta);
        // Dibuja todos los actores del Stage en la pantalla.
        stageUI.draw();
    }

    /**
     * Se llama cuando la pantalla del juego cambia de tamaño.
     * Actualiza el viewport del Stage para adaptarse al nuevo tamaño de la ventana,
     * manteniendo la proporción si es necesario.
     * @param width El nuevo ancho de la pantalla.
     * @param height La nueva altura de la pantalla.
     */
    @Override
    public void resize(int width, int height) {
        // 'true' indica que el viewport debe centrar la cámara después de la actualización.
        stageUI.getViewport().update(width, height, true);
    }

    /**
     * Método para liberar todos los recursos de la pantalla cuando ya no se necesita.
     * Es crucial para evitar fugas de memoria, especialmente para el Stage.
     */
    @Override
    public void dispose() {
        stageUI.dispose(); // Libera los recursos asociados al Stage.
    }
}
