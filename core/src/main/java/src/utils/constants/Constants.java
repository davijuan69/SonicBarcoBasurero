package src.utils.constants;

/**
 * La clase `Constants` define constantes de uso general que se utilizan en todo el proyecto.
 * Estas constantes proporcionan valores fijos para configuraciones del juego, conversiones de unidades,
 * y otros parámetros importantes que no deben cambiar durante la ejecución.
 * El uso de constantes mejora la legibilidad, mantenibilidad y evita "números mágicos" en el código.
 */
public class Constants {
    /**
     * Define la relación de conversión entre píxeles y metros.
     * Es crucial para sistemas de física que operan en metros (como Box2D) y la renderización
     * que se realiza en píxeles. Un valor de `48f` significa que 48 píxeles equivalen a 1 metro.
     */
    public static final float PIXELS_IN_METER = 48f;

    /**
     * El número de anillos (RINGS) que están disponibles o se requieren por juego.
     * Este valor podría representar objetivos, coleccionables o elementos clave para la progresión dentro del juego.
     */
    public static final int RINGS_PER_GAME = 6;
    /**
     * La duración total del juego en minutos.
     * Podría representar el tiempo límite para completar un nivel, una ronda, o la duración total de una partida.
     */
    //public static final int TIME_MINUTES_GAME = 8;

}
