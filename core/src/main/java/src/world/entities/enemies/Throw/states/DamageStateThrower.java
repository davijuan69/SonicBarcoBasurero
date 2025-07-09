package src.world.entities.enemies.Throw.states;

import src.world.entities.enemies.StateEnemy;
import src.world.entities.enemies.Throw.ThrowEnemy;

public class DamageStateThrower extends StateEnemy<ThrowEnemy> {

    public DamageStateThrower(ThrowEnemy enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(ThrowEnemy.AnimationType.DAMAGE);
    }

    @Override
    public void update(Float delta) {
        if (enemy.isAnimationFinish()) {
            enemy.setState(ThrowEnemy.StateType.IDLE);
            if (enemy.isDead()) enemy.game.removeEntity(enemy.getId());
        }
    }

    @Override
    public void end() {

    }
}
