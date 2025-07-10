package src.world.entities.items;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import src.screens.game.GameScreen;
import src.utils.animation.SheetCutter;
import src.utils.constants.CollisionFilters;

public class Mount extends Item {
    public Mount(World world, Rectangle shape, AssetManager assetManager, Integer id, GameScreen game) {
        super(world, shape, assetManager, id, Type.MOUNT, game);

        BodyDef def = new BodyDef();
        def.position.set(shape.x + shape.width / 2, shape.y + shape.height / 2);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        PolygonShape box = new PolygonShape();
        box.setAsBox(shape.width/4, shape.height/2);
        fixture = body.createFixture(box, 0.5f);
        fixture.setUserData(this);
        box.dispose();
        body.setFixedRotation(true);

        Filter filter = new Filter();
        filter.categoryBits = CollisionFilters.ITEM;
        fixture.setFilterData(filter);

        Animation<TextureRegion> loopAnimation = new Animation<>(0.1f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Objetos/monton.png"), 1));
        loopAnimation.setPlayMode(Animation.PlayMode.LOOP);

        setCurrentAnimation(loopAnimation);
    }
    public void throwEntity(Type type, Float impulseX, Float impulseY){
        float linearX = Math.abs(body.getLinearVelocity().x); // Obtiene el valor absoluto de la velocidad lineal X del cuerpo.
        game.addEntityNoPacket(type, // Añade una nueva entidad al juego.
            body.getPosition().add(isFlipX() ? -2.2f : 1.2f,-0.5f), // Calcula la posición de lanzamiento ajustada a la dirección del enemigo.
            new Vector2((isFlipX() ? -impulseX - linearX : impulseX + linearX),impulseY), // Calcula el vector de impulso, ajustando por la dirección y velocidad del enemigo.
            isFlipX() // Pasa la dirección de giro (flip) al crear la entidad.
        );
    }
}
