package com.sonic.app.utils; // Declara el paquete donde se encuentra esta clase.

import com.badlogic.gdx.physics.box2d.World;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * La clase `ThreadSecureWorld` envuelve un objeto `World` de Box2D y proporciona una forma segura para hilos
 * de realizar modificaciones en el mundo físico.
 * Box2D no es seguro para hilos, lo que significa que no se deben realizar cambios en el mundo
 * (como crear o destruir cuerpos/fixtures) desde múltiples hilos simultáneamente, o mientras
 * el mundo está realizando su simulación (`world.step()`).
 * Esta clase aborda ese problema encolando las modificaciones y aplicándolas solo en un momento seguro,
 * generalmente después de que el paso de simulación haya terminado.
 */
public class ThreadSecureWorld {
    private final World world; // La instancia del mundo de Box2D que esta clase envuelve. Es final porque no cambiará después de la inicialización.
    private final ConcurrentLinkedQueue<Runnable> modificationQueue; // Una cola de tareas (Runnable) que representan modificaciones al mundo.
    // `ConcurrentLinkedQueue` es segura para hilos, lo que permite que
    // diferentes hilos añadan modificaciones sin problemas de concurrencia.

    /**
     * Constructor para la clase `ThreadSecureWorld`.
     *
     * @param world La instancia del `World` de Box2D que se va a hacer segura para hilos.
     */
    public ThreadSecureWorld(World world) {
        this.world = world; // Inicializa la instancia del mundo de Box2D.
        this.modificationQueue = new ConcurrentLinkedQueue<>(); // Inicializa la cola de modificaciones.
    }

    /**
     * Realiza un paso de simulación en el mundo de Box2D y luego aplica todas las modificaciones encoladas.
     * Es crucial que este método se llame desde un solo hilo (generalmente el hilo de renderizado/juego principal)
     * para asegurar la seguridad de los hilos de Box2D.
     *
     * @param delta El tiempo transcurrido desde el último paso (generalmente en segundos).
     * @param velocityIterations El número de iteraciones para resolver las velocidades.
     * @param positionIterations El número de iteraciones para resolver las posiciones.
     */
    public void step(float delta, int velocityIterations, int positionIterations) {
        world.step(delta, velocityIterations, positionIterations); // Realiza un paso de simulación del mundo de Box2D.
        // Esto no debe ser interrumpido por modificaciones externas.

        // Después de que el paso de simulación ha terminado, procesa todas las modificaciones encoladas.
        while (!modificationQueue.isEmpty()) { // Mientras haya modificaciones en la cola...
            Runnable run = modificationQueue.poll(); // Obtiene y elimina la primera modificación de la cola.
            if (run == null) continue; // Si por alguna razón 'poll()' devuelve null (raro en ConcurrentLinkedQueue a menos que esté vacía), salta.
            run.run(); // Ejecuta la modificación. Esto suele ser la creación o destrucción de cuerpos/fixtures.
        }
    }

    /**
     * Añade una modificación al mundo de Box2D a la cola.
     * Este método puede ser llamado de forma segura desde cualquier hilo.
     * La modificación se ejecutará en el siguiente `step()` del mundo.
     *
     * @param modification Un objeto `Runnable` que contiene el código para realizar la modificación.
     */
    public void addModification(Runnable modification) {
        modificationQueue.add(modification); // Añade el 'Runnable' a la cola. La operación 'add' de ConcurrentLinkedQueue es segura para hilos.
    }

    /**
     * Limpia todas las modificaciones pendientes en la cola.
     * Las modificaciones ya encoladas no se ejecutarán si se llama a este método antes del próximo `step()`.
     */
    public void clearModifications() {
        modificationQueue.clear(); // Vacía la cola de modificaciones.
    }

    /**
     * Obtiene el número de modificaciones actualmente encoladas.
     *
     * @return El número de modificaciones pendientes.
     */
    public Integer getAmountModifications(){
        return modificationQueue.size(); // Devuelve el tamaño actual de la cola.
    }
}
