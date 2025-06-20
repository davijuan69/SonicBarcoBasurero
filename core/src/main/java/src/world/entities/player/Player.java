package src.world.entities.player;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;
import src.world.entities.player.states.SonicWalkState;
import src.world.entities.player.states.StatePlayer;

public class Player extends PlayerCommon {

    private SonicWalkState walkState;
    private StatePlayer currentState;

    public Player(World world, Float x, Float y, AssetManager assetManager) {
        super(world, x, y, assetManager, -1);
        initStates();
        setCurrentState(walkState); // Iniciamos en estado de caminar para permitir movimiento
    }

    private void initStates() {
        walkState = new SonicWalkState(this);
    }

    public void setCurrentState(StatePlayer state) {
        this.currentState = state;
        this.currentState.start();
    }

    @Override
    public void update(float delta) {
        super.update(delta); // Actualiza animaciones, física y demás
        if (currentState != null) {
            currentState.update(delta); // Actualiza lógica del estado (movimiento)
        }
    }
}
