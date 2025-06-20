package src.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import src.screens.game.GameScreen;

import static src.utils.constants.Constants.PIXELS_IN_METER;

/**
 * Clase abstracta que extiende `Actor` de Scene2D y sirve como base para
 * todos los objetos del juego que tendrán un cuerpo físico (`Body`) en el
 * mundo Box2D.
 * Gestiona la relación entre la representación visual (Actor) y la física (Box2D).
 */
public abstract class ActorBox2d extends Actor {
    protected final World world; // Referencia al mundo físico de Box2D al que pertenece el cuerpo.
    protected Body body;         // El cuerpo físico de Box2D asociado a este Actor.
    protected Fixture fixture;   // La primera o principal forma de colisión (Fixture) del cuerpo.

    /**
     * Contructor de la clase ActorBox2d
     * Inicializa el Actor con su tamaño y posición en coordenadas de pantalla
     * basadas en las dimensiones de un `Rectangle` proporcionado, escalando
     * de metros a píxeles.
     * @param world el mundo donde se creara la figura (instancia de World de Box2D).
     * @param shape dimension y posicion que tendrá (un objeto Rectangle que define el ancho, alto y coordenadas X, Y en metros).
     */
    public ActorBox2d(World world, Rectangle shape){
        this.world = world;
        // Establece el tamaño del Actor en píxeles, escalando desde las dimensiones en metros del 'shape'.
        setSize(PIXELS_IN_METER * shape.width, PIXELS_IN_METER * shape.height);
        // Establece la posición del Actor en píxeles, escalando desde las coordenadas en metros del 'shape'.
        setPosition(shape.x * PIXELS_IN_METER, shape.y * PIXELS_IN_METER);
    }

    /**
     * Obtiene el cuerpo físico (Body) de Box2D asociado a este Actor.
     * @return El objeto Body de Box2D.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Método vacío que se pretende sobrescribir en las subclases para manejar
     * el inicio de un contacto físico con otro ActorBox2d.
     * Es invocado cuando dos cuerpos entran en contacto en el mundo físico.
     * @param actor El otro ActorBox2d con el que se ha producido el contacto.
     * @param game La instancia de GameScreen, que podría ser necesaria para acceder
     * a la lógica del juego o a otros elementos de la pantalla.
     */
    public void beginContactWith(ActorBox2d actor, GameScreen game){}

    /**
     * Libera los recursos de Box2D asociados a este ActorBox2d.
     * Destruye la fixture principal, itera sobre todas las demás fixtures
     * del cuerpo para destruirlas y finalmente destruye el cuerpo completo
     * del mundo de Box2D. Esto es crucial para evitar fugas de memoria en el motor físico.
     */
    public void detach(){
        // Destruye la fixture principal o primera asociada al cuerpo.
        body.destroyFixture(fixture);
        // Itera sobre la lista de fixtures restantes del cuerpo y las destruye una por una.
        // Esto asegura que todas las formas de colisión asociadas al cuerpo sean liberadas.
        for (int i = 0; i< body.getFixtureList().size; i++){
            body.destroyFixture(body.getFixtureList().get(i));
        }
        // Destruye el cuerpo físico completo del mundo de Box2D.
        world.destroyBody(body);

        // La siguiente línea está comentada, pero si estuviera activa, imprimiría información
        // sobre el Actor, la fixture y el body liberados, útil para depuración.
        //System.out.println( this + " liberado del mundo " + fixture + body);
    }
}
