package view;

import com.coti.tools.Esdia;
import controller.Controller;

public class InteractiveView extends BaseView {

    @Override
    public void init() {
        boolean salir = false;

        while (!salir) {
            mostrarMenuPrincipal();
            int opcion = Esdia.readInt("Elige una opción: ", 0, 1);

            switch (opcion) {
                case 1:
                    System.out.println("Gestión de preguntas (todavía no implementada).");
                    break;
                case 0:
                    salir = true;
                    break;
                default:
                    mostrarError("Opción no válida.");
                    break;
            }
        }

        controlador.finalizarAplicacion();
    }

    private void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("===== Examinator 3000 =====");
        System.out.println("1. Gestión de preguntas");
        System.out.println("0. Salir");
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public void mostrarError(String mensaje) {
        System.err.println(mensaje);
    }

    @Override
    public void end() {
        System.out.println("Saliendo de Examinator 3000. ¡Hasta luego!");
    }
}
