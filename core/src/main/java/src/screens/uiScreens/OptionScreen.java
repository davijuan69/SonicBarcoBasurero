package src.screens.uiScreens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import src.main.Main;
import src.screens.components.LayersManager;
import src.screens.components.OptionTable;

/**
 * La clase `OptionScreen` representa la pantalla de opciones o ajustes del juego.
 * Hereda de `BlueCircleScreen` para aprovechar el diseño base (título, botón de regreso, etc.).
 * Esta pantalla se encarga de mostrar y gestionar la tabla de opciones configurables.
 */
public class OptionScreen extends BlueCircleScreen {
    // Declara una instancia de `OptionTable` que contendrá los elementos de UI para las opciones.
    // El doble punto y coma aquí es un error tipográfico y no afectará el funcionamiento,
    // pero idealmente debería ser solo uno: `private final OptionTable optionTable;`
    private final OptionTable optionTable;;

    /**
     * Constructor de la clase `OptionScreen`.
     * Inicializa la pantalla de opciones, configurando el título y el retorno al menú principal,
     * y luego crea y añade la tabla de opciones a la UI.
     *
     * @param main La instancia principal del juego, necesaria para acceder a recursos y funcionalidades.
     */



    public OptionScreen(Main main) {
        // Llama al constructor de la clase padre (`BlueCircleScreen`).
        // Le pasa la instancia `main`, el título de la pantalla ("Ajustes"),
        // `null` para el fondo (indicando que no usará un fondo específico adicional, sino el de la base),
        // y `Main.Screens.MENU` como la pantalla a la que regresar al salir de Ajustes.
        super(main, "Ajustes", Main.Screens.MENU);
        // Obtiene el Skin del juego, que contiene los estilos y recursos gráficos para los widgets de UI.
        Skin skin = main.getSkin();

        // Inicializa un `LayersManager` con el `stageUI` (heredado de UIScreen)
        // y un número de capas (en este caso, 1).
        // Aunque se usa LayersManager, aquí solo se utiliza una capa para la OptionTable.
        LayersManager layersManager = new LayersManager(stageUI, 1);

        // Establece el z-index para la capa actual. En este caso, la capa 0.
        layersManager.setZindex(0);
        // Crea una nueva instancia de `OptionTable`.
        // Le pasa el `skin` (para los estilos de los widgets de opciones),
        // el `Table` de la capa actual (`layersManager.getLayer()`) para que OptionTable pueda añadir sus elementos,
        // y la fuente `briFont` para el texto dentro de las opciones.
        optionTable = new OptionTable(skin, layersManager.getLayer(), main.fonts.briFont);
    }

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla activa del juego.
     * Además de la funcionalidad de la superclase (`BlueCircleScreen`),
     * actualiza la tabla de opciones para asegurar que muestre los valores actuales
     * (por ejemplo, volumen actual, etc.).
     */
    @Override
    public void show() {
        // Llama al método `show()` de la superclase (`BlueCircleScreen`),
        // que maneja la configuración del InputProcessor y potencialmente la música de fondo.
        super.show();
        // Llama al método `update()` de `optionTable`. Esto es crucial para
        // refrescar los estados de los controles de la UI (como sliders o checkboxes)
        // con los valores actuales del juego (ej., volumen actual).
        optionTable.update();
    }
}
