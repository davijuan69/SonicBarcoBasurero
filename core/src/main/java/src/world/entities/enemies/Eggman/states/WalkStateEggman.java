package src.world.entities.enemies.Eggman.states;

import com.badlogic.gdx.math.Vector2;
import src.world.entities.enemies.Eggman.Eggman;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;

public class WalkStateEggman extends StateEnemy<Eggman> {

    public WalkStateEggman(Eggman enemy) {
        super(enemy);
        enemy.getBody().setGravityScale(0);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(Eggman.AnimationType.WALK);
    }

    @Override
    public void update(Float delta) {
        enemy.getBody().applyForce(0, enemy.eggmanDown ? -5f : 5f,
            enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);

        Vector2 velocity = enemy.getBody().getLinearVelocity();
        if (Math.abs(velocity.x) < enemy.speed) {
            enemy.getBody().applyForce(enemy.getSprite().isFlipX()? -3f : 3f, 0,
                enemy.getBody().getWorldCenter().x, enemy.getBody().getWorldCenter().y, true);
        }

        if (enemy.getActCrono() > 5) {
            enemy.setState(Enemy.StateType.IDLE);
        }
        if (enemy.getActCrono() > 1f){
            enemy.setState(Enemy.StateType.ATTACK);
        }
    }

    @Override
    public void end() {

    }
}
