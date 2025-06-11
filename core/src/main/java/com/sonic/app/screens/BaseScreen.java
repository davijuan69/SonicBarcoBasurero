package com.sonic.app.screens; // Declara el paquete donde se encuentra esta clase.

import com.badlogic.gdx.Screen;
import com.sonic.app.main.Main;

/**
 * Clase abstracta BaseScreen.
 * Sirve como una plantilla o un esqueleto para todas las pantallas de un juego o aplicación LibGDX.
 * Implementa la interfaz `Screen` y proporciona una referencia a la clase `Main` del juego.
 * Al ser abstracta, no se pueden crear instancias directamente de `BaseScreen`;
 * se deben crear subclases que la extiendan e implementen sus métodos abstractos (si los hubiera)
 * o sobrescriban sus métodos predeterminados.
 */
public abstract class BaseScreen implements Screen {
    public Main main; // Declara una variable pública para mantener una referencia a la instancia principal del juego (Main).
    // Esto permite que todas las pantallas accedan a recursos globales o lógicas de la aplicación.

    /**
     * Constructor para la clase BaseScreen.
     * Todas las subclases de BaseScreen deben llamar a este constructor.
     *
     * @param main La instancia principal del juego (Main), necesaria para que la pantalla interactúe con el juego global.
     */
    public BaseScreen(Main main) {
        this.main = main; // Asigna la instancia de 'Main' pasada como argumento a la variable de instancia 'main'.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'show()'.
     * Este método se llama cuando esta pantalla se convierte en la pantalla activa.
     * Es un buen lugar para inicializar recursos específicos de la pantalla, configurar listeners, etc.
     * Por defecto, no hace nada, pero las subclases pueden sobrescribirlo para añadir funcionalidad.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void show() {
        // Implementación vacía por defecto. Las subclases pueden sobrescribir esto.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'render(float delta)'.
     * Este método se llama cada fotograma para dibujar la pantalla y actualizar su lógica.
     *
     * @param delta El tiempo transcurrido en segundos desde el último fotograma.
     * Por defecto, no hace nada, pero las subclases deben sobrescribirlo para implementar la lógica de renderizado y actualización.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void render(float delta) {
        // Implementación vacía por defecto. Las subclases DEBEN sobrescribir esto para dibujar y actualizar.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'resize(int width, int height)'.
     * Este método se llama cuando la ventana del juego cambia de tamaño.
     * Útil para ajustar la vista de la cámara o la disposición de los elementos de la interfaz de usuario.
     *
     * @param width El nuevo ancho de la ventana.
     * @param height La nueva altura de la ventana.
     * Por defecto, no hace nada, pero las subclases pueden sobrescribirlo.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void resize(int width, int height) {
        // Implementación vacía por defecto. Las subclases pueden sobrescribir esto.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'pause()'.
     * Este método se llama cuando la aplicación es pausada (ej., cuando se pierde el foco en el escritorio,
     * o la aplicación se minimiza en móvil).
     * Es un buen lugar para guardar el estado del juego o liberar recursos que no son esenciales mientras está en pausa.
     * Por defecto, no hace nada, pero las subclases pueden sobrescribirlo.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void pause() {
        // Implementación vacía por defecto. Las subclases pueden sobrescribir esto.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'resume()'.
     * Este método se llama cuando la aplicación es reanudada después de haber sido pausada.
     * Es un buen lugar para recargar recursos que fueron liberados en 'pause()'.
     * Por defecto, no hace nada, pero las subclases pueden sobrescribirlo.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void resume() {
        // Implementación vacía por defecto. Las subclases pueden sobrescribir esto.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'hide()'.
     * Este método se llama cuando esta pantalla ya no es la pantalla activa (ej., cuando se establece otra pantalla).
     * Es un buen lugar para liberar recursos que solo son necesarios mientras esta pantalla está activa.
     * Por defecto, no hace nada, pero las subclases pueden sobrescribirlo.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void hide() {
        // Implementación vacía por defecto. Las subclases pueden sobrescribir esto.
    }

    /**
     * Método del ciclo de vida de la pantalla: 'dispose()'.
     * Este método se llama cuando la pantalla ya no es necesaria y sus recursos deben ser liberados.
     * Esto sucede cuando la aplicación se cierra o cuando la pantalla se destruye explícitamente.
     * Es crucial liberar todos los recursos cargados (ej. texturas, sonidos) para evitar fugas de memoria.
     * Por defecto, no hace nada, pero las subclases deben sobrescribirlo para limpiar recursos.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Screen.
    public void dispose() {
        // Implementación vacía por defecto. Las subclases DEBEN sobrescribir esto para liberar recursos.
    }
}
