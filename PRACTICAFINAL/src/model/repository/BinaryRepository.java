package model.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Question;

public class BinaryRepository implements IRepository {

    private final List<Question> preguntas = new ArrayList<>();
    private final String rutaFichero;

    public BinaryRepository() throws RepositoryException {
        String home = System.getProperty("user.home");
        this.rutaFichero = home + File.separator + "questions.bin";
        cargarDesdeFichero();
    }

    private void cargarDesdeFichero() throws RepositoryException {
        File fichero = new File(rutaFichero);
        if (!fichero.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                preguntas.clear();
                preguntas.addAll((List<Question>) obj);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryException("Error leyendo el fichero binario.", e);
        }
    }

    private void guardarEnFichero() throws RepositoryException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaFichero))) {
            oos.writeObject(new ArrayList<>(preguntas));
        } catch (IOException e) {
            throw new RepositoryException("Error escribiendo el fichero binario.", e);
        }
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
        guardarEnFichero();
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
        guardarEnFichero();
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
        guardarEnFichero();
    }

    @Override
    public ArrayList<Question> getAllQuestions() throws RepositoryException {
        return new ArrayList<>(preguntas);
    }
}
