package src.world.entities.enemies.basic;

import com.badlogic.gdx.assets.AssetManager;
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
import src.world.entities.enemies.basic.states.DamageStateBasic;
import src.world.entities.enemies.basic.states.IdleStateBasic;
import src.world.entities.enemies.basic.states.WalkStateBasic;

public class BasicEnemy extends Enemy {
    public enum AnimationType {
        IDLE,
        WALK,
        DAMAGE,
    }
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> damageAnimation;

    public BasicEnemy(World world, Rectangle shape, AssetManager assetManager, Integer id, GameScreen game) {
        super(world, shape, assetManager,id, game, Type.BASIC,9);

        BodyDef def = new BodyDef();
        def.position.set(shape.x + shape.width / 2, shape.y + shape.height / 2);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);

        PolygonShape box = new PolygonShape();
        box.setAsBox(shape.width/4, shape.height/4);
        fixture = body.createFixture(box, 1);
        fixture.setUserData(this);
        box.dispose();
        body.setFixedRotation(true);

        setSpritePosModification(0f, getHeight()/4);

        Filter filter = new Filter();
        filter.categoryBits = CollisionFilters.ENEMY;
        filter.maskBits = (short)~CollisionFilters.ENEMY;
        fixture.setFilterData(filter);

        idleState = new IdleStateBasic(this);
        walkState = new WalkStateBasic(this);
        damageState = new DamageStateBasic(this);
        setState(StateType.IDLE);

        idleAnimation = new Animation<>(0.12f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo1.png"), 3));

        walkAnimation = new Animation<>(0.12f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo1.png"), 3));
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        damageAnimation = new Animation<>(0.1f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/enemigo1.png"), 3));

        setCurrentAnimation(idleAnimation);
    }

    public void setAnimation(AnimationType type){
        switch (type){
            case IDLE -> setCurrentAnimation(idleAnimation);
            case WALK -> setCurrentAnimation(walkAnimation);
            case DAMAGE -> setCurrentAnimation(damageAnimation);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
