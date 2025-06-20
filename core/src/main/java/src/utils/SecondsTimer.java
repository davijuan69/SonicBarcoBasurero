package src.utils; // Declara el paquete donde se encuentra esta clase.

// Declara la clase SecondsTimer, que es un temporizador simple que cuenta hacia atrás.
public class SecondsTimer {
    private final Float saveTime; // Declara una variable final (constante) para almacenar el tiempo inicial del temporizador.
    // Se utiliza para reiniciar el temporizador.
    private Float timer;          // Declara la variable 'timer' que contendrá el tiempo restante actual.
    private Boolean finished;     // Declara una variable booleana para indicar si el temporizador ha terminado de contar.

    /**
     * Constructor para la clase SecondsTimer.
     * Inicializa el temporizador con la cantidad de minutos y segundos especificados.
     *
     * @param minutes Los minutos iniciales para el temporizador.
     * @param seconds Los segundos iniciales para el temporizador.
     */
    public SecondsTimer(Integer minutes, Integer seconds){
        float time = minutes * 60 + seconds; // Calcula el tiempo total en segundos a partir de los minutos y segundos dados.
        saveTime = time; // Guarda el tiempo total inicial en 'saveTime' para futuros reinicios.
        timer = time;    // Inicializa el temporizador con el tiempo total calculado.
        finished = false; // El temporizador no ha terminado al ser creado.
    }

    /**
     * Reinicia el temporizador a su valor inicial (saveTime).
     * También establece el estado 'finished' a falso.
     */
    public void resetTimer(){
        timer = saveTime;   // Restaura el temporizador a su valor original.
        finished = false;   // Marca el temporizador como no terminado.
    }

    /**
     * Verifica si el temporizador ha terminado de contar.
     *
     * @return true si el temporizador ha llegado a cero o menos, false en caso contrario.
     */
    public Boolean isFinished(){
        return finished; // Devuelve el estado actual de 'finished'.
    }

    /**
     * Actualiza el temporizador restando el 'delta' (tiempo transcurrido desde la última actualización).
     * Si el temporizador ya ha terminado, no hace nada.
     * Si el temporizador llega a cero o menos, lo marca como terminado.
     *
     * @param delta El tiempo transcurrido desde la última vez que se actualizó el temporizador (generalmente en segundos).
     */
    public void update(Float delta){
        if (finished) return; // Si el temporizador ya terminó, sale del método.
        if (timer <= 0) finished = true; // Si el temporizador llega a cero o menos, lo marca como terminado.
        timer -= delta; // Resta el tiempo transcurrido al temporizador.
    }

    /**
     * Sobrescribe el método toString() para proporcionar una representación en cadena del tiempo restante
     * en formato "minutos:segundos".
     *
     * @return Una cadena que representa el tiempo restante en formato "MM:SS".
     */
    @Override // Indica que este método sobrescribe un método de la clase padre (Object).
    public String toString() {
        // Calcula los minutos restantes (dividiendo el tiempo total en segundos por 60).
        // Calcula los segundos restantes (usando el operador módulo % para obtener el resto de la división por 60).
        // Concatena los minutos y segundos con un ":" en el medio.
        return (int)(timer / 60) + ":" + timer.intValue() % 60;
    }
}
