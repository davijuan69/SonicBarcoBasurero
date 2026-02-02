package src.screens.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import src.world.entities.player.Player;

/**
 * Muestra la vida del jugador como texto fijo en la esquina superior izquierda.
 */
public class HealthBar extends Label {
    private final Player player;
    private int lastCurrentHealth = -1;
    private int lastMaxHealth = -1;

    public HealthBar(Player player, LabelStyle style) {
        super("", style);
        this.player = player;
        setAlignment(Align.topRight);
        setFontScale(0.7f);
        setVisible(true);
        updateText();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        updateText();
        updatePosition();
    }

    private void updateText() {
        if (player == null) return;
        int current = player.getCurrentHealth();
        int max = player.getMaxHealth();
        if (current != lastCurrentHealth || max != lastMaxHealth) {
            setText("Vida: " + current + " / " + max);
            lastCurrentHealth = current;
            lastMaxHealth = max;
        }
    }

    private void updatePosition() {
        if (getStage() == null) return;
        // Position relative to the UI viewport so it stays fixed on screen
        float worldWidth = getStage().getViewport().getWorldWidth();
        float worldHeight = getStage().getViewport().getWorldHeight();
        float padding = 10f;
        float x = worldWidth - padding; // right edge
        float y = worldHeight - padding - 20f; // slightly lower than top
        // snap to integer pixels to avoid sub-pixel jitter
        setPosition(Math.round(x), Math.round(y), Align.topRight);
    }
}
