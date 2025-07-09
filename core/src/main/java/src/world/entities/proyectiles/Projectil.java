package src.world.entities.proyectiles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import src.screens.game.GameScreen;
import src.utils.Box2dUtils;
import src.world.ActorBox2d;
import src.world.entities.Entity;
import src.world.entities.enemies.Enemy;
import src.world.entities.player.Player;
import src.world.entities.player.PlayerCommon;

public class Projectil extends Entity {
    protected GameScreen game;
    private Boolean despawn;
    private final Integer damage;

    public Projectil(World world, Rectangle shape, AssetManager assetManager, Integer id, Type type, GameScreen game, Integer damage) {
        super(world, shape, assetManager, id, type);
        this.game = game;
        despawn = false;
        this.damage = damage;
    }

    public Integer getDamage() {
        return damage;
    }

    @Override
    public synchronized void beginContactWith(ActorBox2d actor, GameScreen game) {
        if (actor instanceof Enemy enemy){
            if (enemy.getCurrentStateType() == Enemy.StateType.DAMAGE) {despawn(); return;}
            game.actDamageEnemy(enemy.getId(), body, damage, damage.floatValue());
            despawn();
        } else if (actor instanceof Player player) {
            if (player.getCurrentStateType() == PlayerCommon.StateType.STUN || player.isInvencible()) {despawn(); return;}
            player.coinDrop = damage;
            player.setCurrentState(Player.StateType.STUN);
            Box2dUtils.knockbackBody(getBody(), body, damage);
            despawn();
        }
    }

    public synchronized void despawn(){
        if (despawn) return;
        game.removeEntityNoPacket(this.getId());
        despawn = true;
    }
}
