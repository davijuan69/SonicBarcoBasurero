package src.utils.constants;

import com.badlogic.gdx.graphics.Color;

/**
 * La clase `MyColors` define constantes de tipo `com.badlogic.gdx.graphics.Color`
 * para colores personalizados que se utilizarán en la aplicación o juego.
 * Esto centraliza la definición de colores específicos y permite una fácil modificación
 * y consistencia en el diseño visual.
 */
public class MyColors {
    /**
     * Define un color **amarillo** personalizado.
     * Los valores (R, G, B, A) están en el rango de 0.0f a 1.0f,
     * donde R=1.0f (rojo máximo), G=1.0f (verde máximo), B=0.02f (azul mínimo) y A=1.0f (opacidad total).
     * Este amarillo tiene un ligero toque de azul para distinguirlo de un amarillo puro (1,1,0,1).
     */
    public static final Color YELLOW = new Color(1f,1f,0.02f,1);
    /**
     * Define un color **azul** personalizado.
     * Los valores (R, G, B, A) están en el rango de 0.0f a 1.0f,
     * donde R=0.01f (rojo mínimo), G=0.15f (verde bajo), B=0.58f (azul moderado) y A=1.0f (opacidad total).
     * Este azul es un tono oscuro y profundo.
     */
    public static final Color BLUE = new Color(0.01f,0.15f,0.58f,1);
    /**
     * Define un color **rojo** personalizado.
     * Los valores (R, G, B, A) están en el rango de 0.0f a 1.0f,
     * donde R=1.0f (rojo máximo), G=0.0f (verde nulo), B=0.0f (azul nulo) y A=1.0f (opacidad total).
     * Este es un rojo puro y brillante.
     */
    public static final Color RED = new Color(1f, 0f,0f, 1);
}
