package app;

import controller.Controller;
import model.Model;
import model.repository.IRepository;
import model.repository.BinaryRepository;
import view.BaseView;
import view.InteractiveView;

public class App {

    public static void main(String[] args) {
        IRepository repositorio = new BinaryRepository();
        Model modelo = new Model(repositorio);
        BaseView vista = new InteractiveView();
        Controller controlador = new Controller(modelo, vista);

        controlador.iniciarAplicacion();
    }
}
