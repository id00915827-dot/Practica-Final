package model.backup;

import model.Question;

import java.util.List;

public interface QuestionBackupIO {

    void exportQuestions(List<Question> preguntas, String nombreFichero) throws QuestionBackupIOException;

    List<Question> importQuestions(String nombreFichero) throws QuestionBackupIOException;

    String getBackupIODescription();
}
