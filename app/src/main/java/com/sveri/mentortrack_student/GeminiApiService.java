package com.sveri.mentortrack_student;

import com.sveri.mentortrack_student.GeminiRequest;
import com.sveri.mentortrack_student.GeminiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GeminiApiService {

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Header("x-goog-api-key") String apiKey,
            @Body GeminiRequest request
    );
}
