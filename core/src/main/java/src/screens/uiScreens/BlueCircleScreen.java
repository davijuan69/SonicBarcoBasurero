package src.screens.uiScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table; // Importar Table para contentLayerTable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import src.main.Main;
import src.screens.components.LayersManager;

/**
 * La clase `BlueCircleScreen` representa una pantalla de UI genérica con un diseño específico.
 * Se utiliza para pantallas como "Opciones", "Créditos" o "Acerca de", donde se necesita un título,
 * un botón de regreso y un fondo opcional. Utiliza un sistema de capas para organizar los elementos visuales.
 */
public class BlueCircleScreen extends UIScreen {
    // Almacena la pantalla a la que se debe regresar cuando el usuario sale de esta pantalla.
    private final Main.Screens backPage;
    // Capa de tabla protegida para que las subclases puedan añadir su contenido.
    protected Table contentLayerTable; // Declaración para la capa de contenido


    /**
     * Constructor de la clase `BlueCircleScreen`.
     * Configura la interfaz visual de la pantalla, incluyendo el título, el fondo,
     * y el botón para regresar a la pantalla anterior.
     *
     * @param main La instancia principal del juego, para acceder a activos y cambiar de pantalla.
     * @param title El texto que se mostrará como título de la pantalla.
     * @param backPage La enumeración de la pantalla a la que se debe regresar al salir.
     */
    public BlueCircleScreen(Main main, String title, Main.Screens backPage) {
        // Llama al constructor de la clase padre (UIScreen) para inicializar el Stage y estilos básicos.
        super(main);
        // Asigna la pantalla de retorno.
        this.backPage = backPage;

        // Carga la textura del fondo y crea la imagen aquí.
        // Asegúrate de que "ui/bg/sonic-frontiers.jpg" esté cargada en tu AssetManager.
        Texture fondoTexture = main.getAssetManager().get("ui/bg/sonic-frontiers.jpg", Texture.class);
        fondoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image bgImage = new Image(fondoTexture);


        // Inicializa LayersManager con el Stage de la UI y un número máximo de capas.
        // Las capas permiten organizar los elementos de la UI en diferentes planos Z.
        // Necesitamos 3 capas principales: 0 (Botón), 1 (Título), 2 (Contenido para subclase), 3 (Fondo).
        // Aunque antes tenías 6, con solo el fondo y sin las decoraciones, 4 es suficiente (0-3).
        // Ajusto a 4, si necesitas más por algún motivo, puedes aumentarlo.
        LayersManager layersManager = new LayersManager(stageUI, 4);

        // --- Configuración del Título de la Pantalla ---
        Label titleLabel = new Label(title, new Label.LabelStyle(main.fonts.briTitleFont, Color.WHITE));
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScaleX(2);
        titleLabel.setFontScaleY(2);

        // --- Configuración del Botón de Salida/Regreso ---
        TextureRegionDrawable drawableUp = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/exit.png", Texture.class));
        TextureRegionDrawable drawableHover = new TextureRegionDrawable(main.getAssetManager().get("ui/buttons/exitHover.png", Texture.class));
        drawableHover.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        drawableUp.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawableUp;
        style.imageOver = drawableHover;

        ImageButton exitButton = new ImageButton(style);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.changeScreen(backPage);
            }
        });
        exitButton.addListener(hoverListener);

        // --- Disposición de los Elementos de la UI usando LayersManager ---
        // Los elementos se añaden a diferentes capas (z-index) para controlar su orden de renderizado.
        // Un z-index más bajo se renderiza primero (más al fondo), un z-index más alto se renderiza encima.

        // Capa 3: Imagen de Fondo (la más profunda)
        layersManager.setZindex(2); // Z-index más alto para el fondo
        layersManager.getLayer().center().bottom();
        layersManager.getLayer().add(bgImage).grow(); // Añade la imagen de fondo y la hace crecer para llenar la capa.


        // Capa 0: Botón de Salida (arriba a la derecha)
        layersManager.setZindex(0); // Establece la capa actual.
        layersManager.getLayer().top(); // Alinea el contenido de esta capa a la parte superior.
        layersManager.getLayer().add().expandX(); // Añade una celda expandible para empujar el botón a la derecha.
        layersManager.getLayer().add(exitButton).size(64).pad(25);

        // Capa 1: Título de la Pantalla (arriba, centrado)
        layersManager.setZindex(1); // Establece la capa actual.
        layersManager.getLayer().top(); // Alinea el contenido de esta capa a la parte superior.
        // Añade el título con un padding superior, ancho fijo y lo centra dentro de su celda.
        layersManager.getLayer().add(titleLabel).padTop(65).width(Gdx.graphics.getWidth()).center();
        layersManager.getLayer().add().expandX();

    }

    /**
     * Se llama en cada fotograma del juego para actualizar y renderizar la pantalla.
     * Aquí se manejan entradas de teclado específicas, como la tecla ESCAPE.
     *
     * @param delta El tiempo en segundos desde el último fotograma.
     */
    @Override
    public void render(float delta) {
        // Llama al método render de la superclase (UIScreen) para procesar el Stage UI.
        super.render(delta);
        // Verifica si la tecla ESCAPE fue presionada justo en este fotograma.
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Si ESCAPE es presionado, cambia la pantalla a la `backPage` definida.
            main.changeScreen(backPage);
        }
    }
}
