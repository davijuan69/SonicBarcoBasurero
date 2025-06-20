package src.world.entities.player.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import src.utils.constants.PlayerControl;
import src.world.entities.player.Player;

public class SonicWalkState extends CanMoveState {
    public SonicWalkState(Player player) {
        super(player);
    }

    @Override
    public void start() {
        // No hace falta inicializar nada más por ahora
    }

    @Override
    public void update(Float delta) {
        Body body = player.getBody();
        Vector2 velocity = body.getLinearVelocity();
        boolean moved = false;

        if (Gdx.input.isKeyPressed(PlayerControl.RIGHT)) {
            player.moveRight();
            player.setFlipX(false); // mirando a la derecha
            moved = true;
        }

        if (Gdx.input.isKeyPressed(PlayerControl.LEFT)) {
            player.moveLeft();
            player.setFlipX(true); // mirando a la izquierda
            moved = true;
        }

        // Si no se está moviendo, frenar suavemente
        if (!moved) {
            body.setLinearVelocity(velocity.x * 0.9f, velocity.y);
        }
    }

    @Override
    public void end() {
        // No se necesita lógica especial al salir de este estado aún
    }
}
