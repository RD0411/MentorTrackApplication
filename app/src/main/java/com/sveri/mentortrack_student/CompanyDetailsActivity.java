package com.sveri.mentortrack_student; // change package

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CompanyDetailsActivity extends AppCompatActivity {

    private TextView tvCompanyName, tvAcademicYear, tvType, tvCriteria,
            tvMale, tvFemale, tvBacklogs, tvRounds, tvPackage,
            tvQuestionsAsked, tvSkillsRequired, tvSubjectsCovered, tvSuggestion;

    private FirebaseFirestore db;
    private ImageButton backButton;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        db = FirebaseFirestore.getInstance();

        // Get company ID passed from previous activity
        companyId = getIntent().getStringExtra("companyId");
        if (companyId == null) {
            Toast.makeText(this, "Error: Company ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        backButton.setOnClickListener(v -> onBackPressed());

        fetchCompanyDetails();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvAcademicYear = findViewById(R.id.tvAcademicYear);
        tvType = findViewById(R.id.tvType);
        tvCriteria = findViewById(R.id.tvCriteria);
        tvMale = findViewById(R.id.tvMale);
        tvFemale = findViewById(R.id.tvFemale);
        tvBacklogs = findViewById(R.id.tvBacklogs);
        tvRounds = findViewById(R.id.tvRounds);
        tvPackage = findViewById(R.id.tvPackage);
        tvQuestionsAsked = findViewById(R.id.tvQuestionsAsked);
        tvSkillsRequired = findViewById(R.id.tvSkillsRequired);
        tvSubjectsCovered = findViewById(R.id.tvSubjectsCovered);
        tvSuggestion = findViewById(R.id.tvSuggestion);
    }

    private void fetchCompanyDetails() {
        DocumentReference docRef = db.collection("placementReports").document(companyId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                tvCompanyName.setText(documentSnapshot.getString("companyName"));
                tvAcademicYear.setText("Academic Year: " + documentSnapshot.getString("academicYear"));
                tvType.setText("Type: " + documentSnapshot.getString("type"));
                tvCriteria.setText("Criteria: " + documentSnapshot.getString("criteria") + " CGPA");
                tvMale.setText("Male Candidates Hired: " + documentSnapshot.getLong("male"));
                tvFemale.setText("Female Candidates Hired: " + documentSnapshot.getLong("female"));
                tvBacklogs.setText("Allowed Backlogs: " + documentSnapshot.getLong("no"));
                tvRounds.setText("No. of Rounds: " + documentSnapshot.getLong("noOfRounds"));
                tvPackage.setText("Package: " + documentSnapshot.getLong("package") + " LPA");
                tvQuestionsAsked.setText("Questions Asked: " + documentSnapshot.getLong("questionsAsked"));
                tvSkillsRequired.setText("Skills Required: " + documentSnapshot.getString("skillsRequired"));
                tvSubjectsCovered.setText("Subjects Covered: " + documentSnapshot.getString("subjectsCovered"));
                tvSuggestion.setText("Suggestion: " + documentSnapshot.getString("suggestion"));
            } else {
                Toast.makeText(this, "No data found for this company", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
