package src.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * La clase `Box2dUtils` proporciona métodos utilitarios para manipular cuerpos en el entorno de Box2D.
 * Contiene funciones comunes que simplifican interacciones físicas complejas, como el retroceso (knockback).
 */
public class Box2dUtils {
    /**
     * Aplica un efecto de retroceso (knockback) a un cuerpo `receiver` alejándolo de un cuerpo `attacker`.
     * Este método calcula la dirección para empujar al `receiver` y aplica una fuerza de impulso.
     *
     * @param receiver El `Body` que recibirá el retroceso.
     * @param attacker El `Body` que está causando el retroceso.
     * @param knockback La magnitud de la fuerza de retroceso a aplicar.
     */
    public static void knockbackBody(Body receiver, Body attacker, float knockback) {
        // Calcula la dirección del empuje: vector desde el atacante al receptor, normalizado.
        // `cpy()` crea una copia para evitar modificar la posición original del atacante.
        // `sub(receiver.getPosition())` obtiene un vector del atacante al receptor.
        // `nor()` normaliza el vector para obtener solo la dirección.
        Vector2 pushDirection = attacker.getPosition().cpy().sub(receiver.getPosition()).nor();

        // Establece la velocidad lineal del receptor a cero antes de aplicar el impulso.
        // Esto asegura que el retroceso no se vea afectado por la velocidad previa del cuerpo.
        receiver.setLinearVelocity(0,0);
        // Aplica un impulso lineal al receptor.
        // `pushDirection.scl(-knockback)` invierte la dirección (aleja al receptor del atacante)
        // y lo escala por la magnitud del knockback.
        // `receiver.getWorldCenter()` es el punto donde se aplica el impulso (centro de masa del cuerpo).
        // `true` indica que el impulso es instantáneo y afecta la velocidad.
        receiver.applyLinearImpulse(pushDirection.scl(-knockback), receiver.getWorldCenter(), true);
        // Aplica un impulso lineal adicional en la dirección Y (hacia arriba) para simular un levantamiento
        // o un salto ligero como parte del retroceso, con una magnitud igual a la del knockback.
        receiver.applyLinearImpulse(0,knockback, receiver.getWorldCenter().x, receiver.getWorldCenter().y, true);
    }
}
