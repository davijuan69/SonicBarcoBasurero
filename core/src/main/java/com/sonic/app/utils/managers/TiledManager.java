package com.sonic.app.utils.managers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sonic.app.screens.game.GameScreen;
import com.sonic.app.utils.constants.ConsoleColor;
import com.sonic.app.world.entities.Entity;
import com.sonic.app.world.statics.FloorPoly;
import com.sonic.app.world.statics.StaticFactory;

import java.util.ArrayList;

import static com.sonic.app.utils.constants.Constants.PIXELS_IN_METER;

/**
 * La clase `TiledManager` se encarga de cargar y parsear los datos de un mapa Tiled (.tmx).
 * Extrae objetos del mapa como entidades estáticas, entidades dinámicas, puntos de aparición
 * y puntos de aparición de jugadores, y los integra en el `GameScreen` del juego.
 * Facilita la configuración del mundo del juego a partir de un diseño de mapa creado en Tiled.
 */
public class TiledManager {
    private final GameScreen game; // Referencia a la pantalla del juego para añadir elementos.
    private TiledMap tiledmap; // El objeto TiledMap cargado.
    private Integer tiledSize; // El tamaño en píxeles de un tile en el mapa.

    /**
     * Constructor para el `TiledManager`.
     * @param game La instancia de `GameScreen` a la que se añadirán los elementos parseados del mapa.
     */
    public TiledManager(GameScreen game) {
        this.game = game;
    }

    /**
     * Carga un mapa Tiled desde la ruta especificada y configura el renderizador.
     * @param map La ruta al archivo .tmx del mapa.
     * @return Un `OrthogonalTiledMapRenderer` configurado para dibujar el mapa.
     */
    public OrthogonalTiledMapRenderer setupMap(String map) {
        tiledmap = new TmxMapLoader().load(map); // Carga el archivo .tmx en un objeto TiledMap.
        // Obtiene el tamaño del tile (ancho/alto) de las propiedades del mapa.
        tiledSize = tiledmap.getProperties().get("tilewidth", Integer.class);

        // Retorna un renderizador que escala el mapa de píxeles del tile a las unidades de mundo
        // definidas por PIXELS_IN_METER.
        return new OrthogonalTiledMapRenderer(tiledmap, PIXELS_IN_METER/tiledSize);
    }

    /**
     * Parsea los objetos de una capa del mapa y los convierte en entidades estáticas del juego.
     * Soporta tanto objetos de polígono (para colisiones de suelo complejas) como objetos de rectángulo.
     * @param objects Los objetos de una capa del mapa (MapObjects).
     */
    public void parsedStaticMap(MapObjects objects) {
        for (MapObject object : objects) {
            // Si el objeto es un polígono (definido en Tiled), se procesa como un `FloorPoly`.
            if (object instanceof PolygonMapObject polygonObject) {
                Polygon polygon = polygonObject.getPolygon();

                // Obtener los atributos del polígono (vértices, posición X, posición Y).
                float[] vertices = polygon.getVertices();
                float x = polygon.getX();
                float y = polygon.getY();

                // Convierte los vértices del polígono de píxeles a unidades de mundo (dividiendo por tiledSize).
                Vector2[] verticesVector = new Vector2[vertices.length/2];
                for (int i = 0; i < vertices.length; i+=2) {
                    verticesVector[i/2] = new Vector2(vertices[i]/tiledSize, vertices[i+1]/tiledSize);
                }

                // Crea una nueva instancia de `FloorPoly` y la añade como un actor en el juego.
                FloorPoly newFloorPoly = new FloorPoly(game.getWorld(),
                    new Rectangle(x/tiledSize, y/tiledSize, 1, 1), // La posición del polígono se escala.
                    verticesVector);
                game.addActor(newFloorPoly);
            }

            // Intenta obtener el tipo de la propiedad "type" del objeto.
            String type = object.getProperties().get("type", String.class);
            // Obtiene las propiedades de posición y tamaño del objeto, escalando a unidades de mundo.
            Float X = (Float) object.getProperties().get("x");
            Float Y = (Float) object.getProperties().get("y");
            Float W = (Float) object.getProperties().get("width");
            Float H = (Float) object.getProperties().get("height");

            // Si el tipo no está definido (es null), se asume que es un tipo de "FLOOR" por defecto.
            if (type == null) {
                game.addStatic(StaticFactory.Type.FLOOR, new Rectangle(X/tiledSize, Y/tiledSize, W/tiledSize, H/tiledSize));
                continue; // Pasa al siguiente objeto.
            }
            try{
                // Intenta crear y añadir una entidad estática usando el tipo obtenido del mapa.
                game.addStatic(StaticFactory.Type.valueOf(type), new Rectangle(X/tiledSize, Y/tiledSize, W/tiledSize, H/tiledSize));
            }
            catch (IllegalArgumentException e) {
                // Si el tipo de estático no es válido (no coincide con un enum `StaticFactory.Type`), imprime un error.
                System.out.println(ConsoleColor.GRAY +  "Tipo de static " + type + " no encontrado" + ConsoleColor.RESET);
            }

        }
    }

