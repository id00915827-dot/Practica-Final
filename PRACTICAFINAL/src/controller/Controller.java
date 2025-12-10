package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Model;
import model.Question;
import model.repository.RepositoryException;
import view.BaseView;

public class Controller {

    private final Model modelo;
    private final BaseView vista;

    public Controller(Model modelo, BaseView vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
    }

    public void iniciarAplicacion() {
        vista.init();
    }

    public void finalizarAplicacion() {
        vista.end();
    }

    public List<Question> obtenerPreguntas() {
        try {
            return modelo.obtenerTodasPreguntasOrdenadas();
        } catch (RepositoryException e) {
            vista.mostrarError("Error al obtener las preguntas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Question buscarPreguntaPorId(UUID id) {
        try {
            for (Question pregunta : modelo.obtenerTodasLasPreguntas()) {
                if (id.equals(pregunta.getId())) {
                    return pregunta;
                }
            }
        } catch (RepositoryException e) {
            vista.mostrarError("Error al buscar la pregunta: " + e.getMessage());
        }
        return null;
    }

    public void crearPregunta(Question pregunta) {
        try {
            modelo.crearPregunta(pregunta);
            vista.mostrarMensaje("Pregunta creada correctamente.");
        } catch (RepositoryException | IllegalArgumentException e) {
            vista.mostrarError("Error al crear la pregunta: " + e.getMessage());
        }
    }

    public void modificarPregunta(Question pregunta) {
        try {
            modelo.modificarPregunta(pregunta);
            vista.mostrarMensaje("Pregunta modificada correctamente.");
        } catch (RepositoryException | IllegalArgumentException e) {
            vista.mostrarError("Error al modificar la pregunta: " + e.getMessage());
        }
    }

    public void eliminarPregunta(Question pregunta) {
        try {
            modelo.eliminarPregunta(pregunta);
            vista.mostrarMensaje("Pregunta eliminada correctamente.");
        } catch (RepositoryException e) {
            vista.mostrarError("Error al eliminar la pregunta: " + e.getMessage());
        }
    }
}
