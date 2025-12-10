package model.repository;

public class RepositoryException extends Exception {

    public RepositoryException(String mensaje) {
        super(mensaje);
    }

    public RepositoryException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}


