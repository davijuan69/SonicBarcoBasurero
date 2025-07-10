package src.utils.managers;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import src.screens.game.GameScreen;
import src.utils.constants.ConsoleColor;

import java.util.ArrayList;

import static src.utils.constants.Constants.PIXELS_IN_METER;
import src.world.FloorPoly;

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
        System.out.println("[DEBUG] Cargando mapa: " + map);
        tiledmap = new TmxMapLoader().load(map); // Carga el archivo .tmx en un objeto TiledMap.
        // Obtiene el tamaño del tile (ancho/alto) de las propiedades del mapa.
        tiledSize = tiledmap.getProperties().get("tilewidth", Integer.class);
        System.out.println("[DEBUG] Tamaño de tile: " + tiledSize);

        System.out.println("[DEBUG] Tilesets encontrados:");
        for (com.badlogic.gdx.maps.tiled.TiledMapTileSet tileset : tiledmap.getTileSets()) {
            System.out.println("[DEBUG] - Tileset: " + tileset.getName() + " con " + tileset.size() + " tiles");
        }

        // Fuerza el filtro de textura a Nearest para todos los tilesets
        for (com.badlogic.gdx.maps.tiled.TiledMapTileSet tileset : tiledmap.getTileSets()) {
            for (com.badlogic.gdx.maps.tiled.TiledMapTile tile : tileset) {
                if (tile instanceof com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile) {
                    com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile staticTile = (com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile) tile;
                    com.badlogic.gdx.graphics.Texture texture = staticTile.getTextureRegion().getTexture();
                    texture.setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest, com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
                }
            }
        }

        System.out.println("[DEBUG] Capas encontradas:");
        for (com.badlogic.gdx.maps.MapLayer layer : tiledmap.getLayers()) {
            System.out.println("[DEBUG] - Capa: " + layer.getName() + " (tipo: " + layer.getClass().getSimpleName() + ")");
        }

        // Retorna un renderizador que escala el mapa sin escalado (1f)
        return new OrthogonalTiledMapRenderer(tiledmap, 1f);
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
                float[] vertices = polygon.getTransformedVertices();
                int numVerts = vertices.length / 2;
                if (numVerts < 3 || numVerts > 8) {
                    //System.out.println("[WARN] Polígono ignorado por tener " + numVerts + " vértices (Box2D solo acepta 3-8)");
                    continue;
                }
                // Verificar vértices repetidos o lados muy pequeños
                boolean valido = true;
                for (int i = 0; i < numVerts; i++) {
                    float x1 = vertices[2*i];
                    float y1 = vertices[2*i+1];
                    float x2 = vertices[2*((i+1)%numVerts)];
                    float y2 = vertices[2*((i+1)%numVerts)+1];
                    float dist = (float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
                    if (dist < 1e-2) {
                        //System.out.println("[WARN] Polígono ignorado por vértices muy juntos o repetidos: " + x1 + "," + y1 + " <-> " + x2 + "," + y2);
                        valido = false;
                        break;
                    }
                }
                System.out.print("[DEBUG] Vértices del polígono: ");
                for (int i = 0; i < numVerts; i++) {
                    System.out.print("(" + vertices[2*i]/tiledSize + "," + vertices[2*i+1]/tiledSize + ") ");
                }
                System.out.println();
                if (!valido) continue;
                float x = 0;
                float y = 0;
                // Convertir los vértices de píxeles a unidades de mundo
                Vector2[] verticesVector = new Vector2[numVerts];
                for (int i = 0; i < vertices.length; i+=2) {
                    verticesVector[i/2] = new Vector2(vertices[i]/tiledSize, vertices[i+1]/tiledSize);
                }
                // Crear y agregar el FloorPoly
                FloorPoly floorPoly = new FloorPoly(game.getWorld(), verticesVector, x, y);
                game.addActor(floorPoly);
                continue;
            }
            // Si es un rectángulo (tiene width y height), también lo agregamos como FloorPoly
            Float X = (Float) object.getProperties().get("x");
            Float Y = (Float) object.getProperties().get("y");
            Float W = (Float) object.getProperties().get("width");
            Float H = (Float) object.getProperties().get("height");
            if (X != null && Y != null && W != null && H != null) {
                if (W <= 0 || H <= 0) {
                    System.out.println("[WARN] Rectángulo ignorado por ancho o alto <= 0: (" + X + "," + Y + "," + W + "," + H + ")");
                    continue;
                }
                // Crear los vértices del rectángulo
                Vector2[] rectVerts = new Vector2[] {
                    new Vector2(0, 0),
                    new Vector2(W/tiledSize, 0),
                    new Vector2(W/tiledSize, H/tiledSize),
                    new Vector2(0, H/tiledSize)
                };
                System.out.print("[DEBUG] Vértices del rectángulo: ");
                for (Vector2 v : rectVerts) {
                    System.out.print("(" + v.x + "," + v.y + ") ");
                }
                System.out.println();
                FloorPoly floorPoly = new FloorPoly(game.getWorld(), rectVerts, X/tiledSize, Y/tiledSize);
                game.addActor(floorPoly);
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
        // parsedPlayer(tiledmap.getLayers().get("playerSpawn").getObjects()); // Procesa la capa de aparición del jugador.
        parsedStaticMap(tiledmap.getLayers().get("colisiones_suelo").getObjects()); // Procesa la capa de colisiones de suelo.
        parsedStaticMap(tiledmap.getLayers().get("colisiones_techo_y_puas_del_tunel").getObjects()); // Procesa la capa de colisiones de techo y puas.
        // parsedSpawnMap(tiledmap.getLayers().get("spawn").getObjects()); // Procesa la capa de puntos de aparición.
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
