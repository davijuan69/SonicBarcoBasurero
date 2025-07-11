package src.world.entities.enemies.Eggman;

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
import src.world.ActorBox2d;
import src.world.entities.enemies.Eggman.states.AttackStateEggman;
import src.world.entities.enemies.Eggman.states.DamageStateEggman;
import src.world.entities.enemies.Eggman.states.IdleStateEggman;
import src.world.entities.enemies.Eggman.states.WalkStateEggman;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.Throw.states.AttackStateThrower;
import src.world.entities.proyectiles.Projectil;

public class Eggman extends Enemy
{
    public Boolean eggmanDown;
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


    public Eggman(World world, Rectangle shape, AssetManager assetManager, Integer id, GameScreen game) {
        super(world, shape, assetManager,id, game, Type.EGGMAN,10);
        eggmanDown = false;

        BodyDef def = new BodyDef();
        def.position.set(shape.x + shape.width / 2, shape.y + shape.height / 4);
        def.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(def);
        body.setGravityScale(0);

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

        idleState = new IdleStateEggman(this);
        walkState = new WalkStateEggman(this);
        damageState = new DamageStateEggman(this);
        attackState = new AttackStateEggman(this);


        setState(StateType.WALK);

        idleAnimation = new Animation<>(0.12f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/eggman/eggman_idle.png"), 3));

        walkAnimation = new Animation<>(0.12f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/eggman/eggman_idle.png"), 3));
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        damageAnimation = new Animation<>(0.2f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/eggman/eggman_molesto.png"), 2));
        damageAnimation.setPlayMode(Animation.PlayMode.LOOP);

        attackAnimation = new Animation<>(0.01f,
            SheetCutter.cutHorizontal(assetManager.get("world/entities/Enemigos/eggman/eggman_idle.png"), 3));



        setCurrentAnimation(walkAnimation);
    }

    public void setAnimation(AnimationType type) {
        switch (type) {
            case IDLE -> setCurrentAnimation(idleAnimation);
            case WALK -> setCurrentAnimation(walkAnimation);
            case DAMAGE -> setCurrentAnimation(damageAnimation);
            case ATTACK -> setCurrentAnimation(attackAnimation);
        }

    }

    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void beginContactWith(ActorBox2d actor, GameScreen game) {
        super.beginContactWith(actor, game);
        if (actor instanceof Projectil) return;
        if (getCurrentStateType() == StateType.DAMAGE) return;
        setState(StateType.IDLE);
    }
}
