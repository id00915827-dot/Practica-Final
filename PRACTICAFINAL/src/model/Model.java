package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import model.backup.QuestionBackupIO;
import model.backup.QuestionBackupIOException;
import model.repository.IRepository;
import model.repository.RepositoryException;

public class Model {

    private final IRepository repositorio;
    private final QuestionBackupIO gestorCopias;

    private Exam examenActual;

    public Model(IRepository repositorio, QuestionBackupIO gestorCopias) {
        this.repositorio = repositorio;
        this.gestorCopias = gestorCopias;
    }

    //  CRUD 

    public List<Question> obtenerTodasLasPreguntas() throws RepositoryException {
        return repositorio.getAllQuestions();
    }

    public List<Question> obtenerTodasPreguntasOrdenadas() throws RepositoryException {
        List<Question> preguntas = repositorio.getAllQuestions();
        preguntas.sort((p1, p2) -> {
            if (p1.getFechaCreacion() == null && p2.getFechaCreacion() == null) {
                return 0;
            }
            if (p1.getFechaCreacion() == null) {
                return 1;
            }
            if (p2.getFechaCreacion() == null) {
                return -1;
            }
            return p1.getFechaCreacion().compareTo(p2.getFechaCreacion());
        });
        return preguntas;
    }

    public void crearPregunta(Question pregunta) throws RepositoryException {
        validarPregunta(pregunta);
        normalizarPregunta(pregunta);
        repositorio.addQuestion(pregunta);
    }

    public void modificarPregunta(Question pregunta) throws RepositoryException {
        validarPregunta(pregunta);
        normalizarPregunta(pregunta);
        repositorio.modifyQuestion(pregunta);
    }

    public void eliminarPregunta(Question pregunta) throws RepositoryException {
        repositorio.removeQuestion(pregunta);
    }

    //  JSON backup 

    public int exportarPreguntas(String nombreFichero)
            throws QuestionBackupIOException, RepositoryException {

        List<Question> preguntas = repositorio.getAllQuestions();
        gestorCopias.exportQuestions(preguntas, nombreFichero);
        return preguntas.size();
    }

    public int importarPreguntas(String nombreFichero)
            throws QuestionBackupIOException, RepositoryException {

        List<Question> actuales = repositorio.getAllQuestions();
        Set<UUID> idsActuales = actuales.stream()
                .map(Question::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        List<Question> importadas = gestorCopias.importQuestions(nombreFichero);
        int contador = 0;

        for (Question pregunta : importadas) {
            UUID id = pregunta.getId();
            if (id == null || idsActuales.contains(id)) {
                continue;
            }
            validarPregunta(pregunta);
            normalizarPregunta(pregunta);
            repositorio.addQuestion(pregunta);
            idsActuales.add(pregunta.getId());
            contador++;
        }

        return contador;
    }

    //  EXAMEN 

    public List<String> obtenerTemasDisponibles() throws RepositoryException {
        List<Question> preguntas = repositorio.getAllQuestions();
        HashSet<String> temas = new HashSet<>();
        for (Question pregunta : preguntas) {
            if (pregunta.getTopics() != null) {
                temas.addAll(pregunta.getTopics());
            }
        }
        List<String> lista = new ArrayList<>(temas);
        lista.sort(String::compareTo);
        return lista;
    }

    public void iniciarExamen(String tema, int numeroPreguntas) throws RepositoryException {
        List<Question> todas = repositorio.getAllQuestions();
        List<Question> seleccionadas = new ArrayList<>();

        String temaMayus = tema == null ? "" : tema.toUpperCase(Locale.ROOT);

        for (Question pregunta : todas) {
            if ("TODOS".equalsIgnoreCase(tema)) {
                seleccionadas.add(pregunta);
            } else if (pregunta.getTopics() != null && pregunta.getTopics().contains(temaMayus)) {
                seleccionadas.add(pregunta);
            }
        }

        java.util.Collections.shuffle(seleccionadas);

        if (numeroPreguntas > 0 && numeroPreguntas < seleccionadas.size()) {
            seleccionadas = new ArrayList<>(seleccionadas.subList(0, numeroPreguntas));
        }

        this.examenActual = new Exam(seleccionadas);
    }

    public Question obtenerPreguntaActualExamen() {
        if (examenActual == null) {
            return null;
        }
        return examenActual.getPreguntaActual();
    }

    public void responderPreguntaActual(int indiceOpcion) {
        if (examenActual != null) {
            examenActual.responderActual(indiceOpcion);
        }
    }

    public void saltarPreguntaActual() {
        if (examenActual != null) {
            examenActual.saltarActual();
        }
    }

    public boolean examenHaTerminado() {
        return examenActual == null || examenActual.haTerminado();
    }

    public String obtenerResumenExamen() {
        if (examenActual == null) {
            return "No hay examen en curso.";
        }
        int total = examenActual.getTotalPreguntas();
        int aciertos = examenActual.contarAciertos();
        int fallos = examenActual.contarFallos();
        int sinResponder = examenActual.contarSinResponder();

        return "Resumen examen -> Total: " + total
                + ", aciertos: " + aciertos
                + ", fallos: " + fallos
                + ", sin responder: " + sinResponder + ".";
    }


    private void validarPregunta(Question pregunta) throws RepositoryException {
        if (pregunta == null) {
            throw new RepositoryException("La pregunta no puede ser nula.");
        }
        if (pregunta.getStatement() == null || pregunta.getStatement().trim().isEmpty()) {
            throw new RepositoryException("El enunciado no puede estar vacío.");
        }
        if (pregunta.getOptions() == null || pregunta.getOptions().size() != 4) {
            throw new RepositoryException("La pregunta debe tener exactamente 4 opciones.");
        }
        long correctas = pregunta.getOptions().stream().filter(Option::isCorrect).count();
        if (correctas != 1) {
            throw new RepositoryException("Debe haber exactamente una opción correcta.");
        }
    }

    private void normalizarPregunta(Question pregunta) {
        if (pregunta.getTopics() != null) {
            HashSet<String> normalizados = new HashSet<>();
            for (String tema : pregunta.getTopics()) {
                if (tema != null) {
                    normalizados.add(tema.trim().toUpperCase(Locale.ROOT));
                }
            }
            pregunta.setTopics(normalizados);
        }
    }
}