    /**
     * Parsea los objetos de una capa del mapa y los convierte en entidades dinámicas del juego.
     * @param objects Los objetos de una capa del mapa (MapObjects).
     */
    public void parsedEntityMap(MapObjects objects) {
        // Se crea un ArrayList temporal para evitar ConcurrentModificationException si se modificara el objeto original.
        ArrayList<MapObject> objectArray = new ArrayList<>();
        for (MapObject object : objects) {
            objectArray.add(object);
        }
        for (MapObject object : objectArray) {
            // Obtiene el tipo de entidad y su posición, escalando a unidades de mundo.
            String type = object.getProperties().get("type", String.class);
            float X = object.getProperties().get("x", Float.class) / tiledSize;
            float Y = object.getProperties().get("y", Float.class )/ tiledSize;

            try{
                // Intenta crear y añadir una entidad usando el tipo y la posición. Se pasa (0,0) para velocidad inicial.
                game.addEntity(Entity.Type.valueOf(type), new Vector2(X, Y), new Vector2(0,0));
            }catch (IllegalArgumentException e) {
                // Si el tipo de entidad no es válido, imprime un error.
                System.out.println(ConsoleColor.GRAY + "Tipo de entidad " + type + " no encontrado" + ConsoleColor.RESET);
            }
        }
    }

    /**
     * Parsea los objetos de una capa del mapa y los añade como puntos de aparición en el `SpawnManager` del juego.
     * Específicamente, busca puntos de aparición para el tipo de entidad "MIRROR".
     * @param objects Los objetos de una capa del mapa (MapObjects).
     */
    public void parsedSpawnMap(MapObjects objects) {
        for (MapObject object : objects) {
            // Obtiene el tipo de entidad y su posición, escalando a unidades de mundo.
            String type = object.getProperties().get("type", String.class);
            float X = object.getProperties().get("x", Float.class) / tiledSize;
            float Y = object.getProperties().get("y", Float.class )/ tiledSize;

            // Si el tipo de entidad es "MIRROR", añade su posición al `spawnMirror` del juego.
            if (Entity.Type.valueOf(type) == Entity.Type.MIRROR) {
                game.spawnMirror.add(new Vector2(X, Y));
            }
        }
    }

    /**
     * Parsea los objetos de una capa del mapa para determinar la posición de aparición del jugador.
     * @param objects Los objetos de una capa del mapa (MapObjects).
     */
    public void parsedPlayer(MapObjects objects) {
        for (MapObject object : objects) {
            // Obtiene el tipo del objeto y su posición, escalando a unidades de mundo.
            String type = object.getProperties().get("type", String.class);
            float X = object.getProperties().get("x", Float.class) / tiledSize;
            float Y = object.getProperties().get("y", Float.class )/ tiledSize;

            // Si el tipo es null, se asume que es un punto de aparición estándar de jugador.
            if (type == null) {
                game.spawnPlayer.add(new Vector2(X, Y));
                continue; // Pasa al siguiente objeto.
            }
            // Si hay un tipo definido (ej. "lobby"), se usa como la posición del jugador en el lobby.
            game.lobbyPlayer = new Vector2(X, Y);

        }
    }

    /**
     * Libera los recursos del mapa Tiled.
     */
    public void dispose() {
        tiledmap.dispose();
    }

    /**
     * Procesa las capas del mapa que contienen la información principal del mundo del juego:
     * puntos de aparición del jugador, estáticos y puntos de aparición genéricos.
     */
    public void makeMap() {
        parsedPlayer(tiledmap.getLayers().get("playerSpawn").getObjects()); // Procesa la capa de aparición del jugador.
        parsedStaticMap(tiledmap.getLayers().get("static").getObjects()); // Procesa la capa de objetos estáticos.
        parsedSpawnMap(tiledmap.getLayers().get("spawn").getObjects()); // Procesa la capa de puntos de aparición.
    }

    /**
     * Procesa las capas del mapa que contienen entidades dinámicas (ej. enemigos, otros objetos interactivos).
     */
    public void makeEntities() {
        parsedEntityMap(tiledmap.getLayers().get("entity").getObjects()); // Procesa la capa de entidades generales.
        parsedEntityMap(tiledmap.getLayers().get("enemy").getObjects()); // Procesa la capa de enemigos (aunque duplicado en makeEnemy).
    }

    /**
     * Procesa específicamente la capa del mapa que contiene entidades enemigas.
     */
    public void makeEnemy() {
        parsedEntityMap(tiledmap.getLayers().get("enemy").getObjects()); // Procesa la capa de enemigos.
    }
}
