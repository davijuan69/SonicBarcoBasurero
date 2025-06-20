package src.screens.components; // Declara el paquete donde se encuentra esta clase, indicando que es un componente de la interfaz de usuario para las pantallas.

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * La clase `SpriteAsActor` es un componente de la interfaz de usuario que extiende `Actor` de Scene2d.
 * Su propósito es permitir que un objeto `Sprite` sea utilizado y gestionado como un `Actor`
 * dentro de un sistema de interfaz de usuario de Scene2d (como un `Stage` o `Table`).
 * Esto combina las capacidades de dibujo y transformación de un `Sprite` con las características de manejo de eventos
 * y organización de un `Actor` de Scene2d.
 */
public class SpriteAsActor extends Actor {
    private final Sprite sprite; // La instancia de `Sprite` que esta clase envuelve. Es `final` porque la referencia al Sprite no cambiará.
    private Float width;         // Almacena el ancho actual del sprite/actor.
    private Float height;        // Almacena el alto actual del sprite/actor.

    /**
     * Constructor para la clase `SpriteAsActor`.
     * Crea un nuevo `Sprite` a partir de la `Texture` dada y establece el tamaño inicial del `Actor`
     * para que coincida con las dimensiones de la textura.
     *
     * @param texture La `Texture` que se utilizará para crear el `Sprite`.
     */
    public SpriteAsActor(Texture texture) {
        sprite = new Sprite(texture); // Inicializa el Sprite con la textura proporcionada.
        setSize(sprite.getWidth(), sprite.getHeight()); // Establece el tamaño del Actor para que coincida con el Sprite.
        this.width = sprite.getWidth(); // Inicializa el ancho del Actor.
        this.height = sprite.getHeight(); // Inicializa el alto del Actor.
    }

    /**
     * Sobrescribe el método `setSize` de `Actor`.
     * Cuando se cambia el tamaño del `Actor`, también se actualizan las variables internas `width` y `height`.
     * Las dimensiones del `Sprite` se ajustan en el método `draw()`.
     *
     * @param width El nuevo ancho del Actor.
     * @param height La nueva altura del Actor.
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Actor).
    public void setSize(float width, float height) {
        super.setSize(width, height); // Llama al método `setSize` de la clase padre para actualizar las propiedades de tamaño del Actor.
        this.width = width; // Almacena el nuevo ancho.
        this.height = height; // Almacena el nuevo alto.
    }

    /**
     * Sobrescribe el método `setColor` de `Actor`.
     * Aplica el color especificado al `Sprite` interno.
     *
     * @param color El objeto `Color` a aplicar.
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Actor).
    public void setColor(Color color) {
        sprite.setColor(color); // Establece el color del Sprite interno.
    }

    /**
     * Sobrescribe el método `setColor` de `Actor` para establecer el color con componentes RGBA.
     * Aplica los componentes de color especificados al `Sprite` interno.
     *
     * @param r Componente rojo (0.0 a 1.0).
     * @param g Componente verde (0.0 a 1.0).
     * @param b Componente azul (0.0 a 1.0).
     * @param a Componente alfa (transparencia, 0.0 a 1.0).
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Actor).
    public void setColor(float r, float g, float b, float a) {
        sprite.setColor(r, g, b, a); // Establece el color del Sprite interno usando componentes individuales.
    }

    /**
     * Sobrescribe el método `draw` de `Actor`.
     * Este método es llamado por el `Stage` o la `Table` para dibujar el `Actor`.
     * Dentro de este método, la posición y el tamaño del `Sprite` interno se sincronizan
     * con la posición y el tamaño del `Actor`, y luego el `Sprite` se dibuja.
     *
     * @param batch El `Batch` a utilizar para el dibujo.
     * @param parentAlpha La opacidad del padre.
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Actor).
    public void draw(Batch batch, float parentAlpha) {
        sprite.setPosition(getX(), getY()); // Sincroniza la posición X e Y del Sprite con la del Actor.
        sprite.setSize(getWidth(), getHeight()); // Sincroniza el tamaño del Sprite con el del Actor.
        sprite.draw(batch); // Dibuja el Sprite en el Batch.
    }

    /**
     * Establece una nueva `Texture` para el `Sprite` interno.
     * No cambia el tamaño del `Actor` automáticamente, solo la textura subyacente del `Sprite`.
     *
     * @param texture La nueva `Texture` a establecer.
     */
    public void setTexture(Texture texture) {
        sprite.setTexture(texture); // Cambia la textura del Sprite.
    }

    /**
     * Establece una nueva `TextureRegion` para el `Sprite` interno.
     * Esto es útil cuando se trabaja con hojas de sprites o atlas de texturas.
     * Además, actualiza el tamaño del `Sprite` (y por lo tanto, la base de dibujo del `Actor`)
     * para que coincida con las dimensiones de la `TextureRegion` establecida.
     *
     * @param textureRegion La nueva `TextureRegion` a establecer.
     */
    public void setTextureRegion(TextureRegion textureRegion) {
        sprite.setRegion(textureRegion); // Establece la región de textura del Sprite.
        sprite.setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight()); // Ajusta el tamaño del Sprite a la región.
        width = (float)textureRegion.getRegionWidth(); // Actualiza el ancho almacenado.
        height = (float)textureRegion.getRegionHeight(); // Actualiza el alto almacenado.
    }

    /**
     * Sobrescribe el método `setScale` de `Actor`.
     * Aplica el factor de escala especificado tanto al `Actor` como al `Sprite` interno.
     *
     * @param scaleXY El factor de escala a aplicar en ambos ejes (X e Y).
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Actor).
    public void setScale(float scaleXY) {
        super.setScale(scaleXY); // Llama al método `setScale` de la clase padre para actualizar la escala del Actor.
        sprite.setScale(scaleXY); // Establece la escala del Sprite interno.
    }
}
