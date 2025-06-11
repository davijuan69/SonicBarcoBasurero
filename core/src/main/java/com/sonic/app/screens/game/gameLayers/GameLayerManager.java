package com.sonic.app.screens.game.gameLayers; // Declara el paquete al que pertenece esta clase.

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sonic.app.screens.game.GameScreen;

/**
 * La clase `GameLayerManager` se encarga de **gestionar las diferentes capas de la interfaz de usuario (UI) dentro de la `GameScreen`**.
 * Actúa como un controlador central para mostrar u ocultar menús, paneles de pausa u otras superposiciones de UI,
 * asegurando que solo una capa específica esté activa y visible a la vez. También maneja los sonidos asociados
 * a la activación y desactivación de estas capas.
 */
public class GameLayerManager { // Define la clase pública GameLayerManager.
    private Sound pauseSound; // Declara una variable privada para el sonido de pausa.
    private Sound pauseExitSound; // Declara una variable privada para el sonido de salida de pausa.

    private final Stage stage; // Declara una variable final para el Stage de Scene2D, donde se dibujarán las capas de UI.
    public final GameScreen game; // Declara una variable final y pública para la instancia de GameScreen, permitiendo el acceso a recursos del juego.

    public enum LayerType { // Define una enumeración pública llamada LayerType para categorizar los tipos de capas.
        MENU // Un tipo de capa: el menú.
    }
    private Boolean visible; // Una variable booleana para controlar la visibilidad de la capa actual.
    private GameLayer currentLayer; // Una referencia a la capa de juego actualmente activa.
    private MenuGameLayer menuGameLayer; // Una instancia de la capa específica del menú de juego.

    public GameLayerManager(GameScreen game, Stage stage){ // Constructor de la clase GameLayerManager.
        this.stage = stage; // Asigna el Stage pasado al constructor a la variable de instancia.
        this.game = game; // Asigna la instancia de GameScreen pasada al constructor a la variable de instancia.
        visible = false; // Inicializa la visibilidad de las capas a falso (no visibles).
        initSounds(); // Llama al método para inicializar los sonidos.
        initLayers(); // Llama al método para inicializar las capas de UI.

        changeLayer(LayerType.MENU); // Cambia la capa actual al tipo MENU.
        setVisible(false); // Establece la visibilidad de la capa actual a falso después de cambiarla.
    }

    private void initSounds(){ // Método privado para inicializar los sonidos.
        // Carga el sonido "pauseExit.wav" desde el AssetManager del juego principal y lo asigna a pauseExitSound.
        pauseExitSound = game.main.getAssetManager().get("sound/ui/pauseExit.wav", Sound.class);
        // Carga el sonido "pause.wav" desde el AssetManager del juego principal y lo asigna a pauseSound.
        pauseSound = game.main.getAssetManager().get("sound/ui/pause.wav", Sound.class);
    }

    private void initLayers(){ // Método privado para inicializar las capas.
        // Crea una nueva instancia de MenuGameLayer, pasándole esta instancia de GameLayerManager y el Stage.
        menuGameLayer = new MenuGameLayer(this, stage);
    }

    public void changeLayer(LayerType type){ // Método público para cambiar la capa activa.
        if (currentLayer != null) currentLayer.setVisible(false); // Si hay una capa actual, la oculta.
        currentLayer = switch (type){ // Asigna la nueva capa actual basándose en el tipo proporcionado.
            case MENU -> menuGameLayer; // Si el tipo es MENU, la capa actual se convierte en menuGameLayer.
        };
        currentLayer.setVisible(visible); // Establece la visibilidad de la nueva capa según el estado de visibilidad del manager.
        currentLayer.update(); // Llama al método update() de la nueva capa para refrescar su estado.
    }

    public void setVisible(Boolean visible){ // Método público para establecer la visibilidad general de las capas.
        this.visible = visible; // Asigna el valor de visibilidad pasado a la variable de instancia.
        currentLayer.setVisible(visible); // Establece la visibilidad de la capa actual.
        if (visible) currentLayer.update(); // Si la capa se está haciendo visible, llama a su método update().
    }
    public Boolean isVisible(){ // Método público para verificar si las capas están visibles.
        return visible; // Devuelve el estado de visibilidad actual.
    }

    public void setVisibleWithSound(Boolean visible){ // Método público para establecer la visibilidad y reproducir un sonido.
        setVisible(visible); // Llama al método setVisible para cambiar la visibilidad.
        if (visible) pauseSound.play(); // Si se hace visible, reproduce el sonido de pausa.
        else pauseExitSound.play(); // Si se hace invisible, reproduce el sonido de salida de pausa.
    }

    public void setCenterPosition(Float x, Float y){ // Método público para establecer la posición central de la capa actual.
        currentLayer.setCenterPosition(x, y); // Llama al método setCenterPosition de la capa actual para moverla.
    }
}
