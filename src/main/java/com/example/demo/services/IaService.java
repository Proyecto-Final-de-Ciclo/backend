package com.example.demo.services;

import java.util.List;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class IaService {

        private static final String SYSTEM_PROMPT = """
                        Eres un asistente técnico especializado en equipos de radioafición.
                        Cuando recibas el nombre de un producto, genera una descripción técnica y objetiva
                        para un anuncio de compraventa, de entre 1 y 3 frases.
                        Incluye características técnicas relevantes del equipo (bandas, modos, potencia,
                        características destacadas) sin usar lenguaje publicitario ni frases de reclamo.
                        Responde ÚNICAMENTE con la descripción, sin saludos, sin explicaciones,
                        sin comillas y sin ningún texto adicional.
                        """;

        private final OpenAiChatModel chatModel;

        public IaService(OpenAiChatModel chatModel) {
                this.chatModel = chatModel;
        }


        // usa Spring AI con OpenAiChatModel, que apunta a OpenRouter, que apunta a Gemini.
        // envia un prompt más el producto que introduzca el usuario.
        public String generarDescripcion(String nombreProducto) {
                var options = OpenAiChatOptions.builder()
                                .model("google/gemini-2.5-flash")
                                .temperature(0.7)
                                .maxTokens(150)
                                .build();

                Prompt prompt = new Prompt(
                                List.of(
                                                new SystemMessage(SYSTEM_PROMPT),
                                                new UserMessage("Producto: " + nombreProducto)),
                                options);

                String resultado = chatModel.call(prompt).getResult().getOutput().getText();

                if (resultado != null && resultado.length() > 500) {
                        resultado = resultado.substring(0, 500);
                }
                return resultado;
        }
}
