package src.screens.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align; // <-- ¡Importante! Asegúrate de tener esta importación
import src.utils.constants.MyColors;
import src.utils.sound.SingleSoundManager;
import src.utils.sound.SoundManager;

/**
 * La clase `OptionTable` es un componente de UI que crea una tabla de opciones
 * para controlar los volúmenes de sonido general, música y efectos de sonido en un juego LibGDX.
 * Utiliza Sliders para permitir al usuario ajustar estos volúmenes.
 */
public class OptionTable {
    private final Slider volumeSlider;
    private final Slider volumenMusicSlider;
    private final Slider volumenSoundSlider;

    // Referencia a la tabla donde se añadirán los componentes.
    // Aunque ya la pasas en el constructor, es bueno tener una referencia aquí
    // si OptionTable no extiende Table.
    private final Table parentTable;

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
        this.parentTable = table; // Guardamos la referencia a la tabla
        SoundManager soundManager = SingleSoundManager.getInstance();

        // --- Componentes para el volumen general ---
        Label volumeLabel = new Label("General", new Label.LabelStyle(font, MyColors.BLUE));
        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeSlider.setValue(soundManager.getVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setVolume(volumeSlider.getValue());
            }
        });

        // --- Componentes para el volumen de la música ---
        Label volumeMusicLabel = new Label("Musica", new Label.LabelStyle(font, MyColors.YELLOW));
        volumenMusicSlider = new Slider(0, 1, 0.01f, false, skin);
        volumenMusicSlider.setValue(soundManager.getVolumeMusic());
        volumenMusicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setVolumeMusic(volumenMusicSlider.getValue());
            }
        });

        // --- Componentes para el volumen de los efectos de sonido ---
        Label volumeSoundLabel = new Label("Sonidos", new Label.LabelStyle(font, MyColors.RED));
        volumenSoundSlider = new Slider(0, 1, 0.01f, false, skin);
        volumenSoundSlider.setValue(soundManager.getVolumeSound());
        volumenSoundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundManager.setVolumeSound(volumenSoundSlider.getValue());
            }
        });

        // --- Añadir los componentes a la tabla con NUEVAS POSICIONES Y ESTILOS ---

        // Ajusta la escala de la fuente del título del volumen.
        // Título "VOLUMEN": Centrado, con padding superior e inferior
        parentTable.add().expandX().padTop(50).padBottom(30).center();
        parentTable.row(); // Siguiente elemento en una nueva fila

        // Etiqueta "General" y Slider: Alineados a la izquierda, con un ancho fijo para el slider
        parentTable.add(volumeLabel).align(Align.left).padLeft(50).padBottom(5); // Alineado a la izquierda, con padding a la izquierda y abajo
        parentTable.row();
        parentTable.add(volumeSlider).width(400).padBottom(40).center(); // Ancho fijo, centrado, más padding abajo
        parentTable.row();

        // Etiqueta "Musica" y Slider: Similar al anterior, con ajustes de padding
        parentTable.add(volumeMusicLabel).align(Align.left).padLeft(50).padBottom(5);
        parentTable.row();
        parentTable.add(volumenMusicSlider).width(400).padBottom(40).center();
        parentTable.row();

        // Etiqueta "Sonidos" y Slider: Similar al anterior, con ajustes de padding
        parentTable.add(volumeSoundLabel).align(Align.left).padLeft(50).padBottom(5);
        parentTable.row();
        parentTable.add(volumenSoundSlider).width(400).padBottom(40).center(); // Último slider con más padding inferior
        parentTable.row(); // Siempre termina con un row() si esperas más contenido debajo o simplemente para cerrar la tabla.
    }

    /**
     * Actualiza los valores de los sliders para reflejar el estado actual
     * de los volúmenes en el `SoundManager`.
     * Esto es útil si los volúmenes pueden ser cambiados por otras partes del juego
     * y la interfaz de usuario necesita sincronizarse.
     */
    public void update(){
        SoundManager soundManager = SingleSoundManager.getInstance();
        volumeSlider.setValue(soundManager.getVolume());
        volumenMusicSlider.setValue(soundManager.getVolumeMusic());
        volumenSoundSlider.setValue(soundManager.getVolumeSound());
    }
}

