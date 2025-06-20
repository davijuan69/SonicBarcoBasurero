package src.utils.constants;

/**
 * La clase `CollisionFilters` define constantes `short` que representan categorías de filtro de colisión.
 * Estas constantes están diseñadas para ser utilizadas en sistemas de detección de colisiones (como Box2D)
 * donde las colisiones se gestionan mediante máscaras de bits.
 * Cada constante es una potencia de 2 (o una combinación de ellas), asegurando que cada categoría
 * tenga un bit único asignado, lo que permite combinaciones bit a bit para definir qué categorías
 * interactúan entre sí.
 */
public class CollisionFilters {
    /**
     * Filtro de colisión para la entidad del **jugador principal**.
     * Representado por el bit 0 (0000 0000 0000 0001 en binario).
     */
    public static final short PLAYER = Byte.parseByte("1", 2);
    /**
     * Filtro de colisión para **otros jugadores** en un contexto multijugador.
     * Representado por el bit 1 (0000 0000 0000 0010 en binario).
     */
    public static final short OTHERPLAYER = Byte.parseByte("10", 2);
    /**
     * Filtro de colisión para entidades **enemigas**.
     * Representado por el bit 2 (0000 0000 0000 0100 en binario).
     */
    public static final short ENEMY = Byte.parseByte("100", 2);
    /**
     * Filtro de colisión para **objetos estáticos** en el entorno (por ejemplo, paredes, suelo).
     * Representado por el bit 3 (0000 0000 0000 1000 en binario).
     */
    public static final short STATIC = Byte.parseByte("1000", 2);
    /**
     * Filtro de colisión para **ítems** que pueden ser recolectados o interactuados.
     * Representado por el bit 4 (0000 0000 0001 0000 en binario).
     */
    public static final short ITEM = Byte.parseByte("10000", 2);
    /**
     * Filtro de colisión para **proyectiles** (por ejemplo, balas, flechas).
     * Representado por el bit 5 (0000 0000 0010 0000 en binario).
     */
    public static final short PROJECTIL = Byte.parseByte("100000", 2);
    /**
     * Filtro de colisión para **plataformas móviles**.
     * Este valor es una combinación de bits, lo que indica que una plataforma móvil
     * podría ser tratada como un objeto estático en ciertos aspectos, pero con la adición
     * de su propio bit para diferenciar su comportamiento (0000 0000 0010 0001 en binario).
     * **Nota:** La representación binaria `100001` es `33` en decimal, que es `100000` (PROJECTIL) + `1` (PLAYER).
     * Esto podría ser un error si se pretendía que fuera un bit único o una combinación lógica diferente.
     * Si se pretendía un bit único, el valor debería ser una potencia de 2, por ejemplo, `Byte.parseByte("1000000", 2)`.
     */
    public static final short MVINGPLAT = Byte.parseByte("100001", 2);
}
