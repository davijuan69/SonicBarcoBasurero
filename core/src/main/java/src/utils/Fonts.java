package src.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import src.utils.constants.MyColors;

/**
 * La clase `Fonts` centraliza la carga y gestión de todas las fuentes de texto (`BitmapFont`)
 * que se utilizan en el juego. Esto asegura consistencia en el estilo visual y facilita la
 * gestión de recursos, ya que todas las fuentes se cargan al inicio y se liberan
 * correctamente al finalizar, implementando la interfaz `Disposable` de LibGDX para evitar
 * pérdidas de memoria.
 */
public class Fonts implements Disposable {
    // Definición de las fuentes BitmapFont que serán accesibles públicamente.
    public final BitmapFont interFont; // Fuente Inter para texto general.
    public final BitmapFont interNameFont; // Fuente Inter para nombres con borde.
    public final BitmapFont interNameFontSmall; // Fuente Inter para nombres pequeños con borde.
    public final BitmapFont briFont; // Fuente Bricolage Grotesque para texto general.
    public final BitmapFont briTitleFont; // Fuente Bricolage Grotesque para títulos con borde y sombra.
    public final BitmapFont briBorderFont; // Fuente Bricolage Grotesque con borde.

    /**
     * Constructor de la clase `Fonts`.
     * Se encarga de inicializar cada una de las fuentes utilizando `FreeTypeFontGenerator`
     * y la utilidad `FontCreator.createFont()` con parámetros específicos de estilo.
     */
    public Fonts(){
        // --- Configuración de briFont ---
        // Se crea un generador de fuentes a partir del archivo TTF de Bricolage Grotesque.
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/Bricolage_Grotesque/BricolageGrotesque_48pt-Regular.ttf"));
        // Se genera la fuente briFont con tamaño 48 y color blanco, sin parámetros adicionales de borde o sombra.
        briFont = FontCreator.createFont(48, Color.WHITE, generator, new FreeTypeFontGenerator.FreeTypeFontParameter());

        // --- Configuración de interFont ---
        // Se cambia el generador para usar el archivo TTF de Inter.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/Inter/Inter_28pt-Regular.ttf"));
        // Se genera interFont con tamaño 40 y color blanco.
        interFont = FontCreator.createFont(40, Color.WHITE, generator, new FreeTypeFontGenerator.FreeTypeFontParameter());

        // --- Configuración de briTitleFont y briBorderFont ---
        // Se vuelve a usar el generador de Bricolage Grotesque.
        generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/Bricolage_Grotesque/BricolageGrotesque_48pt-Regular.ttf"));
        // Se crea un objeto de parámetros para configurar el estilo de la fuente.
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // Se configura el ancho del borde.
        parameter.borderWidth = 4;
        // Se establece el color del borde utilizando un color personalizado de `MyColors`.
        parameter.borderColor = MyColors.BLUE;
        // Se establece el color de la sombra, también de `MyColors`.
        parameter.shadowColor = MyColors.BLUE;
        // Se define el desplazamiento de la sombra en X (hacia la izquierda).
        parameter.shadowOffsetX = -2;
        // Se define el desplazamiento de la sombra en Y (hacia arriba).
        parameter.shadowOffsetY = 2;
        // Se genera briTitleFont con tamaño 48 y color amarillo, usando los parámetros configurados.
        briTitleFont = FontCreator.createFont(48, MyColors.YELLOW, generator, parameter);

        // Se modifican los parámetros existentes para crear briBorderFont.
        // Se cambia el color del borde a negro.
        parameter.borderColor = Color.BLACK;
        // Se elimina la sombra al establecer su color en nulo.
        parameter.shadowColor = null;
        // Se restablecen los desplazamientos de la sombra a cero.
        parameter.shadowOffsetX = 0;
        parameter.shadowOffsetY = 0;
        // Se genera briBorderFont con tamaño 48 y color blanco, con el borde negro y sin sombra.
        briBorderFont = FontCreator.createFont(48, Color.WHITE, generator, parameter);

        // --- Configuración de interNameFont y interNameFontSmall ---
        // Se vuelve a usar el generador de Inter.
        generator= new FreeTypeFontGenerator(Gdx.files.internal("ui/fonts/Inter/Inter_28pt-Regular.ttf"));
        // Se crea un nuevo objeto de parámetros para estas fuentes.
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // Se configura el ancho del borde.
        parameter.borderWidth = 3;
        // Se establece el color del borde a negro.
        parameter.borderColor = Color.BLACK;
        // Se genera interNameFont con tamaño 32 y color rojo, con el borde negro.
        interNameFont = FontCreator.createFont(32, MyColors.RED, generator, parameter);
        // Se modifica el ancho del borde para la versión pequeña.
        parameter.borderWidth = 2;
        // Se genera interNameFontSmall con tamaño 14 y color rojo, con un borde más delgado.
        interNameFontSmall = FontCreator.createFont(14, MyColors.RED, generator, parameter);
    }



    /**
     * Implementación del método `dispose()` de la interfaz `Disposable`.
     * Este método es vital para liberar los recursos de memoria y las texturas
     * asociadas a cada `BitmapFont` cuando ya no son necesarios, evitando así
     * pérdidas de memoria en la aplicación LibGDX.
     */
    @Override
    public void dispose() {
        interFont.dispose();
        interNameFont.dispose();
        interNameFontSmall.dispose();
        briFont.dispose();
        briTitleFont.dispose();
        briBorderFont.dispose();
    }
}
