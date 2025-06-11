package com.sonic.app.utils.indicators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;

/**
 * La clase `IndicatorManager` es un `Actor` de Scene2D que gestiona múltiples `BorderIndicator`s.
 * Su propósito es centralizar la lógica para añadir, eliminar, actualizar y dibujar
 * varios indicadores que apuntan a diferentes objetivos en el juego, manteniendo la posición
 * central desde la que se calculan las direcciones de los indicadores (normalmente la cámara o el jugador principal).
 */
public class IndicatorManager extends Actor {
    private final Texture texture; // La textura base que todos los BorderIndicator usarán.
    private final HashMap<Integer, BorderIndicator> borderIndicators; // Un mapa para almacenar los indicadores, usando un ID entero para identificarlos.

    /**
     * Constructor para el `IndicatorManager`.
     * @param texture La textura que se utilizará para todos los `BorderIndicator` que se creen.
     */
    public IndicatorManager(Texture texture) {
        this.texture = texture;
        // Inicializa el HashMap para almacenar los indicadores.
        borderIndicators = new HashMap<>();
    }

    /**
     * Establece la posición central desde donde todos los indicadores calculan su dirección.
     * Típicamente, esta será la posición de la cámara o del jugador principal.
     * @param centerPosition Vector de posición del centro en metros.
     */
    public void setCenterPositions(Vector2 centerPosition) {
        // Itera sobre todos los `BorderIndicator`s actualmente gestionados y actualiza su posición central.
        for (BorderIndicator borderIndicator : borderIndicators.values()) {
            borderIndicator.setCenterPosition(centerPosition);
        }
    }

    /**
     * Añade un nuevo `BorderIndicator` al gestor.
     * @param id Un identificador único para el nuevo indicador.
     * @param targetPosition La posición del objetivo (en metros) a la que apuntará este indicador.
     */
    public void add(Integer id,Vector2 targetPosition) {
        // Crea una nueva instancia de `BorderIndicator` con la textura y la posición del objetivo dadas.
        BorderIndicator borderIndicator = new BorderIndicator(texture, targetPosition);
        // Almacena el nuevo indicador en el HashMap usando el ID proporcionado.
        borderIndicators.put(id,borderIndicator);
    }

    /**
     * Cambia la posición del objetivo de un `BorderIndicator` existente.
     * @param id El ID del indicador cuyo objetivo se desea cambiar.
     * @param targetPosition La nueva posición del objetivo (en metros) para el indicador.
     */
    public void changeTargetPosition(Integer id,Vector2 targetPosition) {
        // Comprueba si el ID existe en el mapa antes de intentar cambiar el objetivo.
        if (!borderIndicators.containsKey(id)) {
            // Si el ID no se encuentra, registra un mensaje de error en la consola de LibGDX.
            Gdx.app.log("IndicatorManager", "id " + id + " no encontrada para cambiar su target position");
            return;
        }
        // Obtiene el indicador por su ID y actualiza su posición objetivo.
        borderIndicators.get(id).setTargetPosition(targetPosition);
    }

    /**
     * Elimina un `BorderIndicator` del gestor.
     * @param id El ID del indicador que se desea eliminar.
     */
    public void remove(Integer id) {
        // Elimina el indicador del HashMap utilizando su ID.
        borderIndicators.remove(id);
    }

    /**
     * Elimina todos los `BorderIndicator`s del gestor.
     */
    public void clear() {
        // Vacía el HashMap, eliminando todas las referencias a los indicadores.
        borderIndicators.clear();
    }

    /**
     * Método `act` llamado en cada frame para actualizar la lógica de todos los indicadores gestionados.
     * @param delta El tiempo en segundos desde el último frame.
     */
    @Override
    public void act(float delta) {
        // Itera sobre todos los `BorderIndicator`s y llama a su método `act` para que cada uno se actualice.
        for (BorderIndicator borderIndicator : borderIndicators.values()) {
            borderIndicator.act(delta);
        }
    }

    /**
     * Método `draw` para renderizar todos los `BorderIndicator`s gestionados.
     * @param batch El `Batch` utilizado para dibujar los sprites de los indicadores.
     * @param parentAlpha La opacidad heredada de los padres del actor.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Itera sobre todos los `BorderIndicator`s y llama a su método `draw` para que cada uno se dibuje.
        for (BorderIndicator borderIndicator : borderIndicators.values()) {
            borderIndicator.draw(batch, parentAlpha);
        }
    }
}
