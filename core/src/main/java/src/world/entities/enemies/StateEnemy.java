package src.world.entities.enemies;

import src.utils.stateMachine.State;

public abstract class StateEnemy<E extends Enemy> implements State  {
    protected E enemy;
    public StateEnemy(E enemy) {
        this.enemy = enemy;
    }

    @Override
    public void start() {
        enemy.setActCrono(0f);
    }
}
