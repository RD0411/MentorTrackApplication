package com.sveri.mentortrack_student;

import java.util.List;
import java.util.Map;

public class GeminiRequest {

    private String prompt;
    private int maxOutputTokens = 500;
    private double temperature = 0.7;

    public GeminiRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public double getTemperature() {
        return temperature;
    }
}
