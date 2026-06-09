package com.example.demo.services;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.NoticiaDto;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Service
public class NoticiaService {

    private static final String[] FEEDS = {
            "https://www.ure.es/feed",
            "https://ea1uro.com/radio/feed",
            "https://selvamarnoticias.com/feed"
    };

    private static final String[] FUENTES = {
            "URE",
            "EA1URO",
            "Selvamar Noticias"
    };


    // pilla las noticias del último mes, limpia etiquetas HTML y las pasa a noticiasDto. luego las ordena por fecha descendiente.
    public List<NoticiaDto> obtenerNoticias() {
        List<NoticiaDto> resultado = new ArrayList<>();
        LocalDate haceUnMes = LocalDate.now().minusDays(30);

        // para cada feed
        for (int i = 0; i < FEEDS.length; i++) {
            try {
                // se pasa de string a URL
                URL url = new URL(FEEDS[i]);
                // crea el lector de noticias de la librería Rome
                SyndFeedInput input = new SyndFeedInput();
                // se descarga el XML de las noticias de la URL y
                // se parsea con input.bild para que devuelva el
                // SyndFeed con todas las noticias estrcturadas
                SyndFeed feed = input.build(new XmlReader(url));

                // para cada noticia del feed
                for (SyndEntry entry : feed.getEntries()) {
                    if (entry.getPublishedDate() == null)
                        continue;

                    // convertimos la fecha de la noticia a LocalDate
                    LocalDate fecha = entry.getPublishedDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    if (fecha.isAfter(haceUnMes)) {
                        String descripcion = "";
                        if (entry.getDescription() != null) {
                            // quitamos las etiquetas HTML que pueda tener
                            descripcion = entry.getDescription().getValue()
                                    .replaceAll("<[^>]*>", "")
                                    .replaceAll("&nbsp;", " ")
                                    .replaceAll("&amp;", "&")
                                    .replaceAll("&lt;", "<")
                                    .replaceAll("&gt;", ">")
                                    .replaceAll("&quot;", "\"")
                                    .replaceAll("&#[0-9]+;", "")
                                    .replaceAll("\\[…\\]", "")
                                    .replaceAll("\\s+", " ")
                                    .trim();
                        }

                        resultado.add(new NoticiaDto(
                                entry.getTitle(),
                                descripcion,
                                entry.getLink(),
                                fecha,
                                FUENTES[i]));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error leyendo feed " + FUENTES[i] + ": " + e.getMessage());
            }
        }

        resultado.sort(Comparator.comparing(NoticiaDto::getFecha).reversed());
        return resultado;
    }
}