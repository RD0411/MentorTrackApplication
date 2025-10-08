package com.sveri.mentortrack_student;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EligibilityAnalysisActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private Spinner spinnerCompany;
    private MaterialButton btnAnalyze;
    private TextView tvResult;

    private FirebaseFirestore db;

    private List<Map<String, Object>> companiesList = new ArrayList<>();
    private List<String> companyNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility_analysis);

        toolbar = findViewById(R.id.toolbar);
        spinnerCompany = findViewById(R.id.spinnerCompany);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        tvResult = findViewById(R.id.tvResult);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();

        loadCompanies();

        btnAnalyze.setOnClickListener(v -> analyzeEligibility());
    }

    private void loadCompanies() {
        db.collection("placementReports").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        companiesList.add(data);
                        companyNames.add(String.valueOf(data.get("companyName")));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, companyNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCompany.setAdapter(adapter);

                }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load companies", Toast.LENGTH_SHORT).show());
    }

    private void analyzeEligibility() {
        int pos = spinnerCompany.getSelectedItemPosition();
        if (pos < 0 || pos >= companiesList.size()) {
            Toast.makeText(this, "Please select a company", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> selectedCompany = companiesList.get(pos);

        try {
            // 🔹 Prepare prompt text dynamically
            String prompt = "Compare the following student’s profile with company requirements and analyze eligibility:\n\n"
                    + "=== Student Details ===\n"
                    + "Degree: BE\n"
                    + "Department: IT\n"
                    + "CGPA: 9.5\n"
                    + "Backlogs: 0\n"
                    + "Programming Languages: C, C++, Java, Python\n"
                    + "Technical Skills: Full Stack Web Development, Machine Learning\n"
                    + "Certifications: AWS Certified Developer\n"
                    + "Projects: MentorTrack, Home Service, Event Notifier\n"
                    + "Internship Experience: 1.5 month as Advanced Java Developer, 3 month as Android Developer\n"
                    + "Research Papers: 1\n"
                    + "Hackathons Won: 2\n"
                    + "Communication: Average\n"
                    + "Aptitude Score: 65\n"
                    + "Problems Solved: 300\n"
                    + "Gender: Male\n"
                    + "Age: 19\n"
                    + "Location: Pune\n\n"
                    + "=== Company Details ===\n"
                    + "Name: " + safeToString(selectedCompany.get("companyName")) + "\n"
                    + "Criteria: " + safeToString(selectedCompany.get("criteria")) + "\n"
                    + "Rounds: " + safeToString(selectedCompany.get("noOfRounds")) + "\n"
                    + "Skills Required: " + safeToString(selectedCompany.get("skillsRequired")) + "\n"
                    + "Subjects Covered: " + safeToString(selectedCompany.get("subjectsCovered")) + "\n"
                    + "Package: " + safeToString(selectedCompany.get("package")) + " LPA\n"
                    + "Type: " + safeToString(selectedCompany.get("type")) + "\n"
                    + "Suggestion: " + safeToString(selectedCompany.get("suggestion")) + "\n"
                    + "Academic Year: " + safeToString(selectedCompany.get("academicYear")) + "\n\n"
                    + "=== Task ===\n"
                    + "Analyze whether the student meets the company’s expectations. "
                    + "Suggest improvements in technical, aptitude, and communication skills if needed. "
                    + "Provide the output in a well-structured summary.";

            // 🔹 Create JSON body (Gemini format)
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();

            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);
            parts.put(textPart);

            content.put("parts", parts);
            contents.put(content);
            jsonBody.put("contents", contents);

            // 🔹 API key & URL for Gemini
            String apiKey = getString(R.string.gemini_api_key);
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

            // 🔹 Prepare request
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();


            // 🔹 Execute request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(EligibilityAnalysisActivity.this,
                                    "Failed to get analysis: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String result = response.body().string();

                        // Extract text from response JSON
                        try {
                            JSONObject resJson = new JSONObject(result);
                            JSONArray candidates = resJson.optJSONArray("candidates");
                            if (candidates != null && candidates.length() > 0) {
                                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                                JSONArray parts = content.getJSONArray("parts");
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < parts.length(); i++) {
                                    sb.append(parts.getJSONObject(i).optString("text", ""));
                                }

                                String analysis = sb.toString();
                                runOnUiThread(() -> tvResult.setText(analysis));
                            } else {
                                runOnUiThread(() -> tvResult.setText("No analysis returned from model."));
                            }
                        } catch (JSONException e) {
                            runOnUiThread(() -> tvResult.setText("Invalid response format."));
                        }

                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(EligibilityAnalysisActivity.this,
                                        "Error: " + response.message(),
                                        Toast.LENGTH_LONG).show()
                        );
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error forming request JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private String safeToString(Object obj) {
        return (obj != null) ? obj.toString() : "N/A";
    }
}
