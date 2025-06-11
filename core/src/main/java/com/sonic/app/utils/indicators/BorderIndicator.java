package com.sonic.app.utils.indicators;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.sonic.app.utils.constants.Constants.PIXELS_IN_METER;

/**
 * La clase `BorderIndicator` es un `Actor` de Scene2D que actúa como un indicador visual.
 * Se encarga de mostrar un sprite en el borde de la pantalla, apuntando hacia una `targetPosition`
 * desde una `centerPosition` (normalmente la posición de la cámara o del jugador).
 * Su tamaño y visibilidad cambian en función de la distancia al objetivo y el zoom de la cámara,
 * haciendo que el indicador solo sea visible cuando el objetivo está fuera de la pantalla.
 */
public class BorderIndicator extends Actor{
    private final Sprite sprite; // El sprite que representa el indicador visual.
    private final Vector2 targetPosition; // La posición en píxeles a la que apunta el indicador.
    private final Vector2 centerPosition; // La posición en píxeles desde donde se calcula la dirección del indicador (ej. centro de la cámara).

    /**
     * Constructor para el `BorderIndicator`.
     * @param texture La textura a usar para el sprite del indicador.
     * @param targetPosition La posición inicial del objetivo en metros.
     */
    public BorderIndicator(Texture texture, Vector2 targetPosition){
        sprite = new Sprite(texture);
        // Establece el tamaño inicial del sprite utilizando la constante PIXELS_IN_METER.
        sprite.setSize(PIXELS_IN_METER, PIXELS_IN_METER);
        // Convierte la posición del objetivo de metros a píxeles al inicializarla.
        this.targetPosition = targetPosition.scl(PIXELS_IN_METER);
        // Inicializa la posición central a (0,0) píxeles.
        centerPosition = new Vector2(0, 0);
    }

    /**
     * Cambia la posición del target a la que apunta el indicador.
     * @param targetPosition Vector de posición del objetivo en **metros**.
     */
    public void setTargetPosition(Vector2 targetPosition) {
        // Actualiza la posición del objetivo. Es importante que esta posición ya esté en píxeles
        // o se convierta aquí si el input es en metros. La implementación actual parece esperar
        // que el vector 'targetPosition' ya esté escalado por PIXELS_IN_METER al pasarlo,
        // o que 'targetPosition' se use para escalar *nuevamente* en el método 'act'.
        // Una revisión de la línea 56 indica que se escala de nuevo, lo que podría ser un error.
        this.targetPosition.set(targetPosition);
    }

    /**
     * Cambia la posición del centro desde donde se indica la dirección (normalmente la posición de la cámara).
     * @param centerPosition Vector de posición del centro en **metros**.
     */
    public void setCenterPosition(Vector2 centerPosition) {
        // Actualiza la posición central, convirtiéndola de metros a píxeles.
        this.centerPosition.set(centerPosition.scl(PIXELS_IN_METER));
    }

    /**
     * Método `act` llamado en cada frame para actualizar la lógica del indicador.
     * @param delta El tiempo en segundos desde el último frame.
     */
    @Override
    public void act(float delta) {
        // Calcula la dirección desde el centro hasta el objetivo.
        // NOTA: 'targetPosition.scl(PIXELS_IN_METER)' aquí escala 'targetPosition' de nuevo,
        // lo cual es redundante si ya fue escalado en el constructor o 'setTargetPosition'.
        // Esto podría llevar a que el indicador apunte incorrectamente si targetPosition ya está en píxeles.
        Vector2 direction = new Vector2(targetPosition.scl(PIXELS_IN_METER)).sub(centerPosition).nor();
        // Calcula la distancia máxima desde el centro de la pantalla hasta el borde más cercano,
        // menos la mitad del ancho del indicador para que no se superponga con el borde.
        float distanceScreen = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 2f - getWidth()/2;

        // Calcula la distancia euclidiana entre el centro y el objetivo.
        float distance = centerPosition.dst(targetPosition);
        // Calcula un factor de escala para el sprite.
        // El tamaño del indicador se reduce a medida que el objetivo se acerca a 4000 unidades.
        // Esto hace que el indicador sea más grande cuando el objetivo está muy lejos.
        float scaleFactor = 0.6f + 1.2f * (1.0f - Math.min(distance / 4000, 1.0f));

        // Aplica el factor de escala al tamaño del sprite.
        sprite.setSize(PIXELS_IN_METER * scaleFactor, PIXELS_IN_METER * scaleFactor);

        // Controla la visibilidad del indicador: si el objetivo está dentro de la 'distanceScreen',
        // el indicador se vuelve completamente transparente (no visible). De lo contrario, es opaco.
        if (distance < distanceScreen) sprite.setAlpha(0);
        else sprite.setAlpha(1.0f);

        // Ajusta el zoom de la cámara. Limita el zoom a 1.3f para evitar tamaños excesivos.
        float cameraZoom = 1280.0f / Gdx.graphics.getWidth();
        if (cameraZoom > 1.3f) cameraZoom = 1.3f;

        // Ajusta la 'distanceScreen' en función del zoom de la cámara para que el indicador
        // siempre se mantenga a una distancia visual constante del borde.
        distanceScreen *= cameraZoom * 0.9f;

        // Calcula la posición X e Y del indicador en la pantalla.
        // Se coloca en el borde de la 'distanceScreen' en la dirección del objetivo.
        float posX = centerPosition.x + direction.x * distanceScreen - getWidth() / 2f;
        float posY = centerPosition.y + direction.y * distanceScreen - getHeight() / 2f;

        // Establece la posición final del actor (el indicador).
        setPosition(posX, posY);
    }

    /**
     * Método `draw` para renderizar el sprite del indicador.
     * @param batch El `Batch` utilizado para dibujar el sprite.
     * @param parentAlpha La opacidad heredada de los padres del actor.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Establece la posición del sprite para que coincida con la posición del actor.
        sprite.setPosition(getX(), getY());
        // Establece el origen de la rotación en el centro del sprite.
        sprite.setOriginCenter();
        // Establece la rotación del sprite para que coincida con la rotación del actor.
        sprite.setRotation(getRotation());
        // Dibuja el sprite en el batch.
        sprite.draw(batch);
    }
}


