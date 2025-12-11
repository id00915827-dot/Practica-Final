package app;

import controller.Controller;
import java.util.ArrayList;
import java.util.List;
import model.Model;
import model.backup.JSONQuestionBackupIO;
import model.backup.QuestionBackupIO;
import model.creator.GeminiQuestionCreator;
import model.creator.QuestionCreator;
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

            List<QuestionCreator> creadoresPregunta = new ArrayList<>();

            if (args.length == 3 && "-question-creator".equals(args[0])) {
                String modeloGemini = args[1];
                String apiKey = args[2];
                creadoresPregunta.add(new GeminiQuestionCreator(modeloGemini, apiKey));
            }

            Model modelo = new Model(repositorio, gestorCopias, creadoresPregunta);
            BaseView vista = new InteractiveView();
            Controller controlador = new Controller(modelo, vista);

            controlador.iniciarAplicacion();
        } catch (RepositoryException e) {
            System.err.println("Error inicializando el repositorio: " + e.getMessage());
        }
    }
}
