package src.world.entities.enemies.Throw.states;

import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;
import src.world.entities.enemies.Throw.ThrowEnemy;

public class IdleStateThrower extends StateEnemy<ThrowEnemy> {
    private boolean flip = false;

    public IdleStateThrower(ThrowEnemy enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(ThrowEnemy.AnimationType.IDLE);
    }

    @Override
    public void update(Float delta) {
        if (enemy.getActCrono() > 1 && !flip) {
            enemy.setFlipX(!enemy.getSprite().isFlipX());
            flip = true;
        }
        if (enemy.getActCrono() > 1.5f) {
            enemy.setState(Enemy.StateType.WALK);
        }
    }

    @Override
    public void end() {
        flip = false;
    }
}
