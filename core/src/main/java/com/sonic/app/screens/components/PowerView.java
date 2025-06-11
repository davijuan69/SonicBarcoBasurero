package com.sonic.app.screens.components; // Declara el paquete donde se encuentra esta clase, indicando que es un componente de la interfaz de usuario para las pantallas.

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sonic.app.utils.animation.SheetCutter;
import com.sonic.app.utils.animation.SheetCutter;
import com.sonic.app.world.entities.player.powers.PowerUp;

/**
 * La clase `PowerView` es un componente visual de la interfaz de usuario que extiende `SpriteAsActor`.
 * Su propósito es mostrar un ícono que representa el tipo de "power-up" (poder) actual.
 * Utiliza una hoja de sprites para obtener los diferentes íconos de poder.
 */
public class PowerView extends SpriteAsActor { //HOLAAA Hay que adaptar esto para el sonic
    // Define una enumeración interna para mapear los tipos de poder a un orden numérico.
    // Esto es útil para acceder a los íconos en el array `icons` usando su posición ordinal.
    public enum PowerType {
        NORMAL, // Corresponde al estado normal o sin poder.
        WHEEL,  // Ícono para el poder de 'rueda'.
        BOMB,   // Ícono para el poder de 'bomba'.
        SLEEP,  // Ícono para el poder de 'dormir'.
        SWORD,  // Ícono para el poder de 'espada'.
    }

    private final TextureRegion[] icons; // Declara un array de TextureRegion para almacenar todos los íconos de poder.
    // Es `final` porque el array en sí no cambiará después de la inicialización.

    /**
     * Constructor para la clase `PowerView`.
     * Carga las texturas necesarias y corta la hoja de sprites para obtener los íconos individuales.
     *
     * @param assetManager El AssetManager del juego, utilizado para cargar los recursos de textura.
     */
    public PowerView(AssetManager assetManager) {
        // Llama al constructor de la clase padre (`SpriteAsActor`), inicializándolo con una textura "logo.png".
        // Es posible que "logo.png" sea un marcador de posición o la textura por defecto inicial antes de que se establezca un poder.
        super(assetManager.get("logo.png", Texture.class));
        // Carga la hoja de sprites "ui/icons/powerIcons.png" y la corta horizontalmente en 5 regiones iguales.
        // Se asume que esta hoja contiene 5 íconos de poder dispuestos horizontalmente.
        icons = SheetCutter.cutHorizontal(assetManager.get("ui/icons/powerIcons.png"), 5);
    }

    /**
     * Establece el ícono de poder que se mostrará en este `PowerView` basándose en el tipo de `PowerUp` proporcionado.
     * Utiliza la enumeración `PowerType` para mapear el tipo de poder a un índice en el array `icons`.
     *
     * @param type El tipo de `PowerUp` del que se desea mostrar el ícono.
     */
    public void setPower(PowerUp.Type type){
        // Utiliza una declaración `switch` para seleccionar el TextureRegion adecuado según el tipo de poder.
        // `type.ordinal()` devuelve la posición de la constante de enumeración (ej. NORMAL.ordinal() es 0, BOMB.ordinal() es 1, etc.).
        switch (type){
            case NONE -> setTextureRegion(icons[PowerType.NORMAL.ordinal()]); // Si no hay poder, muestra el ícono normal.
            case BOMB ->  setTextureRegion(icons[PowerType.BOMB.ordinal()]);   // Si es una bomba, muestra el ícono de bomba.
            case SLEEP -> setTextureRegion(icons[PowerType.SLEEP.ordinal()]); // Si es dormir, muestra el ícono de dormir.
            case SWORD -> setTextureRegion(icons[PowerType.SWORD.ordinal()]); // Si es espada, muestra el ícono de espada.
            case WHEEL -> setTextureRegion(icons[PowerType.WHEEL.ordinal()]); // Si es rueda, muestra el ícono de rueda.
        }
    }
}
