package src.world.entities.enemies.basic.states;

import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;
import src.world.entities.enemies.basic.BasicEnemy;

public class IdleStateBasic extends StateEnemy<BasicEnemy> {
    private boolean flip = false;

    public IdleStateBasic(BasicEnemy enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(BasicEnemy.AnimationType.IDLE);
    }

    @Override
    public void update(Float delta) {
        if (enemy.getActCrono() > 1 && !flip) {
            enemy.setFlipX(!enemy.getSprite().isFlipX());
            flip = true;
        }
        if (enemy.getActCrono() > 2) {
            enemy.setState(Enemy.StateType.WALK);
        }
    }

    @Override
    public void end() {
        flip = false;
    }
}
