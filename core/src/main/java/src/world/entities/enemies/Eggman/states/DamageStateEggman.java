package src.world.entities.enemies.Eggman.states;
import src.world.ActorBox2dSprite;
import src.world.ActorBox2d;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import src.utils.animation.SheetCutter;
import src.world.entities.Entity;
import src.world.entities.enemies.Eggman.Eggman;
import src.world.entities.enemies.Enemy;
import src.world.entities.enemies.StateEnemy;


public class DamageStateEggman extends StateEnemy<Eggman> {

    public DamageStateEggman(Eggman enemy) {
        super(enemy);

    }

    @Override
    public void start() {
        super.start();
        enemy.setAnimation(Eggman.AnimationType.DAMAGE);

    }

    @Override
    public void update(Float delta) {
        if (enemy.isAnimationFinish()) {
            enemy.setState(Eggman.StateType.WALK);
            if (enemy.isDead()){
                enemy.throwEntity(Entity.Type.RING,0f,3f);
                enemy.game.removeEntity(enemy.getId());
            }
        }
    }

    public void setAnimation(Eggman.AnimationType type, Eggman enemy) {
        enemy.setAnimation(type);
    }

    @Override
    public void end() {

    }
}
