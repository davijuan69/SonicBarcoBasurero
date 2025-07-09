package src.world.entities; // Declara el paquete para esta clase, organizándola dentro de la estructura de entidades del proyecto.

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sun.jdi.Mirror;
import src.screens.game.GameScreen;
import src.world.entities.enemies.Throw.ThrowEnemy;
import src.world.entities.enemies.basic.BasicEnemy;
import src.world.entities.proyectiles.TrashProyectil;

// Define una clase de fábrica responsable de crear diferentes tipos de entidades de juego.
public class EntityFactory {
    private final GameScreen game; // Una referencia a GameScreen, que proporciona acceso a los recursos y la lógica específicos del juego.

    /**
     * Constructor para EntityFactory.
     * @param game La instancia de GameScreen, utilizada para acceder a recursos compartidos como el AssetManager.
     */
    public EntityFactory(GameScreen game){
        this.game = game; // Inicializa la referencia a la pantalla del juego.
    }

    /**
     * Crea y devuelve una nueva Entidad basada en el tipo especificado.
     * Este método actúa como una fábrica, encapsulando la lógica de creación para varias entidades.
     *
     * @param type El tipo de entidad a crear, definido por el enum Entity.Type.
     * @param world El mundo de física Box2D donde existirá la entidad.
     * @param position La posición inicial 2D (x, y) para la entidad.
     * @param id Un identificador entero único para la nueva entidad.
     * @return Una instancia de la Entidad creada, o null si el tipo no es reconocido.
     */
    public Entity create(Entity.Type type, World world, Vector2 position, Integer id){
        // Recupera el AssetManager de la instancia principal del juego para cargar texturas y otros recursos para las entidades.
        AssetManager assetManager = game.main.getAssetManager();
        // Utiliza una expresión switch para crear diferentes instancias de entidades basándose en el Entity.Type proporcionado.
        return switch (type) {
            // Crea un BasicEnemy con un tamaño de 1.5x1.5 unidades.
            case BASIC -> new BasicEnemy(world, new Rectangle(position.x, position.y, 1.5f, 1.5f), assetManager, id, game);
            // Si el tipo de entidad proporcionado no coincide con ningún caso conocido, devuelve null.

            case THROWER -> new ThrowEnemy(world,new Rectangle(position.x, position.y, 1.5f, 1.5f), assetManager, id, game);

            case TRASH -> new TrashProyectil(world, new Rectangle(position.x, position.y, 1f, 1f), assetManager, id, game);

            default -> null;
        };
    }
}
