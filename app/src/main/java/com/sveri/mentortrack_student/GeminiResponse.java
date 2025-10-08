package com.sveri.mentortrack_student;

import java.util.List;
import java.util.Map;

public class GeminiResponse {
    public List<Candidate> candidates;

    public static class Candidate {
        public String content;
    }
}
