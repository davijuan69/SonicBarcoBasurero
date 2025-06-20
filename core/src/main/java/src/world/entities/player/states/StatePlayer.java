package src.world.entities.player.states;

import src.world.entities.player.Player;
import src.utils.StateMachine.State;

public abstract class StatePlayer implements State {
    protected Player player;

    public StatePlayer (Player player){
        this.player = player;
    }

    public abstract void update(float delta);
}
