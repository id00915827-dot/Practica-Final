package model.repository;

import model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BinaryRepository implements IRepository {

    private final List<Question> preguntas = new ArrayList<>();

    public BinaryRepository() {
    }

    @Override
    public Question addQuestion(Question pregunta) throws RepositoryException {
        if (pregunta == null) {
            throw new RepositoryException("La pregunta no puede ser nula.");
        }
        if (pregunta.getId() == null) {
            pregunta.setId(UUID.randomUUID());
        }
        preguntas.add(pregunta);
        return pregunta;
    }

    @Override
    public void removeQuestion(Question pregunta) throws RepositoryException {
        if (pregunta == null || pregunta.getId() == null) {
            throw new RepositoryException("Pregunta no válida.");
        }
        boolean eliminada = preguntas.removeIf(p -> pregunta.getId().equals(p.getId()));
        if (!eliminada) {
            throw new RepositoryException("La pregunta no existe en el repositorio.");
        }
    }

    @Override
    public void modifyQuestion(Question pregunta) throws RepositoryException {
        if (pregunta == null || pregunta.getId() == null) {
            throw new RepositoryException("Pregunta no válida.");
        }
        boolean encontrada = false;
        for (int i = 0; i < preguntas.size(); i++) {
            Question actual = preguntas.get(i);
            if (pregunta.getId().equals(actual.getId())) {
                preguntas.set(i, pregunta);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new RepositoryException("La pregunta no existe en el repositorio.");
        }
    }

    @Override
    public ArrayList<Question> getAllQuestions() throws RepositoryException {
        return new ArrayList<>(preguntas);
    }
}
