package src.world.entities.enemies.Eggman.states;

import src.world.entities.Entity;
import src.world.entities.enemies.Eggman.Eggman;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;
import src.world.entities.enemies.Eggman.Eggman;


public class AttackStateEggman extends StateEnemy<Eggman> {

    public AttackStateEggman(Eggman enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(Eggman.AnimationType.ATTACK);
        enemy.throwEntity(Entity.Type.TRASH, 4f,3f);
        enemy.throwEntity(Entity.Type.TRASH, -4f,3f);
        enemy.throwEntity(Entity.Type.TRASH, 4f,0f);
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
