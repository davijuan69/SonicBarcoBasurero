package src.world.entities.enemies.Throw;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import src.screens.game.GameScreen;
import src.utils.animation.SheetCutter;
import src.utils.constants.CollisionFilters;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.Throw.states.AttackStateThrower;
import src.world.entities.enemies.Throw.states.DamageStateThrower;
import src.world.entities.enemies.Throw.states.IdleStateThrower;
import src.world.entities.enemies.Throw.states.WalkStateThrower;

public class ThrowEnemy extends Enemy
{
    public enum AnimationType {
        IDLE,
        WALK,
        DAMAGE,
        ATTACK
    }

    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> damageAnimation;
    private final Animation<TextureRegion> attackAnimation;

    public ThrowEnemy(World world, Rectangle shape, AssetManager assetManager, Integer id, GameScreen game) {
        super(world, shape, assetManager, id, game, Type.THROWER, 15);
        sprite.setTexture(assetManager.get("world/entities/Enemigos/enemigo3.png", Texture.class));
        BodyDef def = new BodyDef();
        def.position.set(shape.x + shape.width / 2, shape.y + shape.height / 2);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        PolygonShape box = new PolygonShape();
        box.setAsBox(shape.width / 6, shape.height / 6);
        fixture = body.createFixture(box, 1);
        fixture.setUserData(this);
        box.dispose();
        body.setFixedRotation(true);

        setSpritePosModification(0f, getHeight()/3);

        Filter filter = new Filter();
        filter.categoryBits = CollisionFilters.ENEMY;
        filter.maskBits = (short)~CollisionFilters.ENEMY;
        fixture.setFilterData(filter);

        idleState = new IdleStateThrower(this);
        walkState = new WalkStateThrower(this);
        damageState = new DamageStateThrower(this);
        attackState = new AttackStateThrower(this);

        idleAnimation = new Animation<>(0.1f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo3.png"), 4));

        walkAnimation = new Animation<>(0.1f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo3.png"), 4));
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        damageAnimation = new Animation<>(0.2f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo3.png"), 4));
        damageAnimation.setPlayMode(Animation.PlayMode.LOOP);

        attackAnimation = new Animation<>(0.01f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo3.png"), 4));

        setState(StateType.IDLE);
    }

    public void setAnimation(AnimationType type) {
        switch (type) {
            case IDLE -> setCurrentAnimation(idleAnimation);
            case WALK -> setCurrentAnimation(walkAnimation);
            case DAMAGE -> setCurrentAnimation(damageAnimation);
            case ATTACK -> setCurrentAnimation(attackAnimation);
        }
    }
}
