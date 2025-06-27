package src.utils.stateMachine;

/**
 * La interfaz `State` define el contrato para cualquier estado dentro de una máquina de estados.
 * Cada clase que implemente esta interfaz debe proporcionar una lógica específica
 * para el inicio, la actualización y el final de ese estado. Este patrón es fundamental
 * para gestionar el comportamiento de objetos (como jugadores, enemigos, o la propia aplicación)
 * que pueden tener diferentes modos operativos a lo largo del tiempo.
 */
public interface State {

    /**
     * Este método se invoca una vez cuando el estado se activa o se entra en él.
     * Es ideal para inicializar recursos específicos del estado, configurar variables,
     * o realizar acciones que solo deben ocurrir al comienzo del estado.
     */
    void start();

    /**
     * Este método se invoca repetidamente en cada ciclo de actualización del juego
     * mientras el estado está activo. Contiene la lógica principal del estado,
     * como la gestión de entradas del usuario, la actualización de la lógica del juego,
     * el movimiento de objetos, etc.
     * @param delta El tiempo transcurrido desde el último frame en segundos.
     */
    void update(Float delta);

    /**
     * Este método se invoca una vez cuando el estado se desactiva o se sale de él.
     * Es útil para limpiar recursos, restablecer variables, detener animaciones
     * o realizar cualquier acción necesaria antes de pasar a otro estado.
     */
    void end();
}
