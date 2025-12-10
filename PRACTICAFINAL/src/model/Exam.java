package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exam implements Serializable {


    private final List<Question> preguntas;
    private final List<Integer> respuestas; 
    private int indiceActual;

    public Exam(List<Question> preguntas) {
        this.preguntas = new ArrayList<>(preguntas);
        this.respuestas = new ArrayList<>(Collections.nCopies(preguntas.size(), -1));
        this.indiceActual = 0;
    }

    public Question getPreguntaActual() {
        if (indiceActual < 0 || indiceActual >= preguntas.size()) {
            return null;
        }
        return preguntas.get(indiceActual);
    }

    public void responderActual(int indiceOpcion) {
        if (indiceActual < 0 || indiceActual >= preguntas.size()) {
            return;
        }
        respuestas.set(indiceActual, indiceOpcion);
        avanzar();
    }

    public void saltarActual() {
        avanzar();
    }

    private void avanzar() {
        indiceActual++;
        while (indiceActual < preguntas.size() && respuestas.get(indiceActual) != -1) {
            indiceActual++;
        }
    }

    public boolean haTerminado() {
        return indiceActual >= preguntas.size() || preguntas.isEmpty();
    }

    public int getTotalPreguntas() {
        return preguntas.size();
    }

    public int contarAciertos() {
        int aciertos = 0;
        for (int i = 0; i < preguntas.size(); i++) {
            int indiceRespuesta = respuestas.get(i);
            if (indiceRespuesta >= 0 && indiceRespuesta < preguntas.get(i).getOptions().size()) {
                if (preguntas.get(i).getOptions().get(indiceRespuesta).isCorrect()) {
                    aciertos++;
                }
            }
        }
        return aciertos;
    }

    public int contarFallos() {
        int fallos = 0;
        for (int i = 0; i < preguntas.size(); i++) {
            int indiceRespuesta = respuestas.get(i);
            if (indiceRespuesta >= 0 && indiceRespuesta < preguntas.get(i).getOptions().size()) {
                if (!preguntas.get(i).getOptions().get(indiceRespuesta).isCorrect()) {
                    fallos++;
                }
            }
        }
        return fallos;
    }

    public int contarSinResponder() {
        int sinResponder = 0;
        for (int respuesta : respuestas) {
            if (respuesta == -1) {
                sinResponder++;
            }
        }
        return sinResponder;
    }
}
