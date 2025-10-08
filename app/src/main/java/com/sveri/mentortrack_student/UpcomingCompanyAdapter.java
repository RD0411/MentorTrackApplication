package com.sveri.mentortrack_student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpcomingCompanyAdapter extends RecyclerView.Adapter<UpcomingCompanyAdapter.ViewHolder> {

    private final List<CompanyInfo> companyList;

    public UpcomingCompanyAdapter(List<CompanyInfo> companyList) {
        this.companyList = companyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_company_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompanyInfo company = companyList.get(position);
        holder.companyName.setText(company.companyName);
        holder.criteria.setText("Criteria: " + company.criteria);
        holder.date.setText("Date: " + company.date);
        holder.description.setText(company.description);
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyName, criteria, date, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            criteria = itemView.findViewById(R.id.companyCriteria);
            date = itemView.findViewById(R.id.companyDate);
            description = itemView.findViewById(R.id.companyDescription);
        }
    }
}
