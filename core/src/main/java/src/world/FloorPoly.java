package src.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import src.utils.constants.CollisionFilters;

/**
 * Clase que representa una colisión de polígono estática del mapa.
 * Se usa para crear colisiones complejas basadas en polígonos definidos en Tiled.
 */
public class FloorPoly extends ActorBox2d {

    public FloorPoly(World world, Vector2[] vertices, float x, float y) {
        super(world, createBoundingBox(vertices, x, y));

        // Crear el cuerpo estático
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        // Crear la forma del polígono
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);

        // Crear la fixture
        fixture = body.createFixture(polygonShape, 0.0f);
        fixture.setUserData(this);

        // Configurar filtros de colisión
        Filter filter = new Filter();
        filter.categoryBits = CollisionFilters.STATIC;
        filter.maskBits = (short) (CollisionFilters.PLAYER | CollisionFilters.ENEMY | CollisionFilters.OTHERPLAYER);
        fixture.setFilterData(filter);

        polygonShape.dispose();
    }

    private static com.badlogic.gdx.math.Rectangle createBoundingBox(Vector2[] vertices, float x, float y) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Vector2 vertex : vertices) {
            minX = Math.min(minX, vertex.x);
            minY = Math.min(minY, vertex.y);
            maxX = Math.max(maxX, vertex.x);
            maxY = Math.max(maxY, vertex.y);
        }

        return new com.badlogic.gdx.math.Rectangle(x + minX, y + minY, maxX - minX, maxY - minY);
    }

    @Override
    public void beginContactWith(ActorBox2d actor, src.screens.game.GameScreen game) {
        // Las colisiones estáticas no necesitan lógica de contacto específica
    }
}
