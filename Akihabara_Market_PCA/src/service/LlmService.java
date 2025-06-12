package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

import config.ConfigLoader;

/**
 * Clase que ofrece un servicio para interactuar con una API de
 * modelo de lenguaje (IA) mediante HTTP, enviando prompts y
 * recibiendo respuestas generadas.
 */
public class LlmService {
    // Obtiene la clave API desde un archivo o configuración externa
    private static final String apiKey = ConfigLoader.getProperty("api.key");
    // Obtiene el modelo de IA configurado (por ejemplo, nombre del modelo)
    private static final String model = ConfigLoader.getProperty("model");

    /**
     * Envía un prompt (mensaje) a la API de IA y devuelve la respuesta generada.
     * @param prompt Texto con la pregunta o consulta para la IA.
     * @return Respuesta generada por la IA o mensaje de error en caso de fallo.
     */
    public static String generarRespuesta(String prompt) {
        try {
            // Construye un objeto JSON para el mensaje que enviará la petición
            JsonObject message = new JsonObject();
            message.addProperty("role", "user"); // Define que el mensaje es del usuario
            message.addProperty("content", prompt); // Contenido del prompt enviado

            // Crea un arreglo JSON con los mensajes (aquí sólo uno)
            JsonArray messagesArray = new JsonArray();
            messagesArray.add(message);

            // Construye el cuerpo JSON de la petición, incluyendo el modelo y mensajes
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model); // Modelo de IA a usar
            requestBody.add("messages", messagesArray); // Mensajes para la conversación

            // Construye la petición HTTP POST hacia la API de OpenRouter
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://openrouter.ai/api/v1/chat/completions")) // Endpoint de la API
                .header("Authorization", "Bearer " + apiKey) // Autenticación con token Bearer
                .header("Content-Type", "application/json") // Tipo de contenido enviado
                .header("HTTP-Referer", "https://tu-proyecto.com") // Referencia de origen (opcional)
                .header("X-Title", "AkihabaraMarket") // Header personalizado (opcional)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString())) // Cuerpo JSON en formato texto
                .build();

            // Crea un cliente HTTP para enviar la petición
            HttpClient client = HttpClient.newHttpClient();

            // Envía la petición y recibe la respuesta como cadena JSON
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parsea la respuesta JSON para procesarla
            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();

            // Extrae el texto de la respuesta generada por la IA dentro del JSON
            return responseJson
                .getAsJsonArray("choices")  // Accede al array 'choices'
                .get(0).getAsJsonObject()   // Obtiene el primer elemento
                .getAsJsonObject("message") // Extrae el objeto 'message'
                .get("content").getAsString(); // Obtiene el contenido (respuesta IA)

        } catch (Exception e) {
            // Si ocurre un error en la comunicación o parseo, se devuelve mensaje de error
            return "Error al contactar con la IA: " + e.getMessage();
        }
    }
}
