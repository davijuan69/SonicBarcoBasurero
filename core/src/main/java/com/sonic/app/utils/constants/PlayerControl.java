package com.sonic.app.utils.constants;

import com.badlogic.gdx.Input;

/**
 * La clase `PlayerControl` define constantes para las teclas de control asignadas a las acciones del jugador.
 * Utiliza los códigos de teclado de `com.badlogic.gdx.Input.Keys`, lo que facilita la configuración
 * y la modificación de las asignaciones de teclas en el juego.
 * Centralizar estas asignaciones en una clase de constantes mejora la mantenibilidad del código
 * y permite una fácil referencia en cualquier parte del juego donde se necesiten controles del jugador.
 */
public class PlayerControl {
    /**
     * Tecla asignada para la acción de moverse hacia la **izquierda**.
     * Por defecto, se establece en la tecla 'A'.
     */
    public static int LEFT = Input.Keys.A;
    /**
     * Tecla asignada para la acción de moverse hacia la **derecha**.
     * Por defecto, se establece en la tecla 'D'.
     */
    public static int RIGHT = Input.Keys.D;
    /**
     * Tecla asignada para la acción de **saltar**.
     * Por defecto, se establece en la tecla 'W'.
     */
    public static int JUMP = Input.Keys.W;
    /**
     * Tecla asignada para la acción de moverse **hacia abajo** o agacharse.
     * Por defecto, se establece en la tecla 'S'.
     */
    public static int DOWN = Input.Keys.S;
    /**
     * Tecla asignada para la acción de **correr** o esprintar.
     * Por defecto, se establece en la tecla 'SHIFT_LEFT' (Shift izquierdo).
     */
    public static int RUN = Input.Keys.SHIFT_LEFT;
    /**
     * Tecla asignada para una acción genérica de **interacción** o activación.
     * Por defecto, se establece en la tecla 'P'.
     */
    public static int ACTION = Input.Keys.P;
    /**
     * Tecla asignada para la acción de **soltar** un objeto o item.
     * Por defecto, se establece en la tecla 'CONTROL_LEFT' (Control izquierdo).
     */
    public static int DROP = Input.Keys.CONTROL_LEFT;
}
