package model.repository;

import model.Question;

import java.util.ArrayList;

public interface IRepository {

    Question addQuestion(Question pregunta) throws RepositoryException;

    void removeQuestion(Question pregunta) throws RepositoryException;

    void modifyQuestion(Question pregunta) throws RepositoryException;

    ArrayList<Question> getAllQuestions() throws RepositoryException;
}

