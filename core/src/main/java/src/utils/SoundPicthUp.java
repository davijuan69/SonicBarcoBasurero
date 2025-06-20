package src.utils; // Declara el paquete donde se encuentra esta clase.

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Queue;

// Declara la clase SoundPicthUp, que implementa la interfaz Sound.
// Esta clase parece estar diseñada para reproducir un sonido con un tono (pitch) ascendente gradual
// y gestionar su reproducción en una cola, con un mecanismo para reiniciar el tono.
public class SoundPicthUp implements Sound {
    private final Float playInterval; // Declara una constante para el intervalo de tiempo entre reproducciones de sonido.

    private final Sound sound; // Declara una referencia al objeto Sound original que se va a manipular.
    private Float pitch = 1.0f; // Declara la variable 'pitch' (tono), inicializada a 1.0 (tono normal).
    // Este valor se incrementará con cada reproducción.
    private final Queue<Sound> soundQueue = new Queue<>(); // Declara una cola para almacenar solicitudes de reproducción de sonido.
    // Cada vez que se llama a 'play()', el sonido se añade aquí.
    private Float resetTime; // Declara una variable para controlar el tiempo desde la última vez que se reprodujo un sonido,
    // utilizada para determinar cuándo reiniciar el tono.
    private Float timer = 0.0f; // Declara un temporizador para controlar cuándo es el momento de reproducir el siguiente sonido de la cola.

    /**
     * Constructor para la clase SoundPicthUp.
     *
     * @param sound El objeto Sound original que se va a reproducir y cuyo tono se va a manipular.
     * @param playInterval El intervalo de tiempo en segundos entre la reproducción de sonidos consecutivos.
     * @param resetTime El tiempo en segundos de inactividad después del cual el tono se reinicia a su valor original (1.0f).
     */
    public SoundPicthUp(Sound sound, Float playInterval, Float resetTime) {
        this.sound = sound; // Asigna el objeto Sound proporcionado.
        this.playInterval = playInterval; // Asigna el intervalo de reproducción.
        this.resetTime = resetTime; // Asigna el tiempo de reinicio del tono.
    }

    /**
     * Este método debe llamarse repetidamente (ej. en el bucle de renderizado del juego) para actualizar el temporizador
     * y gestionar la reproducción de sonidos y el reinicio del tono.
     *
     * @param delta El tiempo transcurrido desde la última llamada a update (generalmente en segundos).
     */
    public void update(float delta) {
        timer += delta; // Incrementa el temporizador de reproducción.
        resetTime += delta; // Incrementa el temporizador de reinicio del tono.

        // Si el temporizador de reproducción ha alcanzado o superado el intervalo y hay sonidos en la cola,
        // reproduce el siguiente sonido.
        if (timer >= playInterval && soundQueue.size > 0) {
            Sound nextSound = soundQueue.removeFirst(); // Obtiene y elimina el primer sonido de la cola.
            nextSound.play(1f, pitch, 0.0f); // Reproduce el sonido con volumen 1f, el tono actual y pan 0.0f.
            pitch += 0.1f; // Incrementa el tono para la próxima reproducción.
            resetTime = 0.0f; // Reinicia el temporizador de reinicio del tono porque se ha reproducido un sonido.
            timer = 0.0f; // Reinicia el temporizador de reproducción.
        }
        // Si el temporizador de reinicio ha alcanzado o superado el tiempo límite (2.0 segundos),
        // reinicia el tono a su valor original.
        if (resetTime >= 2.0f) { // El valor 2.0f parece ser un umbral fijo para reiniciar el pitch.
            pitch = 1.0f; // Reinicia el tono a 1.0f (normal).
        }
    }

    // A partir de aquí, los siguientes métodos son implementaciones de la interfaz Sound.
    // La mayoría de ellos simplemente añaden el sonido a una cola para su reproducción posterior
    // o delegan la llamada al objeto Sound original.

    /**
     * Añade el sonido a la cola para ser reproducido más tarde por el método update.
     * Este método solo devuelve un ID de sonido ficticio (1) ya que la reproducción real se gestiona internamente.
     * @return Siempre 1 (un ID de sonido ficticio).
     */
    @Override
    public long play() {
        soundQueue.addLast(sound); // Añade el sonido al final de la cola.
        return 1; // Devuelve un ID de sonido (ficticio en este contexto).
    }

