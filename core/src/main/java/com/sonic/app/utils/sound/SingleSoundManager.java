package com.sonic.app.utils.sound;

/**
 * La clase `SingleSoundManager` implementa el patrón de diseño Singleton para la clase `SoundManager`.
 * Esto asegura que solo haya una única instancia de `SoundManager` disponible en toda la aplicación,
 * lo que es útil para gestionar recursos de audio globalmente y evitar problemas de concurrencia
 * o duplicación de objetos de sonido.
 */
public class SingleSoundManager {
    // La única instancia estática de la clase `SoundManager`.
    // Se inicializa a `null` para la carga perezosa (lazy loading).
    private static SoundManager instance = null;

    /**
     * Constructor privado para evitar la instanciación directa de la clase desde fuera.
     * Esto es fundamental para el patrón Singleton.
     */
    private SingleSoundManager() {
    }

    /**
     * Método público estático para obtener la única instancia de `SoundManager`.
     * Si la instancia no ha sido creada, la crea; de lo contrario, devuelve la instancia existente.
     * @return La única instancia de `SoundManager`.
     */
    public static SoundManager getInstance() {
        // Comprueba si la instancia es nula (es decir, si es la primera vez que se solicita).
        if (instance == null) {
            // Si es nula, crea una nueva instancia de `SoundManager`.
            instance = new SoundManager();
        }
        // Devuelve la instancia existente (o la recién creada).
        return instance;
    }
}
