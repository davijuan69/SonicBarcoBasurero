package src.world.entities.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import src.screens.game.GameScreen;
import src.world.ActorBox2d;
import src.world.entities.Entity;

public class Item extends Entity {
    protected GameScreen game;

    public Item(World world, Rectangle shape, AssetManager assetManager, Integer id, Type type, GameScreen game) {
        super(world, shape, assetManager, id, type);
        this.game = game;
    }

    @Override
    public void beginContactWith(ActorBox2d actor, GameScreen game) {

    }

    public synchronized void despawn(){
        game.removeEntity(getId());
    }
}
