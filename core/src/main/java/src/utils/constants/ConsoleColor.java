package src.utils.constants;

/**
 * La clase `ConsoleColor` define constantes de cadena que representan códigos de escape ANSI
 * para cambiar el color del texto en la salida de la consola.
 * Estos códigos son ampliamente soportados por la mayoría de las terminales modernas
 * y permiten colorear la salida para mejorar la legibilidad o resaltar información.
 */
public class ConsoleColor {
    /**
     * Restablece el color del texto de la consola a su color predeterminado.
     * Siempre se debe usar después de aplicar un color para evitar que el color se aplique
     * a todo el texto posterior.
     */
    public static final String RESET = "\033[0m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **negro**.
     */
    public static final String BLACK = "\033[0;30m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **rojo**.
     */
    public static final String RED = "\033[0;31m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **verde**.
     */
    public static final String GREEN = "\033[0;32m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **amarillo**.
     */
    public static final String YELLOW = "\033[0;33m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **azul**.
     */
    public static final String BLUE = "\033[0;34m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **púrpura** (magenta).
     */
    public static final String PURPLE = "\033[0;35m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **cian**.
     */
    public static final String CYAN = "\033[0;36m";
    /**
     * Código de escape ANSI para cambiar el color del texto a **gris** (blanco claro).
     */
    public static final String GRAY = "\033[0;37m";
}
