package view;

import controller.Controller; 
import model.Option;
import model.Question;
import com.coti.tools.Esdia;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractiveView extends BaseView {

    @Override
public void init() {
    boolean salir = false;

    while (!salir) {
        mostrarMenuPrincipal();
        int opcion = Esdia.readInt("Elige una opción: ", 0, 4);

        switch (opcion) {
            case 1:
                gestionarPreguntas();
                break;
            case 2:
                gestionarCopias();
                break;
            case 3:
                ejecutarModoExamen();
                break;
            case 4:
                if (controlador.hayQuestionCreators()) {
                    crearPreguntaAutomatica();
                } else {
                    mostrarError("No hay question creators disponibles.");
                }
                break;
            case 0:
                salir = true;
                break;
            default:
                mostrarError("Opción no válida.");
                break;
        }
    }

    controlador.finalizarAplicacion();
}


    private void mostrarMenuPrincipal() {
    System.out.println();
    System.out.println("===== Examinator 3000 =====");
    System.out.println("1. Gestión de preguntas");
    System.out.println("2. Importar / Exportar preguntas");
    System.out.println("3. Modo examen");
    if (controlador.hayQuestionCreators()) {
        System.out.println("4. Crear pregunta automática (Gemini)");
    }
    System.out.println("0. Salir");
}


    //  GESTIÓN PREGUNTAS 

    private void gestionarPreguntas() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("=== Gestión de preguntas ===");
            System.out.println("1. Listar preguntas");
            System.out.println("2. Ver detalle de una pregunta");
            System.out.println("3. Crear nueva pregunta");
            System.out.println("4. Modificar pregunta");
            System.out.println("5. Eliminar pregunta");
            System.out.println("0. Volver");

            int opcion = Esdia.readInt("Elige una opción: ", 0, 5);
            switch (opcion) {
                case 1:
                    listarPreguntas();
                    break;
                case 2:
                    verDetallePregunta();
                    break;
                case 3:
                    crearPreguntaManual();
                    break;
                case 4:
                    modificarPregunta();
                    break;
                case 5:
                    eliminarPregunta();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    mostrarError("Opción no válida.");
                    break;
            }
        }
    }

    private void listarPreguntas() {
        List<Question> preguntas = controlador.obtenerPreguntas();
        if (preguntas.isEmpty()) {
            mostrarMensaje("No hay preguntas.");
            return;
        }
        System.out.println();
        System.out.println("=== Preguntas ===");
        for (Question pregunta : preguntas) {
            System.out.println(pregunta.getId() + " - " + pregunta.getStatement());
        }
    }

    private Question seleccionarPreguntaPorId() {
        String textoId = Esdia.readString("Introduce el UUID de la pregunta: ");
        try {
            UUID id = UUID.fromString(textoId.trim());
            Question pregunta = controlador.buscarPreguntaPorId(id);
            if (pregunta == null) {
                mostrarError("No se ha encontrado ninguna pregunta con ese UUID.");
            }
            return pregunta;
        } catch (IllegalArgumentException e) {
            mostrarError("UUID no válido.");
            return null;
        }
    }

    private void verDetallePregunta() {
        Question pregunta = seleccionarPreguntaPorId();
        if (pregunta == null) {
            return;
        }
        System.out.println();
        System.out.println("ID: " + pregunta.getId());
        System.out.println("Autor: " + pregunta.getAuthor());
        System.out.println("Temas: " + pregunta.getTopics());
        System.out.println("Enunciado: " + pregunta.getStatement());
        List<Option> opciones = pregunta.getOptions();
        for (int i = 0; i < opciones.size(); i++) {
            Option opcion = opciones.get(i);
            System.out.println((i + 1) + ". " + opcion.getText()
                    + " [" + (opcion.isCorrect() ? "CORRECTA" : "INCORRECTA") + "]");
            System.out.println("    Justificación: " + opcion.getRationale());
        }
    }

    private void crearPreguntaManual() {
        String autor = Esdia.readString("Autor de la pregunta: ");
        String temasTexto = Esdia.readString("Temas (separados por comas): ");
        String enunciado = Esdia.readString("Enunciado: ");

        System.out.println("Introduce las 4 opciones:");
        List<Option> opciones = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String textoOpcion = Esdia.readString("Texto opción " + i + ": ");
            String justificacion = Esdia.readString("Justificación opción " + i + ": ");
            boolean correcta = leerBooleano("¿Es correcta esta opción? (s/n): ");
            opciones.add(new Option(textoOpcion, justificacion, correcta));
        }

        Question pregunta = Question.crearNuevaPregunta(autor, temasTexto, enunciado, opciones);
        controlador.crearPregunta(pregunta);
    }

    private void modificarPregunta() {
        Question pregunta = seleccionarPreguntaPorId();
        if (pregunta == null) {
            return;
        }

        System.out.println("Pulsa Enter para dejar el valor actual.");

        String autor = leerTextoOpcional("Autor [" + pregunta.getAuthor() + "]: ");
        if (!autor.isEmpty()) {
            pregunta.setAuthor(autor);
        }

        String temasTexto = leerTextoOpcional("Temas (comas) " + pregunta.getTopics() + ": ");
        if (!temasTexto.isEmpty()) {
            pregunta.setTopicsDesdeCadena(temasTexto);
        }

        String enunciado = leerTextoOpcional("Enunciado [" + pregunta.getStatement() + "]: ");
        if (!enunciado.isEmpty()) {
            pregunta.setStatement(enunciado);
        }

        List<Option> opciones = pregunta.getOptions();
        for (int i = 0; i < opciones.size(); i++) {
            Option opcion = opciones.get(i);
            System.out.println("Opción " + (i + 1) + ": " + opcion.getText());

            String texto = leerTextoOpcional("Nuevo texto (Enter para mantener): ");
            if (!texto.isEmpty()) {
                opcion.setText(texto);
            }

            String justificacion = leerTextoOpcional("Nueva justificación (Enter para mantener): ");
            if (!justificacion.isEmpty()) {
                opcion.setRationale(justificacion);
            }

            String correctoTexto = leerTextoOpcional("¿Es correcta? (s/n) (Enter para mantener): ");
            if (!correctoTexto.isEmpty()) {
                String valor = correctoTexto.trim().toLowerCase();
                boolean correcta = valor.equals("s") || valor.equals("si");
                opcion.setCorrect(correcta);
            }
        }

        controlador.modificarPregunta(pregunta);
    }

    private void eliminarPregunta() {
        Question pregunta = seleccionarPreguntaPorId();
        if (pregunta == null) {
            return;
        }
        boolean confirmar = leerBooleano("¿Seguro que quieres eliminarla? (s/n): ");
        if (confirmar) {
            controlador.eliminarPregunta(pregunta);
        }
    }

    private boolean leerBooleano(String mensaje) {
        while (true) {
            String entrada = Esdia.readString(mensaje).trim().toLowerCase();
            switch (entrada) {
                case "s":
                case "si":
                    return true;
                case "n":
                case "no":
                    return false;
                default:
                    System.out.println("Responde 's' o 'n'.");
            }
        }
    }

    private String leerTextoOpcional(String mensaje) {
        System.out.print(mensaje);
        java.util.Scanner sc = new java.util.Scanner(System.in);
        return sc.nextLine();
    }

    //  MODO EXAMEN 

    private void ejecutarModoExamen() {
        List<String> temas = controlador.obtenerTemasDisponibles();
        if (temas.isEmpty()) {
            mostrarMensaje("No hay preguntas para generar un examen.");
            return;
        }

        System.out.println();
        System.out.println("=== Temas disponibles ===");
        System.out.println("0. TODOS");
        for (int i = 0; i < temas.size(); i++) {
            System.out.println((i + 1) + ". " + temas.get(i));
        }

        int opcionTema = Esdia.readInt("Elige un tema (número): ", 0, temas.size());
        String temaSeleccionado;
        if (opcionTema == 0) {
            temaSeleccionado = "TODOS";
        } else {
            temaSeleccionado = temas.get(opcionTema - 1);
        }

        int numeroPreguntas = Esdia.readInt("Número de preguntas del examen: ", 1, 1000);

        controlador.iniciarExamen(temaSeleccionado, numeroPreguntas);
        Instant inicio = Instant.now();

        while (!controlador.examenHaTerminado()) {
            Question pregunta = controlador.obtenerPreguntaActualExamen();
            if (pregunta == null) {
                break;
            }

            System.out.println();
            System.out.println("Pregunta:");
            System.out.println(pregunta.getStatement());
            List<Option> opciones = pregunta.getOptions();
            for (int i = 0; i < opciones.size(); i++) {
                System.out.println((i + 1) + ". " + opciones.get(i).getText());
            }
            System.out.println("0. Saltar pregunta");

            int respuesta = Esdia.readInt("Tu respuesta: ", 0, opciones.size());
            if (respuesta == 0) {
                controlador.saltarPreguntaActual();
            } else {
                controlador.responderPreguntaActual(respuesta - 1);
            }
        }

        Instant fin = Instant.now();
        long segundos = Duration.between(inicio, fin).getSeconds();

        System.out.println();
        System.out.println(controlador.obtenerResumenExamen());
        System.out.println("Tiempo empleado: " + segundos + " segundos.");
    }

    //  COPIAS JSON 

    private void gestionarCopias() {
        boolean volver = false;
        while (!volver) {
            System.out.println();
            System.out.println("=== Copias de seguridad ===");
            System.out.println("1. Exportar preguntas a JSON");
            System.out.println("2. Importar preguntas desde JSON");
            System.out.println("0. Volver");

            int opcion = Esdia.readInt("Elige una opción: ", 0, 2);
            switch (opcion) {
                case 1:
                    String nombreExportar = Esdia.readString("Nombre del fichero (sin ruta, sin extensión): ");
                    controlador.exportarPreguntas(nombreExportar);
                    break;
                case 2:
                    String nombreImportar = Esdia.readString("Nombre del fichero (sin ruta, sin extensión): ");
                    controlador.importarPreguntas(nombreImportar);
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    mostrarError("Opción no válida.");
                    break;
            }
        }
    }

    //gemini question creator

    private void crearPreguntaAutomatica() {
    List<String> descripciones = controlador.obtenerDescripcionesQuestionCreators();
    if (descripciones.isEmpty()) {
        mostrarMensaje("No hay question creators disponibles.");
        return;
    }

    System.out.println();
    System.out.println("=== Question creators disponibles ===");
    for (int i = 0; i < descripciones.size(); i++) {
        System.out.println((i + 1) + ". " + descripciones.get(i));
    }

    int indice = Esdia.readInt("Elige un question creator: ", 1, descripciones.size()) - 1;
    String tema = Esdia.readString("Tema de la pregunta: ");
    controlador.crearPreguntaAutomatica(tema, indice);
}


    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public void mostrarError(String mensaje) {
        System.err.println(mensaje);
    }

    @Override
    public void end() {
        System.out.println("Saliendo de Examinator 3000. ¡Hasta luego!");
    }
}
