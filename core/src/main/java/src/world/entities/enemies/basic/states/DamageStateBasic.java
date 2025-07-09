package src.world.entities.enemies.basic.states;

import src.world.entities.enemies.StateEnemy;
import src.world.entities.enemies.basic.BasicEnemy;

public class DamageStateBasic extends StateEnemy<BasicEnemy> {

    public DamageStateBasic(BasicEnemy enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(BasicEnemy.AnimationType.DAMAGE);
    }

    @Override
    public void update(Float delta) {
        if (enemy.isAnimationFinish()) {
            enemy.setState(BasicEnemy.StateType.IDLE);
            if (enemy.isDead()) enemy.game.removeEntity(enemy.getId());
        }
    }

    @Override
    public void end() {

    }
}
