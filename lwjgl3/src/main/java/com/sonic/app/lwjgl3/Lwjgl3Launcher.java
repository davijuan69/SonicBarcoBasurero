package com.sonic.app.lwjgl3; // Declara el paquete al que pertenece esta clase.
// Este paquete suele contener el código específico para la plataforma de escritorio (LWJGL3).

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application; // Importa la clase principal para iniciar una aplicación LibGDX en el escritorio.
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration; // Importa la clase para configurar la aplicación de escritorio (ventana, VSync, etc.).
import com.sonic.app.Main; // Importa la clase 'Main' de tu proyecto, que contiene la lógica principal de la aplicación LibGDX.

/**
 * Lanza la aplicación de escritorio (usando la biblioteca LWJGL3).
 * Esta es la clase principal para ejecutar el juego en plataformas de escritorio (Windows, macOS, Linux).
 */
public class Lwjgl3Launcher {
    /**
     * El método 'main' es el punto de entrada de cualquier aplicación Java.
     * Cuando ejecutas el proyecto en el escritorio, este es el primer método que se llama.
     * @param args Argumentos de la línea de comandos pasados al programa (no se usan en este caso).
     */
    public static void main(String[] args) {
        // Esta línea es un helper para manejar el soporte de macOS y también ayuda en Windows.
        // Si se requiere iniciar una nueva JVM (máquina virtual de Java), esta función lo hace y devuelve 'true',
        // evitando que el resto del código se ejecute en la JVM actual si se ha iniciado una nueva.
        if (StartupHelper.startNewJvmIfRequired()) return; // Si la JVM se reinicia, simplemente regresa.
        createApplication(); // Llama al método para crear y lanzar la aplicación LibGDX.
    }

    /**
     * Crea y devuelve una instancia de Lwjgl3Application, que es la aplicación de escritorio LibGDX.
     * Asocia la lógica principal de tu juego (la clase Main) con la configuración de la ventana.
     * @return Una instancia de Lwjgl3Application lista para ser lanzada.
     */
    private static Lwjgl3Application createApplication() {
        // Retorna una nueva aplicación LibGDX de escritorio.
        // Se le pasa una nueva instancia de 'Main' (tu juego) y la configuración por defecto.
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    /**
     * Configura los parámetros de la ventana y el rendimiento de la aplicación de escritorio.
     * Este método define cómo se comportará la ventana del juego.
     * @return Un objeto Lwjgl3ApplicationConfiguration con la configuración deseada.
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        // Crea una nueva instancia de Lwjgl3ApplicationConfiguration para establecer los ajustes.
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        // Establece el título de la ventana de la aplicación. Este texto aparecerá en la barra de título de la ventana.
        configuration.setTitle("Sonic Barco Basurero");
        // Habilita VSync (Sincronización Vertical). Esto limita los fotogramas por segundo (FPS)
        // a la tasa de refresco del monitor, lo que ayuda a eliminar el "screen tearing" (desgarro de pantalla).
        configuration.useVsync(true);
        // Establece el límite de FPS en primer plano. Se ajusta a la tasa de refresco del monitor actual
        // más 1 para intentar igualar tasas de refresco fraccionales. VSync debería limitar los FPS reales.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        // Si se desactiva VSync (useVsync(false)) y se comenta la línea anterior, se pueden obtener FPS ilimitados.
        // Esto es útil para pruebas de rendimiento, pero puede estresar el hardware.
        // También puede ser necesario configurar los drivers de la GPU para desactivar VSync completamente.
        // Establece el tamaño inicial de la ventana en modo ventana a 640 píxeles de ancho por 480 píxeles de alto.
        configuration.setWindowedMode(640, 480);
        // Puedes cambiar estos archivos; se encuentran en lwjgl3/src/main/resources/ .
        // También pueden cargarse desde la raíz de la carpeta 'assets/'.
        // Establece el icono de la ventana de la aplicación usando la imagen "libgdx128.jpg".
        // Esta imagen se buscará en los recursos o en la carpeta 'assets'.
        configuration.setWindowIcon("libgdx128.jpg");
        return configuration; // Devuelve el objeto de configuración con todos los ajustes aplicados.
    }
}
