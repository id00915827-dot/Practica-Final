package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exam {

    private final List<Question> preguntas;
    private int indiceActual;
    private int aciertos;
    private int fallos;
    private int sinResponder;

    public Exam(List<Question> preguntasExamen) {
        this.preguntas = new ArrayList<>(preguntasExamen);
        Collections.shuffle(this.preguntas);
        this.indiceActual = 0;
        this.aciertos = 0;
        this.fallos = 0;
        this.sinResponder = 0;
    }

    public Question getPreguntaActual() {
        if (indiceActual >= preguntas.size()) {
            return null;
        }
        return preguntas.get(indiceActual);
    }


    public void responderActual(int indiceOpcion) {
        Question pregunta = getPreguntaActual();
        if (pregunta == null) {
            return;
        }

        if (indiceOpcion < 0 || indiceOpcion >= pregunta.getOptions().size()) {
            sinResponder++;
        } else {
            boolean esCorrecta = pregunta.getOptions().get(indiceOpcion).isCorrect();
            if (esCorrecta) {
                aciertos++;
            } else {
                fallos++;
            }
        }

        indiceActual++;
    }

    public void saltarActual() {
        Question pregunta = getPreguntaActual();
        if (pregunta == null) {
            return;
        }
        sinResponder++;
        indiceActual++;
    }

    public boolean haTerminado() {
        return indiceActual >= preguntas.size();
    }

    public int getTotalPreguntas() {
        return preguntas.size();
    }

    public int contarAciertos() {
        return aciertos;
    }

    public int contarFallos() {
        return fallos;
    }

    public int contarSinResponder() {
        return sinResponder;
    }

    public double calcularNotaSobreDiez() {
        int total = getTotalPreguntas();
        if (total == 0) {
            return 0.0;
        }

        double valorPregunta = 10.0 / total;
        double puntuacionBruta = aciertos - (fallos / 3.0); 
        double nota = puntuacionBruta * valorPregunta;

        if (nota < 0) {
            nota = 0;
        }
        if (nota > 10) {
            nota = 10;
        }

        return nota;
    }
}
