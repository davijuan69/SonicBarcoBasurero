package src.utils; // Declara el paquete donde se encuentra esta clase. Las clases de utilidad a menudo se agrupan aquí.

// Declara la clase ScorePlayer. Esta clase representa la puntuación de un jugador.
// Implementa la interfaz Comparable<ScorePlayer>, lo que significa que los objetos ScorePlayer
// pueden compararse entre sí, típicamente para fines de ordenación.
public class ScorePlayer implements Comparable<ScorePlayer> {
    public Integer id; // Declara una variable pública para almacenar el ID del jugador. Utiliza Integer para permitir valores nulos si es necesario.
    public String name; // Declara una variable pública para almacenar el nombre del jugador.
    public Integer score; // Declara una variable pública para almacenar la puntuación del jugador. Utiliza Integer.

    /**
     * Constructor para la clase ScorePlayer.
     * Inicializa un nuevo objeto ScorePlayer con un ID y un nombre dados, y establece la puntuación inicial en 0.
     *
     * @param id El identificador único del jugador.
     * @param name El nombre del jugador.
     */
    public ScorePlayer(Integer id, String name) {
        this.id = id; // Asigna el ID pasado como argumento a la variable de instancia 'id'.
        this.name = name; // Asigna el nombre pasado como argumento a la variable de instancia 'name'.
        this.score = 0; // Inicializa la puntuación del jugador a cero.
    }

    /**
     * Implementación del método compareTo de la interfaz Comparable.
     * Este método define cómo se comparan dos objetos ScorePlayer.
     * En este caso, compara las puntuaciones de los jugadores para ordenar de forma descendente (la puntuación más alta primero).
     *
     * @param o El objeto ScorePlayer con el que se va a comparar este objeto.
     * @return Un valor negativo si la puntuación de 'o' es menor que la de este objeto,
     * cero si son iguales, o un valor positivo si la puntuación de 'o' es mayor.
     * Esto logra un orden descendente porque estamos llamando 'o.score.compareTo(score)'
     * en lugar de 'score.compareTo(o.score)'.
     */
    @Override // Indica que este método anula un método de una superclase o interfaz.
    public int compareTo(ScorePlayer o) {
        // Compara la puntuación del otro jugador ('o') con la puntuación de este jugador.
        // El orden se invierte intencionadamente (o.score.compareTo(score)) para ordenar
        // los jugadores de mayor a menor puntuación.
        return o.score.compareTo(score);
    }
}
