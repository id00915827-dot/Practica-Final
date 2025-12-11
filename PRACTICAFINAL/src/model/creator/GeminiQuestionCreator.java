package model.creator;

import com.google.genai.types.Schema;
import es.usal.genai.GenAiConfig;
import es.usal.genai.GenAiFacade;
import es.usal.genai.SimpleSchemas;
import model.Option;
import model.Question;
import model.creator.dto.OptionDTO;
import model.creator.dto.QuestionDTO;

import java.util.ArrayList;
import java.util.List;

public class GeminiQuestionCreator implements QuestionCreator {

    private final String modeloGemini;
    private final String apiKey;
    private final String descripcion;

    public GeminiQuestionCreator(String modeloGemini, String apiKey) {
        this.modeloGemini = modeloGemini;
        this.apiKey = apiKey;
        this.descripcion = "Gemini (" + modeloGemini + ")";
    }

    @Override
public Question crearPregunta(String tema) throws QuestionCreatorException {
    try {
        GenAiConfig config;

        if (apiKey == null || apiKey.isBlank()) {
            config = GenAiConfig.fromEnv(modeloGemini);
        } else {
            config = GenAiConfig.forGemini(modeloGemini, apiKey);
        }

        GenAiConfig.setSilentMode(); 

        try (GenAiFacade genai = new GenAiFacade(config)) {
            Schema esquema = SimpleSchemas.from(QuestionDTO.class);
            String prompt = construirPrompt(tema);
            QuestionDTO preguntaDTO = genai.generateJson(prompt, esquema, QuestionDTO.class);
            return convertirAQuestion(preguntaDTO, tema);
        }
    } catch (Exception e) {
        throw new QuestionCreatorException(
                "Error generando la pregunta con Gemini: " + e.getMessage(),
                e
        );
    }
}


    private String construirPrompt(String tema) {
        return "Eres un generador de preguntas tipo test para exámenes universitarios.\n"
                + "Crea UNA única pregunta de opción múltiple sobre el tema: \"" + tema + "\".\n"
                + "Rellena el objeto QuestionDTO respetando estas reglas:\n"
                + "- statement: enunciado claro de la pregunta.\n"
                + "- author: usa \"Gemini\" como autor.\n"
                + "- topics: lista de temas en MAYÚSCULAS, incluye siempre \"" + tema + "\".\n"
                + "- options: exactamente 4 opciones.\n"
                + "  * text: texto de la opción.\n"
                + "  * rationale: explicación breve de por qué es correcta o incorrecta.\n"
                + "  * correct: true solo en UNA opción, false en las demás.\n"
                + "No inventes campos nuevos y no dejes campos vacíos.";
    }

    private Question convertirAQuestion(QuestionDTO dto, String tema) throws QuestionCreatorException {
        if (dto == null) {
            throw new QuestionCreatorException("La respuesta de Gemini es nula");
        }

        String autor = dto.author != null && !dto.author.isBlank()
                ? dto.author
                : "Gemini";

        String temasTexto = tema;

        List<Option> opciones = new ArrayList<>();
        if (dto.options != null) {
            for (OptionDTO opcionDTO : dto.options) {
                if (opcionDTO == null) {
                    continue;
                }
                String texto = opcionDTO.text != null ? opcionDTO.text : "";
                String justificacion = opcionDTO.rationale != null ? opcionDTO.rationale : "";
                boolean correcta = opcionDTO.correct;
                opciones.add(new Option(texto, justificacion, correcta));
            }
        }

        while (opciones.size() < 4) {
            opciones.add(new Option("Opción " + (opciones.size() + 1),
                    "Opción de relleno generada localmente.", false));
        }

        if (opciones.size() > 4) {
            opciones = new ArrayList<>(opciones.subList(0, 4));
        }

        try {
            return Question.crearNuevaPregunta(autor, temasTexto, dto.statement, opciones);
        } catch (IllegalArgumentException e) {
            throw new QuestionCreatorException("Pregunta generada no válida", e);
        }
    }

    @Override
    public String getQuestionCreatorDescription() {
        return descripcion;
    }
}
