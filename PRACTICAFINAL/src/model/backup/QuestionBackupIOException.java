package model.backup;

public class QuestionBackupIOException extends Exception {

    public QuestionBackupIOException(String mensaje) {
        super(mensaje);
    }

    public QuestionBackupIOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
