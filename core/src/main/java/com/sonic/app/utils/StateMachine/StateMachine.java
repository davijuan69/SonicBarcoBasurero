package com.sonic.app.utils.StateMachine;

/**
 * La clase `StateMachine` implementa el patrón de diseño de Máquina de Estados.
 * Permite a un objeto cambiar su comportamiento interno basándose en un estado actual,
 * proporcionando una forma estructurada de gestionar transiciones entre diferentes lógicas
 * o modos de operación.
 */
public class StateMachine {
    private State currentState; // El estado actual en el que se encuentra la máquina de estados.

    /**
     * Constructor para la `StateMachine`.
     * Inicializa la máquina sin un estado actual definido. Se debe establecer un estado inicial
     * usando `setState()` después de la creación.
     */
    public StateMachine() {
        // No se requiere inicialización de `currentState` aquí, ya que se manejará con `setState`.
    }

    /**
     * Cambia el estado actual de la máquina a un nuevo estado.
     * Si ya hay un estado activo, su método `end()` es llamado antes de que el nuevo estado
     * entre en juego llamando a su método `start()`.
     * @param newState La nueva instancia de `State` a la que se desea transicionar.
     */
    public void setState(State newState) {
        // Valida que el nuevo estado no sea nulo para evitar errores.
        if (newState == null) return;
        // Si hay un estado actual, llama a su método `end()` para realizar la limpieza.
        if (currentState != null) currentState.end();
        // Asigna el nuevo estado como el estado actual.
        currentState = newState;
        // Llama al método `start()` del nuevo estado para inicializar su lógica.
        currentState.start();
    }

    /**
     * Obtiene el estado actual de la máquina.
     * @return La instancia de `State` que representa el estado actual.
     */
    public State getState(){
        return currentState;
    }

    /**
     * Delega la llamada al método `start()` del estado actual.
     * Este método es redundante si `setState` ya llama a `start()`.
     * Podría ser útil si el estado necesita reiniciarse explícitamente sin cambiarlo.
     */
    public void start() {
        currentState.start();
    }

    /**
     * Delega la llamada al método `update()` del estado actual.
     * Este método debe ser llamado en el bucle principal del juego para permitir
     * que el estado actual ejecute su lógica de actualización.
     * @param delta El tiempo transcurrido desde el último frame en segundos.
     */
    public void update(Float delta) {
        currentState.update(delta);
    }

    /**
     * Delega la llamada al método `end()` del estado actual.
     * Similar a `start()`, este método es parcialmente redundante si `setState` ya llama a `end()`.
     * Podría usarse para una limpieza explícita del estado actual sin una transición.
     */
    public void end() {
        currentState.end();
    }
}
