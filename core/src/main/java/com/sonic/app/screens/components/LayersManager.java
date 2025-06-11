package com.sonic.app.screens.components; // Declara el paquete donde se encuentra esta clase.

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

/**
 * La clase `LayersManager` proporciona una forma de gestionar múltiples capas (`Table`s)
 * dentro de un `Stage` de LibGDX. Cada capa es esencialmente una `Table` que cubre toda la pantalla.
 * Permite organizar actores en diferentes "planos" (zIndex) y controlar su visibilidad o posición de forma conjunta.
 */
public class LayersManager {
    private final ArrayList<Table> layers; // Una lista para almacenar todas las capas (`Table`s). Es `final` porque la lista en sí no cambiará.
    private Integer zIndex; // Un índice para referirse a la capa actualmente "seleccionada" o en foco.

    /**
     * Constructor para la clase `LayersManager`.
     * Inicializa las capas y las añade al `Stage`.
     *
     * @param stage El `Stage` al que se añadirán estas capas.
     * @param numLayers El número de capas que se deben crear.
     */
    public LayersManager(Stage stage, Integer numLayers) {
        layers = new ArrayList<>(); // Inicializa la lista de capas.
        // Crea el número especificado de capas (`Table`s).
        for (int i = 0; i < numLayers; i++) {
            Table table = new Table(); // Crea una nueva instancia de Table para la capa.
            table.setFillParent(true); // Hace que la tabla llene toda la pantalla, útil para las capas de UI.
            layers.add(table); // Añade la tabla creada a la lista de capas.
        }
        // Añade las capas al `Stage` en orden inverso (desde la última hasta la primera).
        // Esto asegura que las capas con un índice más bajo en la lista (`layers.get(0)`)
        // se dibujen encima de las capas con índices más altos en la lista,
        // ya que los actores añadidos más tarde se dibujan encima en Scene2d.
        for (int i = numLayers - 1; i >= 0; i--) {
            stage.addActor(layers.get(i)); // Añade cada capa al Stage.
        }
    }

    /**
     * Cambia el índice de la capa que se obtendrá con `getLayer()`.
     * Este método no afecta la visibilidad ni la posición de las capas, solo establece cuál capa
     * se devolverá en futuras llamadas a `getLayer()`.
     *
     * @param zIndex El índice de la capa que se desea seleccionar.
     */
    public void setZindex(Integer zIndex) {
        this.zIndex = zIndex; // Establece el índice de la capa actual.
    }

    /**
     * Obtiene la capa seleccionada actualmente por `setZindex()`.
     *
     * @return La `Table` que corresponde al `zIndex` actual.
     * Retorna la capa en la posición `zIndex` de la lista `layers`.
     */
    public Table getLayer() {
        return layers.get(zIndex); // Devuelve la capa correspondiente al zIndex actual.
    }

    /**
     * Establece la visibilidad de todas las capas gestionadas por este `LayersManager`.
     *
     * @param visible `true` para hacer visibles todas las capas, `false` para ocultarlas.
     */
    public void setVisible(Boolean visible) {
        for (Table layer : layers) { // Itera sobre cada capa en la lista.
            layer.setVisible(visible); // Establece la visibilidad de la capa.
        }
    }

    /**
     * Limpia todos los actores de cada una de las capas.
     * Esto elimina todos los elementos UI de todas las capas gestionadas.
     */
    public void clear() {
        for (Table layer : layers) { // Itera sobre cada capa.
            layer.clear(); // Elimina todos los hijos de la tabla (actores).
        }
    }

    /**
     * Verifica si las capas son visibles.
     * Como todas las capas comparten el mismo estado de visibilidad establecido por `setVisible()`,
     * basta con comprobar la visibilidad de la primera capa.
     *
     * @return `true` si las capas son visibles, `false` en caso contrario.
     */
    public Boolean isVisible() {
        return layers.get(0).isVisible(); // Devuelve la visibilidad de la primera capa.
    }

    /**
     * Establece la posición de todas las capas en el escenario.
     * Todas las capas se moverán a las coordenadas (x, y) especificadas.
     *
     * @param x La coordenada X para la nueva posición.
     * @param y La coordenada Y para la nueva posición.
     */
    public void setPosition(Float x, Float y) {
        for (Table layer : layers) { // Itera sobre cada capa.
            layer.setPosition(x, y); // Establece la posición de la capa.
        }
    }

    /**
     * Establece la posición de todas las capas de modo que sus centros estén en las coordenadas (x, y) especificadas.
     * Esto es útil para centrar las capas en un punto dado.
     *
     * @param x La coordenada X del centro deseado.
     * @param y La coordenada Y del centro deseado.
     */
    public void setCenterPosition(Float x, Float y) {
        for (Table layer : layers) { // Itera sobre cada capa.
            // Calcula la nueva posición restando la mitad del ancho y la mitad del alto de la capa
            // para centrarla en las coordenadas (x, y).
            layer.setPosition(x - layer.getWidth() / 2, y - layer.getHeight() / 2);
        }
    }
}
