package com.example.demo.services;

import java.net.HttpURLConnection;
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

    public List<NoticiaDto> obtenerNoticias() {
        List<NoticiaDto> resultado = new ArrayList<>();
        LocalDate haceUnMes = LocalDate.now().minusDays(30);

        for (int i = 0; i < FEEDS.length; i++) {
            try {
                // Abrimos la conexión HTTP manualmente para configurarla
                URL url = new URL(FEEDS[i]);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

                // User-Agent: sin esto, muchos sitios WordPress devuelven 403
                conexion.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (compatible; RadiOferta/1.0)");

                // Timeouts de 10 segundos para no quedarnos colgados
                conexion.setConnectTimeout(10_000);
                conexion.setReadTimeout(10_000);

                // Leemos el feed desde la conexión ya configurada
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(conexion.getInputStream()));

                for (SyndEntry entry : feed.getEntries()) {
                    if (entry.getPublishedDate() == null)
                        continue;

                    LocalDate fecha = entry.getPublishedDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    if (fecha.isAfter(haceUnMes)) {
                        String descripcion = "";
                        if (entry.getDescription() != null) {
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
                // Ahora con timeout de 10s, este catch salta rápido en vez de esperar 3 min
                System.err.println("Error leyendo feed " + FUENTES[i] + ": " + e.getMessage());
            }
        }

        resultado.sort(Comparator.comparing(NoticiaDto::getFecha).reversed());
        return resultado;
    }
}