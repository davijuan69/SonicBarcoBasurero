package src.world.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import src.world.ActorBox2dSprite;

public class Entity extends ActorBox2dSprite{

    public enum Type {
        RING,
        ENEMY_MOTOBUG,
        SPRING,
        PLATFORM,
        CHECKPOINT,
        SPIKE,
        MONITOR_RING,
        MONITOR_SPEED,
        MONITOR_INVINCIBILITY,
        GOALPOST,
    }

    protected Type type;
    private final Integer id;

    public Entity(World world, Rectangle shape, AssetManager assetManager, Integer id, Type type){
        super(world, shape, assetManager);
        this.id = id;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }
}

