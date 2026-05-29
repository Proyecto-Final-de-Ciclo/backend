package com.example.demo.domain;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioData {
    private float amount;
    private String base;
    private String date;
    private HashMap<String, Float> rates;
}