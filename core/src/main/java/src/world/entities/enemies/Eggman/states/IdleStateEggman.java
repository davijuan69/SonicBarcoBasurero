package src.world.entities.enemies.Eggman.states;

import src.world.entities.enemies.Eggman.Eggman;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;

public class IdleStateEggman extends StateEnemy<Eggman> {

    private boolean flip = false;

    public IdleStateEggman(Eggman enemy) {
        super(enemy);
    }

    @Override
    public void start() {
        super.start();
        enemy.getBody().setLinearVelocity(0,0);
        enemy.eggmanDown = !enemy.eggmanDown;
        enemy.getBody().setGravityScale(0);
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
        flip = true;
    }
}
