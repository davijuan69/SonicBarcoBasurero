package src.screens.components.chat; // Declara el paquete donde se encuentra esta clase, indicando que es un componente de chat.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

/**
 * La clase `Message` extiende `Label` y representa un mensaje de chat individual
 * que tiene una vida útil limitada y se desvanece gradualmente antes de desaparecer.
 */
public class Message extends Label {
    // Estas variables de instancia no se usan realmente ya que los valores están cableados en el método draw().
    // Podrían eliminarse o utilizarse para hacer el comportamiento del desvanecimiento configurable.
    private Integer maxTime;   // El tiempo máximo que el mensaje estará visible antes de desaparecer completamente (en segundos).
    private Integer opaqueTime; // El tiempo durante el cual el mensaje permanece completamente opaco antes de empezar a desvanecerse (en segundos).

    private Float timeDespawn; // Un temporizador que cuenta el tiempo transcurrido desde que se creó el mensaje.

    /**
     * Constructor principal para la clase `Message`.
     * Inicializa el mensaje con el texto, estilo, tiempo máximo de vida y tiempo opaco.
     *
     * @param text El contenido del mensaje.
     * @param style El estilo de la etiqueta (fuente, color, etc.) del mensaje.
     * @param maxTime El tiempo total, en segundos, que el mensaje permanecerá antes de ser eliminado.
     * @param opaqueTime El tiempo, en segundos, durante el cual el mensaje es completamente opaco antes de empezar a desvanecerse.
     */
    public Message(String text, LabelStyle style, Integer maxTime, Integer opaqueTime) {
        super(text, style); // Llama al constructor de la clase padre (Label) con el texto y el estilo.
        this.maxTime = maxTime;     // Asigna el tiempo máximo de vida.
        this.opaqueTime = opaqueTime; // Asigna el tiempo opaco.
        timeDespawn = 0f;           // Inicializa el temporizador de desvanecimiento a cero.
        setAlignment(Align.topLeft); // Alinea el texto del mensaje en la parte superior izquierda de su área.
    }

    /**
     * Constructor secundario para la clase `Message`.
     * Utiliza valores predeterminados para `maxTime` (10 segundos) y `opaqueTime` (7 segundos).
     *
     * @param text El contenido del mensaje.
     * @param style El estilo de la etiqueta (fuente, color, etc.) del mensaje.
     */
    public Message(String text, LabelStyle style) {
        this(text, style, 10, 7); // Llama al constructor principal con valores predeterminados para los tiempos.
    }

    /**
     * Sobrescribe el método `draw` de `Actor` (heredado a través de `Label`).
     * Este método se encarga de dibujar el mensaje en la pantalla y gestionar su desvanecimiento.
     *
     * @param batch El `Batch` a utilizar para dibujar.
     * @param parentAlpha La opacidad del padre.
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Label).
    public void draw(Batch batch, float parentAlpha) {
        // Nota: Las variables locales 'maxTime' y 'opaqueTime' dentro de este método
        // están hardcodeadas a 10 y 7 respectivamente, sobrescribiendo efectivamente
        // los valores de las variables de instancia con el mismo nombre.
        // Esto significa que los valores pasados en los constructores para maxTime y opaqueTime
        // no tienen ningún efecto en la lógica de desvanecimiento.
        int maxTime = 10;   // Tiempo total de vida del mensaje en segundos (fijo en 10s).
        int opaqueTime = 7; // Tiempo en segundos que el mensaje permanece completamente opaco (fijo en 7s).

        // Incrementa el temporizador de desvanecimiento con el tiempo transcurrido desde el último fotograma.
        timeDespawn += Gdx.graphics.getDeltaTime();

        float alpha = 1f; // Inicializa la opacidad a completamente opaca.
        // Si el tiempo transcurrido supera el tiempo opaco, el mensaje comienza a desvanecerse.
        if (timeDespawn > opaqueTime) {
            // Calcula la opacidad basándose en la proporción del tiempo transcurrido desde que
            // empezó el desvanecimiento hasta el final del tiempo de vida del mensaje.
            // `Math.max(0, ...)` asegura que la opacidad no caiga por debajo de cero.
            alpha = Math.max(0, 1 - ((timeDespawn - opaqueTime) / (maxTime - opaqueTime)));
        }

        getColor().a = alpha; // Establece el componente alfa del color del Label. Esto afecta su opacidad.
        super.draw(batch, parentAlpha * alpha); // Llama al método draw de la clase padre (Label) para dibujar el texto,
        // aplicando la opacidad calculada y la opacidad del padre.

        // Si el temporizador ha alcanzado o superado el tiempo máximo, elimina el mensaje del Stage.
        if (timeDespawn >= maxTime) remove(); // `remove()` elimina el Actor de su padre (la Tabla Chat en este caso).
    }
}
