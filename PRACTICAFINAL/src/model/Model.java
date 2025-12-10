package model;

import model.backup.QuestionBackupIO;
import model.backup.QuestionBackupIOException;
import model.repository.IRepository;
import model.repository.RepositoryException;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Model {

    private final IRepository repositorio;
    private final QuestionBackupIO gestorCopias;

    public Model(IRepository repositorio, QuestionBackupIO gestorCopias) {
        this.repositorio = repositorio;
        this.gestorCopias = gestorCopias;
    }

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

    // JSON backup 

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

