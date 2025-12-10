package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Question implements Serializable {


    private UUID id;
    private String author;
    private HashSet<String> topics;
    private String statement;
    private java.util.List<Option> options;
    private transient LocalDateTime fechaCreacion;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String autor) {
        this.author = autor;
    }

    public HashSet<String> getTopics() {
        return topics;
    }

    public void setTopics(HashSet<String> temas) {
        this.topics = temas;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String enunciado) {
        this.statement = enunciado;
    }

    public java.util.List<Option> getOptions() {
        return options;
    }

    public void setOptions(java.util.List<Option> opciones) {
        this.options = opciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setTopicsDesdeCadena(String temasTexto) {
        HashSet<String> nuevosTemas = new HashSet<>();
        if (temasTexto != null) {
            String[] partes = temasTexto.split(",");
            for (String parte : partes) {
                String tema = parte;
                if (!tema.isEmpty()) {
                    nuevosTemas.add(tema.toUpperCase(Locale.ROOT));
                }
            }
        }
        this.topics = nuevosTemas;
    }

    public static Question crearNuevaPregunta(String autor, String temasTexto, String enunciado, List<Option> opciones) {
        Question pregunta = new Question();
        pregunta.setId(UUID.randomUUID());
        pregunta.setAuthor(autor);
        pregunta.setTopicsDesdeCadena(temasTexto);
        pregunta.setStatement(enunciado);
        pregunta.setOptions(opciones);
        pregunta.setFechaCreacion(LocalDateTime.now());
        return pregunta;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Question pregunta = (Question) objeto;
        return Objects.equals(id, pregunta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
