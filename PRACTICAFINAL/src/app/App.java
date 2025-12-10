package app;

import controller.Controller;
import model.Model;
import model.backup.JSONQuestionBackupIO;
import model.backup.QuestionBackupIO;
import model.repository.BinaryRepository;
import model.repository.IRepository;
import model.repository.RepositoryException;
import view.BaseView;
import view.InteractiveView;

public class App {

    public static void main(String[] args) {
        try {
            IRepository repositorio = new BinaryRepository();
            QuestionBackupIO gestorCopias = new JSONQuestionBackupIO();
            Model modelo = new Model(repositorio, gestorCopias);
            BaseView vista = new InteractiveView();
            Controller controlador = new Controller(modelo, vista);

            controlador.iniciarAplicacion();
        } catch (RepositoryException e) {
            System.err.println("Error inicializando el repositorio: " + e.getMessage());
        }
    }
}
