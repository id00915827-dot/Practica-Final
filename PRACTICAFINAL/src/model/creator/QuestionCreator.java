package model.creator;

import model.Question;

public interface QuestionCreator {
    Question crearPregunta(String tema) throws QuestionCreatorException;
    String getQuestionCreatorDescription();
}
