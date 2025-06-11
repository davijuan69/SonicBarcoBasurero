package com.sonic.app.screens.components.chat; // Declara el paquete donde se encuentra esta clase, indicando que es un componente de chat para las pantallas.

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * La clase `Chat` extiende `Table` y representa un componente de interfaz de usuario para mostrar mensajes de chat.
 * Está diseñada para ser una tabla que ocupa toda la pantalla y donde los mensajes se añaden en la parte inferior,
 * apilándose hacia arriba.
 */
public class Chat extends Table {
    private final Label.LabelStyle labelStyle; // Declara una variable final para almacenar el estilo que se aplicará a los mensajes del chat.

    /**
     * Constructor para la clase `Chat`.
     * Crea una instancia de un componente de chat dentro de una tabla.
     *
     * @param labelStyle El estilo (`Label.LabelStyle`) que se aplicará a todos los mensajes de texto añadidos al chat.
     * Este estilo define la fuente, el color, etc., de los mensajes.
     */
    public Chat(Label.LabelStyle labelStyle){
        this.labelStyle = labelStyle; // Asigna el estilo de etiqueta proporcionado a la variable de instancia.
        setFillParent(true); // Hace que esta tabla (el chat) ocupe todo el espacio de su padre (generalmente el Stage).
        pad(20);             // Añade un padding (relleno) de 20 píxeles alrededor del contenido de la tabla.
        bottom();            // Alinea el contenido de la tabla a la parte inferior. Esto significa que los mensajes nuevos
        // se añadirán en la parte inferior y se "empujarán" hacia arriba a medida que se añaden más.
    }

    /**
     * Añade un nuevo mensaje al chat.
     * Crea un nuevo objeto `Message` (presumiblemente otra clase de componente para un mensaje individual)
     * con el texto y el estilo de etiqueta definidos para el chat, y lo añade a la tabla.
     *
     * @param message El texto del mensaje que se va a añadir al chat.
     */
    public void addMessage(String message){
        Message newMessage = new Message(message, labelStyle); // Crea una nueva instancia de Message con el texto y el estilo.
        // Añade el nuevo mensaje a la tabla.
        // `expandX()`: Permite que el mensaje se expanda horizontalmente para ocupar el espacio disponible.
        // `fillX()`: Obliga al mensaje a llenar completamente el espacio horizontal asignado.
        // `left()`: Alinea el contenido del mensaje a la izquierda dentro de su celda.
        // `row()`: Pasa a la siguiente fila de la tabla, asegurando que cada mensaje esté en su propia fila.
        add(newMessage).expandX().fillX().left().row();
    }
}
