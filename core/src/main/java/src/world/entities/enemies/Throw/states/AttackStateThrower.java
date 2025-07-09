package src.world.entities.enemies.Throw.states;

import src.world.entities.Entity;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;
import src.world.entities.enemies.Throw.ThrowEnemy;

public class AttackStateThrower extends StateEnemy<ThrowEnemy> {

    public AttackStateThrower(ThrowEnemy enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(ThrowEnemy.AnimationType.ATTACK);
        enemy.throwEntity(Entity.Type.TRASH, 4f,4f);
    }

    @Override
    public void update(Float delta) {
        if (enemy.getActCrono() > 1) {
            enemy.setState(Enemy.StateType.IDLE);
        }
    }

    @Override
    public void end() {

    }

}
