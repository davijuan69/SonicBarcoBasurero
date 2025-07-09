package src.screens.uiScreens;

import com.badlogic.gdx.Gdx; // Importa Gdx para obtener el ancho de la pantalla
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table; // Importa Table para la organización
import src.main.Main;
import src.screens.components.LayersManager;
import src.utils.constants.MyColors;

/**
 * La clase `InfoScreen` representa la pantalla de información o "Acerca de" del juego.
 * Muestra información sobre controles, objetivo del juego y detalles técnicos.
 * Hereda de `BlueCircleScreen` para usar su fondo y botón de retorno.
 */
public class InfoScreen extends BlueCircleScreen {

    /**
     * Constructor de la clase `InfoScreen`.
     * Configura el contenido de la pantalla, incluyendo los títulos y descripciones
     * de los controles, el objetivo y la información técnica, utilizando una tabla
     * para una mejor organización visual.
     * @param main La instancia principal del juego para acceder a recursos y cambiar de pantalla.
     */
    public InfoScreen(Main main) {
        // Llama al constructor de la clase padre (BlueCircleScreen)
        // para inicializar el fondo circular y el botón de regreso al menú.
        super(main, "Acerca de", Main.Screens.MENU);

        // Se usa LayersManager para manejar la superposición general de elementos,
        // aunque el contenido principal está dentro de una única tabla.
        LayersManager layersManager = new LayersManager(stageUI, 1);

        // --- Definición de Estilos de Label personalizados para esta pantalla ---
        // Estilo para los títulos de sección (más grandes)
        Label.LabelStyle titleStyle = new Label.LabelStyle(main.fonts.interFont, MyColors.RED);

        // Estilo para el contenido de texto principal (legible, con un color suave)
        Label.LabelStyle contentStyle = new Label.LabelStyle(main.fonts.interFont, Color.RED);

        // Estilo específico para la sección de información técnica (mantiene el color azul)
        Label.LabelStyle techInfoStyle = new Label.LabelStyle(main.fonts.interFont, MyColors.RED);


        // --- Creación de la Tabla principal para organizar todo el contenido ---
        Table contentTable = new Table();
        contentTable.setFillParent(true); // Hace que la tabla ocupe todo el espacio del Stage
        // contentTable.debugAll(); // Descomenta esta línea para ver los bordes de la tabla y celdas (útil para depuración y ajustes)

        // --- Sección de Controles ---
        Label controlsTitle = new Label("Controles:", titleStyle);
        controlsTitle.setFontScale(1.0f); // Tamaño de fuente grande para el título
        // Añade el título a la tabla, alineado a la izquierda y con un margen inferior
        contentTable.add(controlsTitle).padBottom(10).left().row();

        Label controlsContent = new Label(
            "W/A/S/D: Moverse\n",
            contentStyle);
        controlsContent.setFontScale(0.8f); // Tamaño de fuente legible para el contenido
        controlsContent.setWrap(true); // Permite que el texto se ajuste a múltiples líneas
        // Añade el contenido, ajusta su ancho al 70% de la pantalla, con margen inferior y alineación izquierda
        contentTable.add(controlsContent).width(Gdx.graphics.getWidth() * 0.7f).padBottom(30).left().row();

        // --- Sección de Objetivo del Juego ---
        Label objectiveTitle = new Label("Objetivo:", titleStyle);
        objectiveTitle.setFontScale(1.0f);
        contentTable.add(objectiveTitle).padBottom(10).left().row();

        Label objectiveContent = new Label(
            "Limpia el mapa y derrota al malvado Dr. Robotnik para Ganar.",
            contentStyle);
        objectiveContent.setFontScale(0.8f);
        objectiveContent.setWrap(true);
        contentTable.add(objectiveContent).width(Gdx.graphics.getWidth() * 0.7f).padBottom(30).left().row();

        // --- Sección de Información Técnica ---
        Label techInfoTitle = new Label("Información Técnica:", titleStyle);
        techInfoTitle.setFontScale(1.0f);
        contentTable.add(techInfoTitle).padBottom(10).left().row();

        Label techInfoContent = new Label(
            "Librerías utilizadas: LibGDX\n" +
                "Lenguaje de programación: Java\n" +
                "Versión: v0.0.1\n" +
                "Desarrolladores: Julio Solórzano, Juan Acevedo, Ramón Hernández\n" +
                "Colaboradores: Daniel Carreño",
            techInfoStyle); // Usa el estilo específico para la info técnica
        techInfoContent.setFontScale(0.8f);
        techInfoContent.setWrap(true);
        contentTable.add(techInfoContent).width(Gdx.graphics.getWidth() * 0.7f).padBottom(30).left().row();


        // --- Adición de la Tabla al LayersManager ---
        // Se establece la capa 0 para esta tabla.
        layersManager.setZindex(0);
        // La capa se centrará en el Stage.
        layersManager.getLayer().center();
        // La tabla se añade a la capa, expandiéndose para ocupar el espacio central disponible.
        layersManager.getLayer().add(contentTable).expand().fill();
    }

    @Override
    public void show() {
        // Asegúrate de llamar a super.show() si BlueCircleScreen tiene lógica importante aquí.
        super.show();
    }

    @Override
    public void resize(int width, int height) {
        // Asegúrate de que el Stage se actualice correctamente con los cambios de tamaño de pantalla.
        stageUI.getViewport().update(width, height, true);
    }
}
