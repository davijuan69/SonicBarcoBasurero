package src.world.entities.player.states;

import src.world.entities.player.Player;
import src.utils.stateMachine.State;
import src.world.entities.player.PlayerCommon;

public abstract class StatePlayer implements State {
    protected Player player;

    public StatePlayer (Player player){
        this.player = player;
    }
}
