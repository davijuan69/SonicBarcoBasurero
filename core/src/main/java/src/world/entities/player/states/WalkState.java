package src.world.entities.player.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import src.utils.constants.PlayerControl;
import src.world.entities.player.Player;

public class WalkState extends CanBasicMoveState{
    public WalkState(Player player){
        super(player);
    }

    @Override
    public void start() {
        player.setAnimation(Player.AnimationType.WALK);

        player.speed = Player.WALK_SPEED;
        player.maxSpeed =  Player.WALK_MAX_SPEED;
    }

    @Override
    public void update(Float delta) {
        super.update(delta);
        if (player.getCurrentStateType() != Player.StateType.WALK) return;
        Vector2 velocity = player.getBody().getLinearVelocity();
        // Shift para correr; si no, caminar y luego trotar
        if (PlayerControl.isRunPressed()) {
            player.setCurrentState(Player.StateType.RUN);
            return;
        }

        float absVx = Math.abs(velocity.x);
        if (absVx > Player.TROT_THRESHOLD) {
            player.speed = Player.TROT_SPEED;
            player.maxSpeed = Player.TROT_MAX_SPEED;
            if (player.getCurrentAnimationType() != Player.AnimationType.RUN) {
                player.setAnimation(Player.AnimationType.RUN);
            }
        } else {
            player.speed = Player.WALK_SPEED;
            player.maxSpeed = Player.WALK_MAX_SPEED;
            if (player.getCurrentAnimationType() != Player.AnimationType.WALK) {
                player.setAnimation(Player.AnimationType.WALK);
            }
        }
        if (velocity.x == 0 || (!Gdx.input.isKeyPressed(PlayerControl.LEFT) && !Gdx.input.isKeyPressed(PlayerControl.RIGHT))){
            player.setCurrentState(Player.StateType.IDLE);
        }
    }

    @Override
    public void end() {

    }
}
