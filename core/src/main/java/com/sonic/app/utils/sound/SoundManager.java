package com.sonic.app.utils.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.sonic.app.main.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * La clase `SoundManager` gestiona la reproducción de música y efectos de sonido en el juego.
 * Implementa la interfaz `Music.OnCompletionListener` para manejar la reproducción continua de pistas de música.
 * Utiliza un hilo separado para la transición de música (fade-in/fade-out) para evitar bloquear el hilo principal del juego.
 * Permite controlar el volumen global, el volumen de la música y el volumen de los efectos de sonido de forma independiente.
 */
public class SoundManager implements Music.OnCompletionListener {
    private Float volume = 1f; // Volumen global de la aplicación (0.0f a 1.0f).
    private Float volumeMusic = 1f; // Volumen específico para la música (0.0f a 1.0f), se multiplica con el volumen global.
    private Float volumeSound = 1f; // Volumen específico para los efectos de sonido (0.0f a 1.0f), se multiplica con el volumen global.

    private final Random random; // Generador de números aleatorios para seleccionar pistas de música.
    // Mapa para organizar las pistas de música por tipo (por ejemplo, música de nivel, música de menú).
    private final HashMap<Main.SoundTrackType,ArrayList<Music>> soundTracks;
    private Main.SoundTrackType currentSoundTrack; // El tipo de pista de sonido que se está reproduciendo actualmente.

    private Music currentMusic; // La instancia de la música que se está reproduciendo en este momento.
    // Un servicio de ejecución de un solo hilo para manejar las operaciones de música (fade-in/out)
    // de forma asíncrona.
    private final ExecutorService musicThread;

    /**
     * Establece el volumen global de todo el audio.
     * @param volume El nuevo valor del volumen global (0.0f a 1.0f).
     */
    public void setVolume(Float volume) {
        this.volume = volume;
        // Si hay música reproduciéndose, ajusta su volumen inmediatamente.
        if (currentMusic != null) {
            currentMusic.setVolume(volume * volumeMusic);
        }
    }

    /**
     * Obtiene el volumen global actual.
     * @return El volumen global (0.0f a 1.0f).
     */
    public Float getVolume() {
        return volume;
    }

    /**
     * Establece el volumen específico para la música.
     * @param volumeMusic El nuevo valor del volumen de la música (0.0f a 1.0f).
     */
    public void setVolumeMusic(Float volumeMusic) {
        this.volumeMusic = volumeMusic;
        // Si hay música reproduciéndose, ajusta su volumen inmediatamente.
        if (currentMusic != null) {
            currentMusic.setVolume(volume * volumeMusic);
        }
    }

    /**
     * Obtiene el volumen de la música actual.
     * @return El volumen de la música (0.0f a 1.0f).
     */
    public Float getVolumeMusic() {
        return volumeMusic;
    }

    /**
     * Establece el volumen específico para los efectos de sonido.
     * @param volumeSound El nuevo valor del volumen de los efectos de sonido (0.0f a 1.0f).
     */
    public void setVolumeSound(Float volumeSound) {
        this.volumeSound = volumeSound;
    }

    /**
     * Obtiene el volumen de los efectos de sonido actual.
     * @return El volumen de los efectos de sonido (0.0f a 1.0f).
     */
    public Float getVolumeSound() {
        return volumeSound;
    }

    /**
     * Constructor para `SoundManager`.
     * Inicializa el generador de números aleatorios, el mapa de pistas de sonido
     * y el servicio de ejecución para el hilo de música.
     */
    public SoundManager(){
        random = new Random();
        soundTracks = new HashMap<>();
        musicThread = Executors.newSingleThreadExecutor();
    }

    /**
     * Reproduce un efecto de sonido con volumen, tono y paneo predeterminados.
     * @param sound La instancia de `Sound` a reproducir.
     */
    public void playSound(Sound sound){
        sound.play(volume * volumeSound, 1f, 0); // Aplica volumen global * volumen de sonido, tono 1.0 (normal), paneo 0 (centro).
    }

    /**
     * Reproduce un efecto de sonido con un tono especificado.
     * @param sound La instancia de `Sound` a reproducir.
     * @param pitch El tono del sonido (1.0f es normal).
     */
    public void playSound(Sound sound, Float pitch){
        sound.play(volume * volumeSound, pitch, 0); // Aplica volumen global * volumen de sonido, tono especificado, paneo 0.
    }

    /**
     * Reproduce un efecto de sonido con un tono y un volumen de sonido específicos.
     * @param sound La instancia de `Sound` a reproducir.
     * @param pitch El tono del sonido (1.0f es normal).
     * @param volumeSound El volumen específico para este sonido (se multiplica con el volumen global).
     */
    public void playSound(Sound sound, Float pitch, Float volumeSound){
        // Aplica el volumen del sonido pasado por parámetro multiplicado por el volumen global.
        sound.play(volumeSound * volume, pitch, 0);
    }

