package src.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import static src.utils.constants.Constants.PIXELS_IN_METER;

/**
 * Clase abstracta que extiende `ActorBox2d` para añadir capacidades de renderizado de sprites y animación.
 * Es la base para cualquier entidad del juego que no solo tenga un cuerpo físico Box2D,
 * sino que también necesite ser dibujada en la pantalla usando un sprite o una animación.
 */
public abstract class ActorBox2dSprite extends ActorBox2d{
    protected Sprite sprite; // El sprite que representa visualmente el actor.
    private Float animateTime; // El tiempo acumulado para controlar el progreso de la animación.
    private Animation<TextureRegion> currentAnimation; // La animación actualmente activa para el sprite.
    private Boolean flipX; // Indica si el sprite debe voltearse horizontalmente.
    private final Vector2 spritePosModification; // Un vector para ajustar la posición del sprite respecto al cuerpo Box2D.

    /**
     * Constructor de la clase ActorBox2dSprite.
     * Inicializa el ActorBox2d con un mundo y una forma, y además configura
     * el sprite inicial y las variables relacionadas con la animación.
     * @param world El mundo Box2D al que pertenece el cuerpo.
     * @param shape La forma y posición inicial del cuerpo y el sprite.
     * @param assetManager El AssetManager para cargar recursos como texturas.
     */
    public ActorBox2dSprite(World world, Rectangle shape, AssetManager assetManager) {
        super(world, shape); // Llama al constructor de la clase padre (ActorBox2d).
        animateTime = 0f; // Inicializa el tiempo de animación a cero.
        flipX = false; // Por defecto, el sprite no está volteado horizontalmente.
        spritePosModification = new Vector2(0, 0); // Inicializa la modificación de posición del sprite a cero.
        // Crea un nuevo Sprite usando una textura de ejemplo ("logo.png") cargada desde el AssetManager.
        sprite = new Sprite(assetManager.get("logo.png", Texture.class));
        // Establece el tamaño del sprite basado en las dimensiones de la forma y la constante de escalado.
        sprite.setSize(shape.width * PIXELS_IN_METER, shape.height * PIXELS_IN_METER);
    }

    /**
     * Establece la animación actual para el sprite y reinicia el tiempo de animación.
     * @param currentAnimation La animación de tipo `Animation<TextureRegion>` a establecer.
     */
    protected void setCurrentAnimation(Animation<TextureRegion> currentAnimation) {
        this.currentAnimation = currentAnimation;
        animateTime = 0f; // Reinicia el tiempo de animación al cambiar de animación.
    }

    /**
     * Obtiene el tiempo acumulado de la animación actual.
     * @return El tiempo transcurrido en la animación actual.
     */
    public Float getAnimateTime() {
        return animateTime;
    }

    /**
     * Reinicia el tiempo de animación a cero, útil para reiniciar una animación.
     */
    public void resetAnimateTime() {
        animateTime = 0f;
    }

    /**
     * Establece si el sprite debe voltearse horizontalmente.
     * También aplica el volteo al sprite interno.
     * @param flipX Un booleano que indica si se debe voltear (true) o no (false).
     */
    public void setFlipX(Boolean flipX) {
        sprite.setFlip(flipX, false); // Aplica el volteo horizontal al sprite. El volteo vertical es false.
        this.flipX = flipX; // Almacena el estado de volteo.
    }

    /**
     * Comprueba si la animación actual ha terminado de reproducirse.
     * @return true si la animación ha terminado, false en caso contrario.
     */
    public Boolean isAnimationFinish() {
        // Delega la comprobación a la propia animación. El segundo parámetro (false)
        // indica que la animación no está en modo de bucle.
        return currentAnimation.isAnimationFinished(animateTime);
    }

    /**
     * Obtiene el objeto Sprite asociado a este ActorBox2dSprite.
     * @return El objeto Sprite.
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Establece una modificación de posición para el sprite en relación con la posición
     * del cuerpo Box2D. Esto puede ser útil para ajustar el centro del sprite
     * o aplicar offsets visuales sin afectar la física.
     * @param x Desplazamiento en el eje X.
     * @param y Desplazamiento en el eje Y.
     */
    public void setSpritePosModification(Float x, Float y) {
        spritePosModification.set(x,y); // Establece los nuevos valores de modificación.
    }

    /**
     * Comprueba si el sprite está volteado horizontalmente.
     * @return true si el sprite está volteado, false si no lo está.
     */
    public Boolean isFlipX() {
        return flipX;
    }

    /**
     * Sobrescribe el método `draw` de la clase `Actor` para renderizar el sprite.
     * Este método se encarga de actualizar la posición del sprite basándose en
     * la posición del cuerpo Box2D, seleccionar el frame de la animación y dibujarlo.
     * @param batch El Batch utilizado para dibujar el sprite.
     * @param parentAlpha El alfa del padre, que se multiplica con el color del sprite.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Si el cuerpo físico (body) es nulo, no hay nada que dibujar, así que se sale del método.
        if (body == null) return;

        // Actualiza la posición del Actor (y por ende del sprite) en coordenadas de píxeles.
        // La posición del cuerpo Box2D (body.getPosition()) está en metros, se escala a píxeles.
        // Se resta la mitad del ancho/alto para centrar el sprite en la posición del cuerpo Box2D.
        // Se aplica spritePosModification para ajustes finos de posición.
        setPosition(
            body.getPosition().x * PIXELS_IN_METER - getWidth() / 2 + spritePosModification.x,
            body.getPosition().y * PIXELS_IN_METER - getHeight() / 2 + spritePosModification.y
        );

        // Si hay una animación actual, obtiene el frame correspondiente al tiempo de animación actual
        // y lo establece como la región de textura del sprite. 'false' indica que no es un bucle.
        if (currentAnimation != null) sprite.setRegion(currentAnimation.getKeyFrame(animateTime, false));

        // Asegura que el sprite mantenga su estado de volteo horizontal.
        sprite.setFlip(flipX, false); // El volteo vertical es siempre false.

        // Aplica el color del Actor al sprite, incluyendo la transparencia del padre.
        sprite.setColor(getColor());

        // Establece la posición final del sprite para el dibujo.
        sprite.setPosition(getX(), getY());

        // Establece el origen del sprite en su centro, importante para rotaciones o escalados.
        sprite.setOriginCenter();

        // Dibuja el sprite en el Batch.
        sprite.draw(batch);

        // Incrementa el tiempo de animación para el siguiente frame en el siguiente ciclo de renderizado.
        animateTime += Gdx.graphics.getDeltaTime();
    }
}
