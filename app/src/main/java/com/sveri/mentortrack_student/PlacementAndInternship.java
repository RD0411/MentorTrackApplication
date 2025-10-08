package com.sveri.mentortrack_student; // <-- change this to your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sveri.mentortrack_student.CompanyAdapter;
import com.sveri.mentortrack_student.CompanyModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlacementAndInternship extends AppCompatActivity {

    private RecyclerView companyRecyclerView;
    private CompanyAdapter companyAdapter;
    private List<CompanyModel> companyList, filteredList;
    private TextInputEditText searchEditText;
    private FirebaseFirestore db;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_and_internship);

        db = FirebaseFirestore.getInstance();

        companyRecyclerView = findViewById(R.id.companyRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        backButton = findViewById(R.id.backButton);

        companyList = new ArrayList<>();
        filteredList = new ArrayList<>();

        companyAdapter = new CompanyAdapter(this, filteredList, company -> {
            // When user clicks a company → open details activity
            Intent intent = new Intent(PlacementAndInternship.this, CompanyDetailsActivity.class);
            intent.putExtra("companyId", company.getId());
            startActivity(intent);
        });

        companyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyRecyclerView.setAdapter(companyAdapter);

        backButton.setOnClickListener(v -> onBackPressed());

        fetchCompanies();

        // Search filter logic
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCompanies(s.toString());
            }
        });
    }

    private void fetchCompanies() {
        CollectionReference placementRef = db.collection("placementReports");

        placementRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                companyList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CompanyModel company = new CompanyModel();
                    company.setId(document.getId());
                    company.setCompanyName(document.getString("companyName"));
                    company.setType(document.getString("type"));
                    companyList.add(company);
                }

                // Initially show all companies
                filteredList.clear();
                filteredList.addAll(companyList);
                companyAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCompanies(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(companyList);
        } else {
            for (CompanyModel company : companyList) {
                if (company.getCompanyName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(company);
                }
            }
        }
        companyAdapter.notifyDataSetChanged();
    }
}
