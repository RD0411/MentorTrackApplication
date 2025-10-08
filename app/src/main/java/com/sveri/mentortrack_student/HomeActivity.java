package com.sveri.mentortrack_student;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private FirebaseFirestore db;
    private SharedPreferences prefs;
    private String email;

    private TextView userNameTextView;
    private ImageView profileImageView;

    private TextView mentorNameTextView, mentorEmailTextView, mentorDeptTextView, mentorPhoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        email = prefs.getString("email", null);

        if (email == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userName);
        profileImageView = headerView.findViewById(R.id.profileImage);

        mentorNameTextView = findViewById(R.id.mentorName);
        mentorEmailTextView = findViewById(R.id.mentorEmail);
        mentorDeptTextView = findViewById(R.id.mentorDept);
        mentorPhoneTextView = findViewById(R.id.mentorPhone);

        db = FirebaseFirestore.getInstance();
        loadUserInfo();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // stay
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            } else if (id == R.id.nav_placement_and_internship) {
                startActivity(new Intent(getApplicationContext(), PlacementAndInternship.class));
            } else if (id == R.id.nav_change_password) {
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
            } else if (id == R.id.nav_milestones) {
                startActivity(new Intent(getApplicationContext(), StudentProgressActivity.class));
            } else if (id == R.id.nav_placement_eligibility_analysis) {
                startActivity(new Intent(getApplicationContext(), EligibilityAnalysisActivity.class));
            } else if (id == R.id.nav_feedback) {
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
            } else if (id == R.id.nav_logout) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadUserInfo() {
        db.collection("students").document(email).get().addOnSuccessListener(studentSnapshot -> {
            if (studentSnapshot.exists()) {
                String name = studentSnapshot.getString("name");
                String base64Image = studentSnapshot.getString("profileImage");

                if (name != null) {
                    userNameTextView.setText(name);
                }

                if (base64Image != null && !base64Image.isEmpty()) {
                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImageView.setImageBitmap(bitmap);
                }

                String mentorEmail = studentSnapshot.getString("mentoremail");

                if (mentorEmail != null && !mentorEmail.isEmpty()) {
                    db.collection("teachers").document(mentorEmail).get()
                            .addOnSuccessListener(mentorSnapshot -> {
                                if (mentorSnapshot.exists()) {
                                    String mentorName = mentorSnapshot.getString("name");
                                    String mentorDepartment = mentorSnapshot.getString("department");
                                    String mentorPhone = mentorSnapshot.getString("phone");

                                    mentorNameTextView.setText("Mentor Name : " + mentorName);
                                    mentorEmailTextView.setText("Email : " + mentorEmail);
                                    mentorDeptTextView.setText("Department : " + mentorDepartment);
                                    mentorPhoneTextView.setText("Phone No : " + mentorPhone);
                                }
                            });
                }
            }
        });

        List<ExamData> examDataList = new ArrayList<>();

        db.collection("students").document(email)
                .collection("exam")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot examDoc : querySnapshot.getDocuments()) {
                        String examName = examDoc.getId();
                        Map<String, Object> marksMap = examDoc.getData();
                        examDataList.add(new ExamData(examName, marksMap));
                    }

                    ReportPagerAdapter adapter = new ReportPagerAdapter(this, examDataList);
                    ViewPager2 reportPager = findViewById(R.id.reportPager);
                    reportPager.setAdapter(adapter);
                });
    }

    // 🔔 Toolbar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            showUpcomingCompaniesDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 🧩 Show Dialog of Upcoming Companies
    private void showUpcomingCompaniesDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_upcoming_companies, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerCompanies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<CompanyInfo> companyList = new ArrayList<>();
        UpcomingCompanyAdapter adapter = new UpcomingCompanyAdapter(companyList);
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();

        db.collection("upcomingCompanies")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    companyList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CompanyInfo info = new CompanyInfo(
                                doc.getString("companyName"),
                                doc.getString("criteria"),
                                doc.getString("date"),
                                doc.getString("description")
                        );
                        companyList.add(info);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load companies", Toast.LENGTH_SHORT).show();
                });
    }
}
