/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//Nota: la licencia y los derechos de autor anteriores se aplican solo a este archivo.

package src.lwjgl3; // Declara el paquete al que pertenece esta clase.

import com.badlogic.gdx.Version; // Importa la clase Version de LibGDX para obtener la versión actual.
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader; // Importa el cargador de librerías nativas de LWJGL3.
import org.lwjgl.system.macosx.LibC; // Importa la clase LibC para funciones de sistema en macOS (como getpid).
import org.lwjgl.system.macosx.ObjCRuntime; // Importa la clase ObjCRuntime para interactuar con el Objective-C Runtime en macOS.

import java.io.BufferedReader; // Importa BufferedReader para leer la salida de procesos.
import java.io.File; // Importa File para manejar archivos y rutas.
import java.io.InputStreamReader; // Importa InputStreamReader para convertir InputStream a Reader.
import java.lang.management.ManagementFactory; // Importa ManagementFactory para acceder a la JVM de forma programática (ej. argumentos de entrada).
import java.util.ArrayList; // Importa ArrayList para usar listas dinámicas.

import static org.lwjgl.system.JNI.invokePPP; // Importa una función JNI para invocar métodos con tres punteros como argumentos.
import static org.lwjgl.system.JNI.invokePPZ; // Importa una función JNI para invocar métodos con dos punteros y un booleano como argumentos.
import static org.lwjgl.system.macosx.ObjCRuntime.objc_getClass; // Importa una función para obtener una clase de Objective-C.
import static org.lwjgl.system.macosx.ObjCRuntime.sel_getUid; // Importa una función para obtener el UID de un selector de Objective-C.

/**
 * Agrega algunas utilidades para asegurar que la JVM se inició con el
 * argumento {@code -XstartOnFirstThread}, que es requerido en macOS para que LWJGL 3
 * funcione. También ayuda en Windows cuando los nombres de usuario contienen caracteres
 * fuera del alfabeto latino, una causa común de fallos al iniciar.
 * <br>
 * <a href="https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Basado en esta publicación de java-gaming.org por kappa</a>
 * @author damios
 */
public class StartupHelper {

