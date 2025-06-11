package com.sonic.app.screens.components; // Declara el paquete donde se encuentra esta clase, indicando que es un componente de la interfaz de usuario para las pantallas.

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;

/**
 * La clase `ColorPickerImage` es un componente de interfaz de usuario que extiende `Image`
 * y permite al usuario seleccionar un color de una textura (imagen), asumiendo que la textura es un círculo de color.
 * También implementa `Disposable` para gestionar la liberación de recursos (el Pixmap).
 */
public class ColorPickerImage extends Image implements Disposable {
    private final Pixmap pixmap; // Declara una variable final para almacenar los datos de píxeles de la textura.
    // Se utiliza para leer el color de píxeles específicos.
    private final Color selectColor; // Declara una variable final para almacenar el color seleccionado por el usuario.

    /**
     * Constructor para la clase `ColorPickerImage`.
     * Inicializa el componente con una textura dada y configura el listener de clic.
     *
     * @param texture La textura que se utilizará como selector de color (debería ser una imagen circular de colores).
     */
    public ColorPickerImage(Texture texture) {
        super(texture); // Llama al constructor de la clase padre (Image) con la textura proporcionada.
        this.pixmap = textureToPixmap(texture); // Convierte la Texture en un Pixmap para acceder a los datos de los píxeles.
        selectColor = new Color(); // Inicializa el objeto Color que contendrá el color seleccionado.

        // Añade un ClickListener a esta imagen para detectar cuándo el usuario hace clic en ella.
        addListener(new ClickListener() {
            @Override // Indica que este método sobrescribe un método de la clase padre.
            public void clicked(InputEvent event, float x, float y) {
                // Se llama cuando se hace clic en la imagen. 'x' e 'y' son las coordenadas locales del clic dentro de la imagen.

                // Primero, verifica si el clic ocurrió dentro de un círculo (asumiendo que la imagen es un selector circular).
                if (!isInsideCircle(x, y)) return; // Si el clic está fuera del círculo, no hace nada.

                // Calcula las coordenadas del píxel en el Pixmap.
                // 'y' en Scene2d es de abajo hacia arriba, mientras que Pixmap es de arriba hacia abajo.
                // Por lo tanto, se invierte la coordenada 'y' para que coincida con el Pixmap.
                int pixelX = (int) x;
                int pixelY = (int) (getHeight() - y); // Convierte la coordenada 'y' de Scene2d a la coordenada 'y' de Pixmap.

                // Obtiene el valor de color entero del píxel en las coordenadas calculadas.
                int colorInt = pixmap.getPixel(pixelX, pixelY);

                // Si el color es completamente transparente (0x00000000), no es un color válido para seleccionar,
                // así que no hace nada. Esto es útil si la imagen tiene un fondo transparente.
                if (colorInt == 0x00000000) return;

                // Establece el color seleccionado utilizando el color del píxel.
                // La clase Color de LibGDX puede convertir directamente un entero que representa un color ARGB.
                selectColor.set(colorInt);
            }
        });
    }

    /**
     * Método auxiliar para verificar si un punto (x, y) está dentro de un círculo.
     * Asume que el círculo está centrado en la imagen y tiene un radio basado en la mitad del ancho de la imagen.
     *
     * @param x La coordenada X del punto a verificar.
     * @param y La coordenada Y del punto a verificar.
     * @return true si el punto está dentro del círculo, false en caso contrario.
     */
    private boolean isInsideCircle(float x, float y) {
        float centerX = getWidth() / 2; // Calcula la coordenada X del centro de la imagen.
        float centerY = getHeight() / 2; // Calcula la coordenada Y del centro de la imagen.
        // Calcula el radio del círculo. Se resta 1 para evitar posibles problemas de borde si el borde de la textura tiene píxeles transparentes.
        float radius = pixmap.getWidth() / 2f - 1;

        // Calcula la distancia al cuadrado desde el centro del círculo al punto (x, y).
        float dx = x - centerX;
        float dy = y - centerY;

        // Comprueba si la distancia al cuadrado es menor o igual al radio al cuadrado (fórmula de un círculo).
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    /**
     * Obtiene el color que ha sido seleccionado por el usuario.
     *
     * @return El objeto Color que representa el color seleccionado.
     */
    public Color getSelectColor() {
        return selectColor; // Devuelve el color actualmente seleccionado.
    }

    /**
     * Método auxiliar para convertir una `Texture` en un `Pixmap`.
     * Esto es necesario porque `Texture` está en la GPU y `Pixmap` permite acceder a los datos de píxeles de la CPU.
     *
     * @param texture La textura a convertir.
     * @return El Pixmap que contiene los datos de píxeles de la textura.
     */
    private Pixmap textureToPixmap(Texture texture) {
        texture.getTextureData().prepare(); // Prepara los datos de la textura para su acceso (puede cargarla si no está ya cargada).
        return texture.getTextureData().consumePixmap(); // Consume (obtiene) el Pixmap de los datos de la textura.
        // Nota: Una vez consumido, los datos de la textura no pueden ser preparados nuevamente.
    }

    /**
     * Libera los recursos asociados con este objeto.
     * Es crucial llamar a este método cuando el `ColorPickerImage` ya no sea necesario para evitar fugas de memoria,
     * especialmente el `Pixmap` que consume memoria de la CPU.
     */
    @Override // Indica que este método sobrescribe un método de la interfaz Disposable.
    public void dispose() {
        pixmap.dispose(); // Libera la memoria ocupada por el Pixmap.
    }
}
