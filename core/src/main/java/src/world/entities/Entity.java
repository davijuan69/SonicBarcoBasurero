package src.world.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import src.world.ActorBox2dSprite;

public class Entity extends ActorBox2dSprite{

    public enum Type {
        BASIC, //enemigo basico
        THROWER,
        TRASH,
        MOUNT,
        RING, // moneda del juego
        EGGMAN,
        CHECKPOINT,

    }
    protected Type type;  // Tipo de la entidad (de la enumeración anterior)
    private final Integer id; // Identificador único de la entidad

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

