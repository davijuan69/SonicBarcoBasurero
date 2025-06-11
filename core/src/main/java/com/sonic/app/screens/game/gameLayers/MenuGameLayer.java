package com.sonic.app.screens.game.gameLayers;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sonic.app.screens.components.OptionTable;
import com.sonic.app.screens.components.OptionTable;

/**
 * Propósito de la clase:
 * Esta clase representa una capa de menú dentro del juego. Es responsable de mostrar opciones al jugador,
 * como la posibilidad de volver al menú principal o desconectarse de una partida multijugador.
 * Actúa como una superposición que puede hacerse visible o invisible para pausar el juego
 * y permitir la interacción con el menú.
 */
public class MenuGameLayer extends GameLayer {
    // Declara una instancia de OptionTable que se usará para el menú de opciones.
    private final OptionTable optionTable;

    // Constructor de la clase MenuGameLayer.
    // Recibe un GameLayerManager para gestionar las capas del juego y un Stage donde se dibujarán los elementos.
    public MenuGameLayer(GameLayerManager gameLayerManager, Stage stage){
        // Llama al constructor de la clase padre (GameLayer).
        // Le pasa el gestor de capas, el escenario y un z-index inicial de 2.
        super(gameLayerManager, stage, 2);

        // Crea un botón con texto e imagen (ImageTextButton).
        // El texto del botón cambia según si el juego es un cliente ("Desconectarse") o no ("Volver al Menu").
        // Se usa un estilo predefinido (manager.game.myImageTextbuttonStyle).
        ImageTextButton exitButton = new ImageTextButton(manager.game.main.isClient() ? "Desconectarse": "Volver al Menu", manager.game.myImageTextbuttonStyle);

        // Agrega un ClickListener al botón de salida.
        // Cuando se hace clic en el botón, se llama al método endGame() del juego para finalizar la partida.
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                manager.game.endGame();
            }
        });

        // Agrega un listener para el evento de "hover" (cuando el ratón pasa por encima del botón).
        exitButton.addListener(manager.game.hoverListener);

        // Establece el z-index de la capa a 0. Esto probablemente coloca el fondo o elementos base de la capa de menú.
        setZindex(0);
        // Inicializa la tabla de opciones, pasándole el skin del juego, la capa actual y la fuente.
        optionTable = new OptionTable(manager.game.main.getSkin(), getLayer(), manager.game.main.fonts.briFont);
        // Agrega el botón de salida a la capa, con un ancho fijo de 400 y un padding superior de 10.
        getLayer().add(exitButton).width(400).padTop(10);

        // Establece el z-index de la capa a 1. Esto probablemente coloca el fondo de pausa por encima de otros elementos.
        setZindex(1);
        // Agrega el fondo de pausa (pauseBg) a la capa, haciendo que ocupe todo el espacio disponible.
        getLayer().add(pauseBg).grow();

        // Inicialmente, la capa de menú no es visible.
        setVisible(false);
    }

    // Sobrescribe el método setVisible de la clase padre.
    // Controla la visibilidad de la capa del menú.
    @Override
    public void setVisible(Boolean visible) {
        super.setVisible(visible);
    }

    // Sobrescribe el método update de la clase padre.
    // Se encarga de actualizar la lógica de la tabla de opciones en cada frame.
    @Override
    public void update() {
        optionTable.update();
    }
}
