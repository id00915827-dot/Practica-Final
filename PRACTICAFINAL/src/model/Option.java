package model;

import java.io.Serializable;

public class Option implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text;
    private String rationale;
    private boolean correct;

    public Option() {
    }

    public Option(String texto, String justificacion, boolean correcta) {
        this.text = texto;
        this.rationale = justificacion;
        this.correct = correcta;
    }

    public String getText() {
        return text;
    }

    public void setText(String texto) {
        this.text = texto;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String justificacion) {
        this.rationale = justificacion;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correcta) {
        this.correct = correcta;
    }
}