    /**
     * Inicia la reproducción de una pista de música con un efecto de fade-in.
     * Detiene la música actual con fade-out antes de iniciar la nueva.
     * @param music La instancia de `Music` a reproducir.
     */
    public void playMusic(Music music){
        music.setVolume(0); // Inicializa el volumen de la nueva música a 0 para el fade-in.
        music.setOnCompletionListener(this); // Establece este `SoundManager` como oyente para cuando la música termine.
        musicThread.submit(() -> { // Envía la tarea a un hilo para evitar bloquear el hilo principal.
            fadeOutMusic(); // Primero, asegura que la música actual se desvanece y se detiene.
            currentMusic = music; // Asigna la nueva música como la actual.
            Gdx.app.postRunnable(() -> { // Ejecuta la reproducción de la música en el hilo principal de LibGDX.
                music.play();
                System.out.println("Music started"); // Mensaje de depuración.
            });

            // Bucle para realizar el fade-in de la música.
            while (music.getVolume() < volume * volumeMusic) {
                float newVolume = music.getVolume() + 0.005f; // Incrementa el volumen gradualmente.
                if (newVolume > volume * volumeMusic) newVolume = volume * volumeMusic; // Asegura no exceder el volumen objetivo.
                music.setVolume(newVolume);
                try {
                    Thread.sleep(50); // Pausa brevemente para suavizar el fade.
                } catch (InterruptedException e) {
                    Gdx.app.log("SoundManager", "Error al iniciar la música"); // Manejo de errores.
                }
            }
        });
    }

    /**
     * Detiene la música actual con un efecto de fade-out.
     * Sincronizado para asegurar que solo una operación de parada se ejecuta a la vez.
     */
    public synchronized void stopMusic(){
        musicThread.submit(this::fadeOutMusic); // Envía la tarea de fade-out al hilo de música.
    }

    /**
     * Método privado para desvanecer el volumen de la música actual hasta 0 y luego detenerla.
     * Este método se ejecuta en el `musicThread`.
     */
    private void fadeOutMusic() {
        if (currentMusic == null) return; // Si no hay música reproduciéndose, no hace nada.
        // Bucle para realizar el fade-out de la música.
        while (currentMusic.getVolume() > 0) {
            float newVolume = currentMusic.getVolume() - 0.005f; // Decrementa el volumen gradualmente.
            if (newVolume < 0) newVolume = 0; // Asegura que el volumen no baje de 0.
            currentMusic.setVolume(newVolume);
            try {
                Thread.sleep(50); // Pausa brevemente para suavizar el fade.
            } catch (InterruptedException e) {
                Gdx.app.log("SoundManager", "Error al detener la música"); // Manejo de errores.
            }
        }
        currentMusic.stop(); // Detiene la música una vez que el volumen llega a 0.
    }

    /**
     * Añade un nuevo tipo de pista de sonido al gestor, inicializando una lista vacía para esa categoría.
     * @param type El tipo de pista de sonido (definido en `Main.SoundTrackType`).
     */
    public void addSoundTrack(Main.SoundTrackType type){
        soundTracks.put(type,new ArrayList<>());
    }

    /**
     * Establece el tipo de pista de sonido actual y reproduce una canción aleatoria de esa categoría.
     * @param type El tipo de pista de sonido a establecer.
     */
    public void setSoundTracks(Main.SoundTrackType type){
        if (type == currentSoundTrack) return; // Si ya es el tipo actual, no hace nada.
        currentSoundTrack = type; // Actualiza el tipo de pista de sonido actual.
    }

    /**
     * Añade una instancia de `Music` a una categoría de pista de sonido existente.
     * @param music La instancia de `Music` a añadir.
     * @param soundTrack La categoría de pista de sonido a la que añadir la música.
     */
    public void addMusicToSoundTrack(Music music, Main.SoundTrackType soundTrack){
        soundTracks.get(soundTrack).add(music); // Añade la música a la lista correspondiente en el mapa.
    }

    /**
     * Método privado para seleccionar y reproducir aleatoriamente una pista de música
     * del `currentSoundTrack`.
     */
    private void playSoundTrack(){
        // Selecciona un índice aleatorio dentro del rango de la lista de música del tipo actual.
        int select = random.nextInt(soundTracks.get(currentSoundTrack).size());
        // Reproduce la música seleccionada.
        playMusic(soundTracks.get(currentSoundTrack).get(select));
    }

    /**
     * Callback que se invoca cuando una pista de música ha terminado de reproducirse.
     * Cuando la música actual termina, llama a `playSoundTrack()` para reproducir la siguiente canción aleatoria.
     * @param music La instancia de `Music` que acaba de terminar.
     */
    @Override
    public void onCompletion(Music music) {
        playSoundTrack(); // Reproduce la siguiente pista aleatoria.
    }

    /**
     * Libera los recursos del `SoundManager`.
     * Detiene la música actual si está reproduciéndose y cierra el hilo de música.
     */
    public void dispose(){
        if (currentMusic != null) {
            currentMusic.stop(); // Detiene la música actual.
        }
        musicThread.shutdown(); // Cierra el servicio de ejecución del hilo.
    }
}