    /**
     * Añade el sonido a la cola para ser reproducido más tarde, ignorando el volumen en este punto.
     * @param volume El volumen deseado (no utilizado directamente aquí, se usa 1f en la reproducción real).
     * @return Siempre 1 (un ID de sonido ficticio).
     */
    @Override
    public long play(float volume) {
        soundQueue.addLast(sound); // Añade el sonido al final de la cola.
        return 1; // Devuelve un ID de sonido (ficticio en este contexto).
    }

    /**
     * Añade el sonido a la cola para ser reproducido más tarde, ignorando los parámetros de tono y paneo.
     * @param volume El volumen deseado (no utilizado directamente aquí).
     * @param pitch El tono deseado (no utilizado directamente aquí).
     * @param pan El paneo deseado (no utilizado directamente aquí).
     * @return Siempre 1 (un ID de sonido ficticio).
     */
    @Override
    public long play(float volume, float pitch, float pan) {
        soundQueue.addLast(sound); // Añade el sonido al final de la cola.
        return 1; // Devuelve un ID de sonido (ficticio en este contexto).
    }

    /**
     * Delega la llamada al método loop() del objeto Sound original.
     * @return El ID de sonido de la reproducción en bucle.
     */
    @Override
    public long loop() {
        return sound.loop();
    }

    /**
     * Delega la llamada al método loop(volume) del objeto Sound original.
     * @param volume El volumen para el bucle.
     * @return El ID de sonido de la reproducción en bucle.
     */
    @Override
    public long loop(float volume) {
        return sound.loop(volume);
    }

    /**
     * Delega la llamada al método loop(volume, pitch, pan) del objeto Sound original.
     * @param volume El volumen para el bucle.
     * @param pitch El tono para el bucle.
     * @param pan El paneo para el bucle.
     * @return El ID de sonido de la reproducción en bucle.
     */
    @Override
    public long loop(float volume, float pitch, float pan) {
        return sound.loop(volume, pitch, pan);
    }

    /**
     * Delega la llamada al método stop() del objeto Sound original.
     */
    @Override
    public void stop() {
        sound.stop();
    }

    /**
     * Delega la llamada al método pause() del objeto Sound original.
     */
    @Override
    public void pause() {
        sound.pause();
    }

    /**
     * Delega la llamada al método resume() del objeto Sound original.
     */
    @Override
    public void resume() {
        sound.resume();
    }

    /**
     * Delega la llamada al método dispose() del objeto Sound original.
     * Esto libera los recursos asociados al sonido.
     */
    @Override
    public void dispose() {
        sound.dispose();
    }

    /**
     * Delega la llamada al método stop(soundId) del objeto Sound original.
     * @param soundId El ID del sonido a detener.
     */
    @Override
    public void stop(long soundId) {
        sound.stop(soundId);
    }

    /**
     * Delega la llamada al método pause(soundId) del objeto Sound original.
     * @param soundId El ID del sonido a pausar.
     */
    @Override
    public void pause(long soundId) {
        sound.pause(soundId);
    }

    /**
     * Delega la llamada al método resume(soundId) del objeto Sound original.
     * @param soundId El ID del sonido a reanudar.
     */
    @Override
    public void resume(long soundId) {
        sound.resume(soundId);
    }

    /**
     * Delega la llamada al método setLooping(soundId, looping) del objeto Sound original.
     * @param soundId El ID del sonido.
     * @param looping Si el sonido debe reproducirse en bucle.
     */
    @Override
    public void setLooping(long soundId, boolean looping) {
        sound.setLooping(soundId, looping);
    }

    /**
     * Delega la llamada al método setPitch(soundId, pitch) del objeto Sound original.
     * @param soundId El ID del sonido.
     * @param pitch El nuevo tono.
     */
    @Override
    public void setPitch(long soundId, float pitch) {
        sound.setPitch(soundId, pitch);
    }

    /**
     * Delega la llamada al método setVolume(soundId, volume) del objeto Sound original.
     * @param soundId El ID del sonido.
     * @param volume El nuevo volumen.
     */
    @Override
    public void setVolume(long soundId, float volume) {
        sound.setVolume(soundId, volume);
    }

    /**
     * Delega la llamada al método setPan(soundId, pan, volume) del objeto Sound original.
     * @param soundId El ID del sonido.
     * @param pan El nuevo paneo.
     * @param volume El volumen.
     */
    @Override
    public void setPan(long soundId, float pan, float volume) {
        sound.setPan(soundId, pan, volume);
    }
}
