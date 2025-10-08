package com.sveri.mentortrack_student;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class StudentProgressActivity extends AppCompatActivity {

    private TextInputEditText inputDegree, inputDepartment, inputYear, inputRollNo,
            inputCgpa, inputBacklogs, inputProgrammingLanguages, inputTechnicalSkills,
            inputCertifications, inputProjects, inputInternship, inputResearch,
            inputHackathons, inputCommunication, inputAptitude, inputProblems,
            inputGender, inputAge, inputLocation;

    private MaterialButton btnSubmit;
    private MaterialToolbar toolbar;

    private FirebaseFirestore db;
    private SharedPreferences prefs;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_progress);

        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", null);

        if (email == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        loadExistingData();
        btnSubmit.setOnClickListener(v -> saveProgressToFirestore());
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Student Progress Form");

        inputDegree = findViewById(R.id.inputDegree);
        inputDepartment = findViewById(R.id.inputDepartment);
        inputYear = findViewById(R.id.inputYear);
        inputRollNo = findViewById(R.id.inputRollNo);
        inputCgpa = findViewById(R.id.inputCgpa);
        inputBacklogs = findViewById(R.id.inputBacklogs);
        inputProgrammingLanguages = findViewById(R.id.inputProgrammingLanguages);
        inputTechnicalSkills = findViewById(R.id.inputTechnicalSkills);
        inputCertifications = findViewById(R.id.inputCertifications);
        inputProjects = findViewById(R.id.inputProjects);
        inputInternship = findViewById(R.id.inputInternship);
        inputResearch = findViewById(R.id.inputResearch);
        inputHackathons = findViewById(R.id.inputHackathons);
        inputCommunication = findViewById(R.id.inputCommunication);
        inputAptitude = findViewById(R.id.inputAptitude);
        inputProblems = findViewById(R.id.inputProblems);
        inputGender = findViewById(R.id.inputGender);
        inputAge = findViewById(R.id.inputAge);
        inputLocation = findViewById(R.id.inputLocation);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    /** Load existing Firestore data for the student **/
    private void loadExistingData() {
        DocumentReference docRef = db.collection("students").document(email);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                setText(inputDegree, documentSnapshot.getString("degree"));
                setText(inputDepartment, documentSnapshot.getString("department"));
                setText(inputYear, documentSnapshot.getString("year"));
                setText(inputRollNo, documentSnapshot.getString("rollno"));
                setText(inputCgpa, documentSnapshot.getString("CGPA"));
                setText(inputBacklogs, documentSnapshot.getString("backlogs"));
                setText(inputProgrammingLanguages, documentSnapshot.getString("programmingLanguage"));
                setText(inputTechnicalSkills, documentSnapshot.getString("technicalSkills"));
                setText(inputCertifications, documentSnapshot.getString("certifications"));
                setText(inputProjects, documentSnapshot.getString("projects"));
                setText(inputInternship, documentSnapshot.getString("internshipExperience"));
                setText(inputResearch, documentSnapshot.getString("researchPaperPublished"));
                setText(inputHackathons, documentSnapshot.getString("hackathonsWin"));
                setText(inputCommunication, documentSnapshot.getString("communication"));
                setText(inputAptitude, documentSnapshot.getString("aptitudeScore"));
                setText(inputProblems, documentSnapshot.getString("problemSolved"));
                setText(inputGender, documentSnapshot.getString("gender"));
                setText(inputAge, documentSnapshot.getString("age"));
                setText(inputLocation, documentSnapshot.getString("location"));

                Toast.makeText(this, "Existing data loaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No previous data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    /** Save student progress details into Firestore **/
    private void saveProgressToFirestore() {
        String degree = getText(inputDegree);
        String department = getText(inputDepartment);
        String year = getText(inputYear);

        if (TextUtils.isEmpty(degree) || TextUtils.isEmpty(department) || TextUtils.isEmpty(year)) {
            Toast.makeText(this, "Please fill Degree, Department, and Year", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> studentData = new HashMap<>();
        studentData.put("degree", degree);
        studentData.put("department", department);
        studentData.put("year", year);
        studentData.put("rollno", getText(inputRollNo));
        studentData.put("CGPA", getText(inputCgpa));
        studentData.put("backlogs", getText(inputBacklogs));
        studentData.put("programmingLanguage", getText(inputProgrammingLanguages));
        studentData.put("technicalSkills", getText(inputTechnicalSkills));
        studentData.put("certifications", getText(inputCertifications));
        studentData.put("projects", getText(inputProjects));
        studentData.put("internshipExperience", getText(inputInternship));
        studentData.put("researchPaperPublished", getText(inputResearch));
        studentData.put("hackathonsWin", getText(inputHackathons));
        studentData.put("communication", getText(inputCommunication));
        studentData.put("aptitudeScore", getText(inputAptitude));
        studentData.put("problemSolved", getText(inputProblems));
        studentData.put("gender", getText(inputGender));
        studentData.put("age", getText(inputAge));
        studentData.put("location", getText(inputLocation));
        studentData.put("lastUpdated", System.currentTimeMillis());

        db.collection("students").document(email)
                .set(studentData, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void setText(TextInputEditText editText, String value) {
        if (value != null) editText.setText(value);
    }
}
