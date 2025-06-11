package com.sonic.app.utils.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * La clase `CameraShakeManager` gestiona el efecto de temblor (shake) de una cámara ortográfica.
 * Permite aplicar un temblor a la cámara durante un período de tiempo y con una fuerza determinada,
 * lo que es útil para efectos visuales como explosiones, impactos o eventos del juego.
 */
public class CameraShakeManager {
    OrthographicCamera camera; // La cámara ortográfica a la que se le aplicará el temblor.
    private Float timeShake; // El tiempo restante durante el cual la cámara debe temblar (en segundos).
    private Float forceShake; // La intensidad del temblor de la cámara.

    /**
     * Constructor para el `CameraShakeManager`.
     * @param camera La instancia de `OrthographicCamera` que será gestionada por este manager.
     */
    public CameraShakeManager(OrthographicCamera camera) {
        this.camera = camera;
        // Inicializa el tiempo y la fuerza del temblor a cero, indicando que no hay temblor activo.
        timeShake = 0f;
        forceShake = 0f;
    }

    /**
     * Añade un efecto de temblor a la cámara. Si ya hay un temblor activo,
     * este método sobrescribe los valores actuales con los nuevos.
     * @param time La duración del temblor en segundos.
     * @param force La magnitud del desplazamiento de la cámara por frame durante el temblor.
     */
    public void addShake(Float time, Float force) {
        timeShake = time;
        forceShake = force;
    }

    /**
     * Actualiza el estado del temblor de la cámara. Debe ser llamado en el bucle principal del juego.
     * @param delta El tiempo transcurrido desde el último frame en segundos.
     */
    public void update(Float delta) {
        // Comprueba si aún queda tiempo para el temblor.
        if (timeShake > 0) {
            // Reduce el tiempo restante del temblor.
            timeShake -= delta;
            // Aplica un desplazamiento aleatorio a la posición X de la cámara.
            // El desplazamiento es un valor aleatorio entre -forceShake/2 y +forceShake/2,
            // lo que crea un movimiento de temblor centrado alrededor de la posición original.
            camera.position.x += (float) (Math.random() * forceShake - forceShake / 2);
            // Aplica un desplazamiento aleatorio similar a la posición Y de la cámara.
            camera.position.y += (float) (Math.random() * forceShake - forceShake / 2);
        }
    }
}
