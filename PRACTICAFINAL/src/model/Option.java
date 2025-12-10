package model;

import java.io.Serializable;

public class Option implements Serializable {


    private String text;
    private String rationale;
    private boolean correct;

    public String getText() {
        return text;
    }

    public void setText(String texto) {
        this.text = texto;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String razon) {
        this.rationale = razon;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correcta) {
        this.correct = correcta;
    }
}