    // Argumento de propiedad del sistema que se establece cuando la JVM es reiniciada,
    // para evitar un bucle infinito de reinicios.
    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    // Constructor privado para evitar la instanciación de esta clase de utilidad.
    private StartupHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Inicia una nueva JVM si la aplicación fue iniciada en macOS sin el
     * argumento {@code -XstartOnFirstThread}. Esto también incluye código para
     * Windows, para el caso en que el directorio de inicio del usuario incluya ciertos
     * caracteres no latinos (sin este código, la mayoría de las aplicaciones LWJGL3 fallan
     * inmediatamente para esos usuarios). Retorna si se inició una nueva JVM y
     * por lo tanto, no se debe ejecutar más código en la JVM actual.
     * <p>
     * <u>Uso:</u>
     *
     * <pre><code>
     * public static void main(String... args) {
     * if (StartupHelper.startNewJvmIfRequired(true)) return; // Esto maneja el soporte de macOS y ayuda en Windows.
     * // después de esto va el código real del método main
     * }
     * </code></pre>
     *
     * @param redirectOutput
     * Si la salida de la nueva JVM debe ser redirigida a la
     * antigua JVM, para que pueda ser accedida en el mismo lugar;
     * mantiene la JVM antigua en ejecución si está habilitado.
     * @return Si se inició una nueva JVM y, por lo tanto, no se debe ejecutar
     * más código en esta.
     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        // Obtiene el nombre del sistema operativo y lo convierte a minúsculas.
        String osName = System.getProperty("os.name").toLowerCase();
        // Comprueba si el sistema operativo NO es macOS.
        if (!osName.contains("mac")) {
            // Si el sistema operativo es Windows.
            if (osName.contains("windows")) {
// Aquí, estamos intentando solucionar un problema con la forma en que LWJGL3 carga sus archivos .dll extraídos.
// Por defecto, LWJGL3 extrae al directorio especificado por "java.io.tmpdir", que suele ser el directorio de inicio del usuario.
// Si el nombre del usuario tiene caracteres no ASCII (o algunos no alfanuméricos), eso fallaría.
// Al extraer a la carpeta "ProgramData" relevante, que suele ser "C:\ProgramData", evitamos esto.
// También cambiamos temporalmente la propiedad "user.name" a una sin caracteres inválidos.
// Revertimos nuestros cambios inmediatamente después de cargar las librerías nativas de LWJGL3.
                // Obtiene la ruta de la variable de entorno ProgramData.
                String programData = System.getenv("ProgramData");
                // Si ProgramData no está establecido, intenta usar una ruta alternativa.
                if(programData == null) programData = "C:\\Temp\\";
                // Guarda la ruta temporal actual del sistema y el nombre de usuario actual.
                String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
                String prevUser = System.getProperty("user.name", "libGDX_User");
                // Establece una nueva ruta temporal para la extracción de librerías, dentro de ProgramData.
                System.setProperty("java.io.tmpdir", programData + "/libGDX-temp");
                // Cambia el nombre de usuario a una versión segura (sin caracteres problemáticos) para la carga de nativas.
                System.setProperty("user.name", ("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION).replace('.', '_'));
                // Carga las librerías nativas de LWJGL3. Esto ocurrirá con las rutas temporales modificadas.
                Lwjgl3NativesLoader.load();
                // Restaura la ruta temporal original del sistema.
                System.setProperty("java.io.tmpdir", prevTmpDir);
                // Restaura el nombre de usuario original del sistema.
                System.setProperty("user.name", prevUser);
            }
            return false; // Si no es macOS, no se necesita reiniciar la JVM (excepto el arreglo de Windows que ya se hizo).
        }

        // No es necesario el argumento -XstartOnFirstThread en una imagen nativa de GraalVM.
        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        // Comprueba si ya estamos en el hilo principal, por ejemplo, al ejecutar a través de Construo.
        // Obtiene la dirección de la función "objc_msgSend" del Objective-C Runtime.
        long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
        // Obtiene la clase "NSThread" del Objective-C Runtime.
        long NSThread      = objc_getClass("NSThread");
        // Obtiene una referencia al hilo actual usando Objective-C.
        long currentThread = invokePPP(NSThread, sel_getUid("currentThread"), objc_msgSend);
        // Llama al método "isMainThread" en el hilo actual para verificar si es el hilo principal.
        boolean isMainThread = invokePPZ(currentThread, sel_getUid("isMainThread"), objc_msgSend);
        // Si ya estamos en el hilo principal, no es necesario reiniciar la JVM.
        if(isMainThread) return false;

        // Obtiene el ID del proceso actual.
        long pid = LibC.getpid();

        // Comprueba si la variable de entorno "JAVA_STARTED_ON_FIRST_THREAD_<pid>" está establecida a "1".
        // Esto indica que la JVM ya fue iniciada con -XstartOnFirstThread.
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false; // Si ya está, no es necesario hacer nada.
        }

        // Comprueba si la propiedad del sistema "JVM_RESTARTED_ARG" (que se estableció previamente)
        // está establecida a "true". Esto evita un bucle infinito si el reinicio falla.
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                "Hubo un problema al evaluar si la JVM se inició con el argumento -XstartOnFirstThread.");
            return false; // Si la JVM ya intentó reiniciarse y algo falló, se informa y se evita un nuevo intento.
        }

        // Reinicia la JVM con el argumento -XstartOnFirstThread
        ArrayList<String> jvmArgs = new ArrayList<>(); // Lista para almacenar los argumentos de la nueva JVM.
        // Determina el separador de archivos del sistema (ej. "/" en Unix, "\" en Windows).
        String separator = System.getProperty("file.separator", "/");
        // La siguiente línea se usa asumiendo que apuntas a Java 8, el mínimo para LWJGL3.
        // Construye la ruta al ejecutable de Java.
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
        // Si se apunta a Java 9 o superior, se podría usar la siguiente línea en lugar de la anterior:
        //String javaExecPath = ProcessHandle.current().info().command().orElseThrow();

        // Comprueba si la ruta del ejecutable de Java existe.
        if (!(new File(javaExecPath)).exists()) {
            System.err.println(
                "No se pudo encontrar una instalación de Java. Si estás distribuyendo esta aplicación con un JRE incluido, ¡asegúrate de establecer el argumento -XstartOnFirstThread manualmente!");
            return false; // Si no se encuentra Java, no se puede reiniciar la JVM.
        }

        jvmArgs.add(javaExecPath); // Agrega la ruta del ejecutable de Java como el primer argumento.
        jvmArgs.add("-XstartOnFirstThread"); // Agrega el argumento crucial para macOS.
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true"); // Agrega la propiedad para marcar que la JVM ha sido reiniciada.
        // Agrega todos los argumentos de entrada de la JVM actual a la lista de argumentos para la nueva JVM.
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp"); // Agrega el argumento para la ruta de clases.
        jvmArgs.add(System.getProperty("java.class.path")); // Agrega la ruta de clases de la JVM actual.
        // Intenta determinar la clase principal que se está ejecutando.
        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid); // Primero, intenta obtenerla de una variable de entorno.
        if (mainClass == null) { // Si no se encuentra en la variable de entorno.
            StackTraceElement[] trace = Thread.currentThread().getStackTrace(); // Obtiene el stack trace del hilo actual.
            if (trace.length > 0) {
                // Obtiene el nombre de la clase principal del último elemento del stack trace.
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("No se pudo determinar la clase principal.");
                return false; // Si no se puede determinar la clase principal, no se puede reiniciar la JVM.
            }
        }
        jvmArgs.add(mainClass); // Agrega el nombre de la clase principal a los argumentos.

        try {
            // Si no se debe redirigir la salida (la nueva JVM se ejecuta de forma independiente).
            if (!redirectOutput) {
                ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs); // Crea un constructor de procesos.
                processBuilder.start(); // Inicia la nueva JVM como un proceso separado.
            } else { // Si se debe redirigir la salida (la salida de la nueva JVM se muestra en la consola de la antigua).
                Process process = (new ProcessBuilder(jvmArgs))
                    .redirectErrorStream(true).start(); // Inicia el proceso y redirige el flujo de errores al flujo de salida normal.
                BufferedReader processOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream())); // Crea un lector para la salida del proceso.
                String line;

                // Lee línea por línea la salida del nuevo proceso y la imprime en la consola de la JVM actual.
                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }

                process.waitFor(); // Espera a que el nuevo proceso (la nueva JVM) termine su ejecución.
            }
        } catch (Exception e) {
            System.err.println("Hubo un problema al reiniciar la JVM"); // Muestra un mensaje de error si ocurre una excepción.
            e.printStackTrace(); // Imprime la traza de la pila de la excepción para depuración.
        }

        return true; // Retorna true indicando que se intentó (o se logró) reiniciar la JVM.
    }

    /**
     * Inicia una nueva JVM si la aplicación fue iniciada en macOS sin el
     * argumento {@code -XstartOnFirstThread}. Retorna si se inició una nueva JVM y
     * por lo tanto, no se debe ejecutar más código. Redirige la salida de la
     * nueva JVM a la antigua.
     * <p>
     * <u>Uso:</u>
     *
     * <pre>
     * public static void main(String... args) {
     * if (StartupHelper.startNewJvmIfRequired()) return; // Esto maneja el soporte de macOS y ayuda en Windows.
     * // el código real del método main
     * }
     * </pre>
     *
     * @return Si se inició una nueva JVM y, por lo tanto, no se debe ejecutar
     * más código en esta.
     */
    public static boolean startNewJvmIfRequired() {
        // Llama a la versión completa del método con 'redirectOutput' establecido a 'true' por defecto.
        return startNewJvmIfRequired(true);
    }
}
