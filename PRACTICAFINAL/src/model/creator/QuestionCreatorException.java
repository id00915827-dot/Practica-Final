package model.creator;

public class QuestionCreatorException extends Exception {

    public QuestionCreatorException(String mensaje) {
        super(mensaje);
    }

    public QuestionCreatorException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
