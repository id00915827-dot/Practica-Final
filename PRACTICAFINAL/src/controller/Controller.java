package controller;

import model.Model;
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

}
