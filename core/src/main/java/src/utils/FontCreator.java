package src.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator; //No se porque da error

/**
 * La clase `FontCreator` proporciona un método estático para crear y configurar objetos `BitmapFont`
 * a partir de un generador de fuentes FreeType. Esta utilidad simplifica el proceso de creación
 * de fuentes personalizadas con diferentes tamaños, colores y filtros de textura.
 */
public class FontCreator {
    /**
     * Crea y configura una fuente `BitmapFont` usando un generador FreeType.
     * @param size El tamaño de la fuente en píxeles.
     * @param color El color deseado para la fuente.
     * @param generator La instancia de `FreeTypeFontGenerator` utilizada para generar la fuente.
     * @param parameter Los parámetros de configuración de la fuente que se modificarán y usarán.
     * @return Una nueva instancia de `BitmapFont` configurada con los parámetros especificados.
     */
    public static BitmapFont createFont(int size, Color color, FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        // Configura el tamaño de la fuente en los parámetros.
        parameter.size = size;
        // Configura el color de la fuente en los parámetros.
        parameter.color = color;
        // Habilita la generación incremental de la fuente, lo que puede ser útil para grandes juegos
        // donde se necesitan muchos caracteres y se cargan a medida que se usan.
        parameter.incremental = true;

        // Genera la fuente BitmapFont utilizando los parámetros configurados.
        BitmapFont font = generator.generateFont(parameter);
        // Establece el color de la fuente directamente en el objeto BitmapFont.
        font.setColor(color);
        // Aplica un filtro de textura lineal a la región de la textura de la fuente.
        // Esto suaviza la apariencia de la fuente cuando se escala, evitando que se vea pixelada.
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Genera y retorna una nueva fuente con los parámetros actuales.
        // NOTA: Se está llamando a `generator.generateFont(parameter)` dos veces.
        // La primera llamada asigna a `font`, luego se configura `font`,
        // y la segunda llamada genera *otra* fuente que es la que se retorna.
        // Esto podría ser un error si la intención era devolver la primera fuente configurada.
        return generator.generateFont(parameter);
    }
}
