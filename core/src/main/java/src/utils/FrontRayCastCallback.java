package src.utils; // Declara el paquete al que pertenece esta clase, indicando su naturaleza de utilidad.

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

// Declara la clase FrontRayCastCallback, que implementa la interfaz RayCastCallback.
// Esta clase está diseñada para ser utilizada con la funcionalidad de "raycasting" de Box2D para determinar
// qué es lo primero que golpea el rayo.
public class FrontRayCastCallback implements RayCastCallback {
    private Fixture hitFixture; // Declara una variable miembro privada para almacenar la primera "fixture" golpeada por el rayo.

    /**
     * Este método es llamado por Box2D para cada "fixture" intersectada por el rayo.
     *
     * @param fixture La "fixture" que golpeó el rayo.
     * @param point El punto de intersección en coordenadas del mundo.
     * @param normal El vector normal de la superficie en el punto de intersección.
     * @param fraction La fracción a lo largo del rayo desde el punto de inicio hasta el punto de impacto (0.0 a 1.0).
     * @return La fracción a lo largo del rayo para continuar el "raycast". Devolver `fraction`
     * significa que este es el impacto más cercano hasta ahora, y el "raycast" debería detenerse aquí
     * para encontrar el *primer* impacto.
     */
    @Override // Indica que este método anula un método de la interfaz RayCastCallback.
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        hitFixture = fixture; // Almacena la "fixture" actual como la "fixture" golpeada.
        return fraction; // Devuelve la fracción para indicar que este es el impacto más cercano y que el "raycast" debe terminar aquí.
    }

    /**
     * Devuelve la "fixture" que fue golpeada primero por el "raycast".
     *
     * @return El objeto Fixture que fue golpeado, o null si no se golpeó nada.
     */
    public Fixture getHitFixture() {
        return hitFixture; // Devuelve la "fixture" golpeada almacenada.
    }
}
