package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.domain.CambioData;

@Service
public class ServiceCambioMoneda {


    // usa RestClient para llamar a la API.
    public CambioData cambioMoneda(String monedaOrigen, String monedaDestino) {
        RestClient restClient = RestClient.create("https://api.frankfurter.dev/v1/latest");

        return restClient.get()
                .uri("?from=" + monedaOrigen + "&to=" + monedaDestino)
                .retrieve()
                .body(CambioData.class);
    }
}