package model.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Question;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONQuestionBackupIO implements QuestionBackupIO {

    private final Gson gson;

    public JSONQuestionBackupIO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void exportQuestions(List<Question> preguntas, String nombreFichero) throws QuestionBackupIOException {
        try {
            String home = System.getProperty("user.home");
            Path ruta = Paths.get(home, nombreFichero + ".json");
            String json = gson.toJson(preguntas);
            Files.writeString(ruta, json);
        } catch (IOException e) {
            throw new QuestionBackupIOException("Error exportando preguntas a JSON.", e);
        }
    }

    @Override
    public List<Question> importQuestions(String nombreFichero) throws QuestionBackupIOException {
        try {
            String home = System.getProperty("user.home");
            Path ruta = Paths.get(home, nombreFichero + ".json");
            if (!Files.exists(ruta)) {
                return new ArrayList<>();
            }
            String contenido = Files.readString(ruta);
            Type tipoLista = new TypeToken<List<Question>>() {}.getType();
            List<Question> lista = gson.fromJson(contenido, tipoLista);
            if (lista == null) {
                return new ArrayList<>();
            }
            return lista;
        } catch (IOException e) {
            throw new QuestionBackupIOException("Error importando preguntas desde JSON.", e);
        }
    }

    @Override
    public String getBackupIODescription() {
        return "Copias de seguridad en formato JSON";
    }
}
