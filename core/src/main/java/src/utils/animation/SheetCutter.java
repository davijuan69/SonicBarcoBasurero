package src.utils.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * La clase SheetCutter proporciona métodos utilitarios para cortar una hoja de textura (spritesheet)
 * en múltiples TextureRegions, que pueden ser usados para animaciones o para extraer sprites individuales.
 */
public class SheetCutter {
    /**
     * Corta una hoja de textura horizontalmente en un número específico de frames.
     * Asume que todos los frames están en una sola fila.
     *
     * @param sheet La textura de la hoja a cortar.
     * @param amount El número de frames en los que se debe cortar la hoja horizontalmente.
     * @return Un array de TextureRegion que contiene los frames cortados.
     */
    public static TextureRegion[] cutHorizontal(Texture sheet, Integer amount){
        // Divide la hoja de textura en una matriz 2D de TextureRegion.
        // El ancho de cada región es el ancho total de la hoja dividido por la cantidad de frames,
        // y la altura es la altura completa de la hoja.
        TextureRegion[][] tmp = TextureRegion.split(sheet,
            sheet.getWidth() / amount,
            sheet.getHeight());
        // Crea un nuevo array para almacenar los frames resultantes.
        TextureRegion[] frames = new TextureRegion[amount];
        // Copia los frames de la primera fila de la matriz temporal al array de frames.
        System.arraycopy(tmp[0], 0, frames, 0, amount);
        return frames;
    }

    /**
     * Corta una hoja de textura verticalmente en un número específico de frames.
     * Asume que todos los frames están en una sola columna.
     *
     * @param sheet La textura de la hoja a cortar.
     * @param amount El número de frames en los que se debe cortar la hoja verticalmente.
     * @return Un array de TextureRegion que contiene los frames cortados.
     */
    public static TextureRegion[] cutVertical(Texture sheet, Integer amount){
        // Divide la hoja de textura en una matriz 2D de TextureRegion.
        // El ancho de cada región es el ancho completo de la hoja,
        // y la altura es la altura total de la hoja dividida por la cantidad de frames.
        TextureRegion[][] tmp = TextureRegion.split(sheet,
            sheet.getWidth(),
            sheet.getHeight() / amount);
        // Crea un nuevo array para almacenar los frames resultantes.
        TextureRegion[] frames = new TextureRegion[amount];
        // Itera a través de las filas de la matriz temporal y copia el primer elemento (columna 0)
        // de cada fila al array de frames.
        for (int i = 0; i < amount; i++) {
            frames[i] = tmp[i][0];
        }
        return frames;
    }

    /**
     * Corta una hoja de textura en una cuadrícula definida por un número específico de filas y columnas.
     *
     * @param sheet La textura de la hoja a cortar.
     * @param rows El número de filas en la cuadrícula.
     * @param columns El número de columnas en la cuadrícula.
     * @return Un array de TextureRegion que contiene todos los frames cortados,
     * dispuestos de izquierda a derecha, de arriba a abajo.
     */
    public static TextureRegion[] cutSheet(Texture sheet, Integer rows, Integer columns){
        // Divide la hoja de textura en una matriz 2D de TextureRegion.
        // El ancho de cada región es el ancho total de la hoja dividido por el número de columnas,
        // y la altura es la altura total de la hoja dividida por el número de filas.
        TextureRegion[][] tmp = TextureRegion.split(sheet,
            sheet.getWidth() / columns,
            sheet.getHeight() / rows);
        // Crea un nuevo array para almacenar todos los frames resultantes.
        // El tamaño del array es el producto del número de filas y columnas.
        TextureRegion[] frames = new TextureRegion[rows * columns];
        // Inicializa un índice para rastrear la posición actual en el array de frames.
        int index = 0;
        // Itera a través de las filas.
        for (int i = 0; i < rows; i++) {
            // Itera a través de las columnas dentro de cada fila.
            for (int j = 0; j < columns; j++) {
                // Asigna cada TextureRegion de la matriz temporal al array de frames,
                // incrementando el índice para la siguiente posición.
                frames[index++] = tmp[i][j];
            }
        }
        return frames;
    }
}
