package com.sonic.app.screens.components; // Declara el paquete donde se encuentra esta clase.

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.sonic.app.utils.constants.MyColors;
import com.sonic.app.utils.sound.SingleSoundManager;
import com.sonic.app.utils.sound.SingleSoundManager;
import com.sonic.app.utils.sound.SoundManager;

/**
 * La clase `OptionTable` es un componente de UI que crea una tabla de opciones
 * para controlar los volúmenes de sonido general, música y efectos de sonido en un juego LibGDX.
 * Utiliza Sliders para permitir al usuario ajustar estos volúmenes.
 */
public class OptionTable {
    private final Slider volumeSlider; // Declara un Slider para controlar el volumen general.
    private final Slider volumenMusicSlider; // Declara un Slider para controlar el volumen de la música.
    private final Slider volumenSoundSlider; // Declara un Slider para controlar el volumen de los efectos de sonido.

    /**
     * Constructor para la clase `OptionTable`.
     * Se encarga de construir y añadir los componentes de la interfaz de usuario
     * relacionados con las opciones de volumen a una tabla proporcionada.
     *
     * @param skin La Skin de LibGDX que define el aspecto visual de los Sliders y otros widgets.
     * @param table La Table de Scene2d a la que se añadirán los componentes de volumen.
     * @param font La BitmapFont que se utilizará para el texto de las etiquetas.
     */
    public OptionTable(Skin skin, Table table, BitmapFont font) {
        // Obtiene la instancia única del SoundManager (siguiendo el patrón Singleton).
        SoundManager soundManager = SingleSoundManager.getInstance();

        // --- Componentes para el volumen general ---
        // Crea una etiqueta de título para la sección de volumen.
        Label volumeTitleLabel = new Label("VOLUMEN",  new Label.LabelStyle(font, MyColors.BLUE));
        // Crea una etiqueta para el slider de volumen general.
        Label volumeLabel = new Label("General", new Label.LabelStyle(font, MyColors.BLUE));

        // Crea el Slider para el volumen general.
        // Rango de 0 a 1, con pasos de 0.01, no es vertical, usa el skin proporcionado.
        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        // Establece el valor inicial del slider al volumen actual del SoundManager.
        volumeSlider.setValue(soundManager.getVolume());
        // Añade un listener al slider para detectar cuando su valor cambia.
        volumeSlider.addListener(new ChangeListener() {
            @Override // Indica que este método sobrescribe un método de la clase padre.
            public void changed(ChangeEvent event, Actor actor) {
                // Cuando el slider cambia, actualiza el volumen general en el SoundManager.
                soundManager.setVolume(volumeSlider.getValue());
            }
        });

        // --- Componentes para el volumen de la música ---
        // Crea una etiqueta para el slider de volumen de música.
        Label volumeMusicLabel = new Label("Musica", new Label.LabelStyle(font, MyColors.BLUE));

        // Crea el Slider para el volumen de la música.
        volumenMusicSlider = new Slider(0, 1, 0.01f, false, skin);
        // Establece el valor inicial del slider al volumen actual de la música del SoundManager.
        volumenMusicSlider.setValue(soundManager.getVolumeMusic());
        // Añade un listener al slider para detectar cuando su valor cambia.
        volumenMusicSlider.addListener(new ChangeListener() {
            @Override // Indica que este método sobrescribe un método de la clase padre.
            public void changed(ChangeEvent event, Actor actor) {
                // Cuando el slider cambia, actualiza el volumen de la música en el SoundManager.
                soundManager.setVolumeMusic(volumenMusicSlider.getValue());
            }
        });

        // --- Componentes para el volumen de los efectos de sonido ---
        // Crea una etiqueta para el slider de volumen de los efectos de sonido.
        Label volumeSoundLabel = new Label("Sonidos", new Label.LabelStyle(font, MyColors.BLUE));

        // Crea el Slider para el volumen de los efectos de sonido.
        volumenSoundSlider = new Slider(0, 1, 0.01f, false, skin);
        // Establece el valor inicial del slider al volumen actual de los efectos de sonido del SoundManager.
        volumenSoundSlider.setValue(soundManager.getVolumeSound());
        // Añade un listener al slider para detectar cuando su valor cambia.
        volumenSoundSlider.addListener(new ChangeListener() {
            @Override // Indica que este método sobrescribe un método de la clase padre.
            public void changed(ChangeEvent event, Actor actor) {
                // Cuando el slider cambia, actualiza el volumen de los efectos de sonido en el SoundManager.
                soundManager.setVolumeSound(volumenSoundSlider.getValue());
            }
        });

        // --- Añadir los componentes a la tabla ---
        // Ajusta la escala de la fuente del título del volumen.
        volumeTitleLabel.setFontScale(1.2f);
        // Añade el título a la tabla, expandiéndolo horizontalmente y añadiendo un padding.
        table.add(volumeTitleLabel).expandX().pad(10);
        table.row(); // Pasa a la siguiente fila de la tabla.

        // Añade la etiqueta y el slider de volumen general a la tabla.
        table.add(volumeLabel).expandX().pad(10);
        table.row();
        table.add(volumeSlider).expandX().pad(10);
        table.row();

        // Añade la etiqueta y el slider de volumen de música a la tabla.
        table.add(volumeMusicLabel).expandX().pad(10);
        table.row();
        table.add(volumenMusicSlider).expandX().pad(10);
        table.row();

        // Añade la etiqueta y el slider de volumen de los efectos de sonido a la tabla.
        table.add(volumeSoundLabel).expandX().pad(10);
        table.row();
        table.add(volumenSoundSlider).expandX().pad(10);
        table.row();
    }

    /**
     * Actualiza los valores de los sliders para reflejar el estado actual
     * de los volúmenes en el `SoundManager`.
     * Esto es útil si los volúmenes pueden ser cambiados por otras partes del juego
     * y la interfaz de usuario necesita sincronizarse.
     */
    public void update(){
        SoundManager soundManager = SingleSoundManager.getInstance(); // Obtiene la instancia actual del SoundManager.
        volumeSlider.setValue(soundManager.getVolume()); // Actualiza el slider de volumen general.
        volumenMusicSlider.setValue(soundManager.getVolumeMusic()); // Actualiza el slider de volumen de música.
        volumenSoundSlider.setValue(soundManager.getVolumeSound()); // Actualiza el slider de volumen de efectos de sonido.
    }
}
