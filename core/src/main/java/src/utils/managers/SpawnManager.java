package src.utils.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * La clase `SpawnManager` gestiona un conjunto de puntos de aparición (spawn points) en un juego.
 * Permite añadir puntos de aparición disponibles, asignar un punto de aparición a una entidad (identificada por un ID),
 * liberar un punto de aparición para que vuelva a estar disponible, y reasignar un punto de aparición a una entidad.
 * Esto es útil para controlar dónde aparecen jugadores, enemigos u objetos en el entorno del juego.
 */
public class SpawnManager {
    private final ArrayList<Vector2> spawnPoints; // Una lista de puntos de aparición disponibles (Vector2).
    private final HashMap<Integer, Vector2> takenSpawnPoints; // Un mapa que asocia IDs de entidades con los puntos de aparición que han tomado.
    private final Random random; // Un objeto Random para seleccionar puntos de aparición aleatorios.

    /**
     * Constructor para el `SpawnManager`.
     * Inicializa las listas y el generador de números aleatorios.
     */
    public SpawnManager() {
        spawnPoints = new ArrayList<>();
        takenSpawnPoints = new HashMap<>();
        random = new Random();
    }

    /**
     * Añade un punto de aparición a la lista de puntos disponibles.
     * @param spawnPoint El Vector2 que representa el punto de aparición a añadir.
     */
    public void add(Vector2 spawnPoint) {
        spawnPoints.add(spawnPoint);
    }

    /**
     * Asigna un punto de aparición aleatorio de los disponibles a una entidad específica.
     * El punto de aparición tomado se elimina de la lista de `spawnPoints` disponibles
     * y se añade a `takenSpawnPoints` con el ID de la entidad.
     * @param id El ID de la entidad que tomará el punto de aparición.
     * @return El Vector2 del punto de aparición asignado, o `null` si no hay puntos disponibles.
     */
    public Vector2 takeSpawnPoint(int id) {
        // Si no hay puntos de aparición disponibles, se registra un mensaje y se retorna null.
        if (spawnPoints.isEmpty()) {
            Gdx.app.log("SpawnManager", "No hay spawn points disponibles");
            return null;
        }

        // Selecciona un punto de aparición aleatorio de la lista de disponibles, lo elimina y lo asigna.
        Vector2 newTakenSpawnPoint = new Vector2(spawnPoints.remove(random.nextInt(spawnPoints.size())));
        // Almacena el punto tomado en el mapa con el ID de la entidad. Se usa `new Vector2(newTakenSpawnPoint)`
        // para asegurar que se guarda una copia y no la misma referencia que se eliminó.
        takenSpawnPoints.put(id, new Vector2(newTakenSpawnPoint));
        return newTakenSpawnPoint;
    }

    /**
     * Libera un punto de aparición que había sido tomado por una entidad, haciéndolo de nuevo disponible.
     * @param id El ID de la entidad que liberará su punto de aparición.
     */
    public void unTakeSpawnPoint(int id) {
        // Comprueba si el ID existe en los puntos de aparición tomados.
        if (!takenSpawnPoints.containsKey(id)) {
            Gdx.app.log("SpawnManager", "id " + id + " no encontrada");
            return;
        }

        // Elimina el punto de aparición del mapa de tomados y lo añade de nuevo a la lista de disponibles.
        // Se usa `new Vector2(takenSpawnPoints.remove(id))` para añadir una copia del Vector2.
        add(new Vector2(takenSpawnPoints.remove(id)));
    }

    /**
     * Realiza un "respawn" para una entidad, asignándole un nuevo punto de aparición.
     * Si la entidad ya tenía un punto tomado, este se libera y luego se le asigna uno nuevo.
     * @param id El ID de la entidad a reaparecer.
     * @return El nuevo Vector2 del punto de aparición asignado.
     */
    public Vector2 reSpawn(int id){
        // Verifica si la entidad ya tiene un punto de aparición tomado.
        boolean contain = takenSpawnPoints.containsKey(id);

        Vector2 lastSpawnPoint = null;
        // Si la entidad tenía un punto, lo remueve de `takenSpawnPoints` y guarda una copia.
        if (contain) lastSpawnPoint = new Vector2(takenSpawnPoints.remove(id));

        // Asigna un nuevo punto de aparición a la entidad.
        Vector2 newSpawnPoint = takeSpawnPoint(id);
        // Si la entidad tenía un punto de aparición previo, lo añade de nuevo a la lista de disponibles.
        // Esto asegura que el punto anterior no se pierda si se necesita reutilizar.
        if (contain) add(lastSpawnPoint);
        return newSpawnPoint;
    }

    /**
     * Limpia todos los puntos de aparición, tanto los disponibles como los tomados.
     */
    public void clear() {
        spawnPoints.clear();
        takenSpawnPoints.clear();
    }
}
