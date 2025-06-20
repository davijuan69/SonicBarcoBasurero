package src.screens.game.gameLayers; // Declara el paquete donde se encuentra esta clase, indicando que es una capa específica del juego.

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import src.screens.components.LayersManager;

/**
 * La clase abstracta `GameLayer` extiende `LayersManager` y sirve como la base para
 * las diferentes capas de la interfaz de usuario específicas del juego (ej., menús, HUD, etc.).
 * Proporciona una referencia al `GameLayerManager` principal y una imagen de fondo de pausa común.
 */
public abstract class GameLayer extends LayersManager {
    protected final GameLayerManager manager; // Una referencia al gestor principal de capas del juego. Es `protected` para que las subclases puedan acceder a él, y `final` porque su referencia no cambiará.

    protected Image pauseBg; // Una imagen de fondo que se puede usar para superponer el juego cuando está en pausa o se muestra un menú.

    /**
     * Constructor para la clase `GameLayer`.
     * Inicializa la clase padre `LayersManager` y carga la imagen de fondo de pausa.
     *
     * @param manager El `GameLayerManager` que gestiona esta capa.
     * @param stage El `Stage` de Scene2d al que se añadirán los elementos de esta capa.
     * @param numLayers El número de capas internas que este `LayersManager` debe gestionar.
     */
    public GameLayer(GameLayerManager manager, Stage stage, Integer numLayers) {
        super(stage, numLayers); // Llama al constructor de la clase padre `LayersManager`.
        this.manager = manager; // Asigna el `GameLayerManager` proporcionado.

        // Carga la textura "whiteBg.png" del AssetManager del juego principal y la utiliza para crear la imagen de fondo de pausa.
        pauseBg = new Image(manager.game.main.getAssetManager().get("ui/bg/whiteBg.png", Texture.class));
    }

    /**
     * Método abstracto `update()`.
     * Las subclases de `GameLayer` deben implementar este método para definir
     * su lógica de actualización específica de cada fotograma (ej., actualizar el estado de los botones, animaciones, etc.).
     * Este método se llamará en cada ciclo de renderizado del juego.
     */
    public abstract void update();

}
