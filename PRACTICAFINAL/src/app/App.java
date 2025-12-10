package app;

import controller.Controller;
import model.Model;
import view.BaseView;
import view.InteractiveView;

public class App {

    public static void main(String[] args) {
        Model modelo = new Model();
        BaseView vista = new InteractiveView();
        Controller controlador = new Controller(modelo, vista);

        controlador.iniciarAplicacion();
    }
}
