package src.world.entities.player.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import src.utils.constants.PlayerControl;
import src.world.entities.player.Player;

public abstract class CanMoveState extends StatePlayer {
    public CanMoveState(Player player) {
        super(player);
    }

    @Override
    public void update(float delta) {
        Body body = player.getBody();
        Vector2 velocity = body.getLinearVelocity();

        boolean moved = false;

        if (Gdx.input.isKeyPressed(PlayerControl.RIGHT)) {
            player.moveRight();
            if (player.isFlipX()) player.setFlipX(false);
            moved = true;
        }

        if (Gdx.input.isKeyPressed(PlayerControl.LEFT)) {
            player.moveLeft();
            if (!player.isFlipX()) player.setFlipX(true);
            moved = true;
        }

        // Si no se está presionando nada, frenar suavemente
        if (!moved) {
            body.setLinearVelocity(velocity.x * 0.9f, velocity.y); // freno suave
        }
    }
}
