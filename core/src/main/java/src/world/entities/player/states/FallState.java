package src.world.entities.player.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import src.utils.constants.PlayerControl;
import src.world.entities.player.Player;
import src.world.entities.player.PlayerCommon;

public class FallState extends CanMoveState {
    private Float fallForce;

    public FallState(Player player) {
        super(player);
        fallForce = 0f;
    }

    @Override
    public void start() {
        player.setAnimation(Player.AnimationType.FALLSIMPLE);
    }

    @Override
    public void update(Float delta) {
        super.update(delta);

        Vector2 velocity = player.getBody().getLinearVelocity();
        fallForce = Math.max(fallForce, Math.abs(velocity.y));
        if (velocity.y == 0){
            if (velocity.x == 0)  player.setCurrentState(Player.StateType.IDLE);
            else player.setCurrentState(Player.StateType.WALK);
        }
    }

    @Override
    public void end() {
        float resultFallForce = fallForce / 2;
        if (resultFallForce > 13f) {
            player.game.addCameraShake(0.1f, resultFallForce*1.5f);
            //player.playSound(Player.SoundType.HEAVYFALL);
        }
        fallForce = 0f;
    }
}
