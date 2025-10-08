package com.sveri.mentortrack_student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sveri.mentortrack_student.R;
import com.sveri.mentortrack_student.CompanyModel;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    public interface OnCompanyClickListener {
        void onCompanyClick(CompanyModel company);
    }

    private Context context;
    private List<CompanyModel> companyList;
    private OnCompanyClickListener listener;

    public CompanyAdapter(Context context, List<CompanyModel> companyList, OnCompanyClickListener listener) {
        this.context = context;
        this.companyList = companyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_company_card, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        CompanyModel company = companyList.get(position);
        holder.companyNameText.setText(company.getCompanyName());
        holder.companyDomainText.setText(company.getType());
        holder.itemView.setOnClickListener(v -> listener.onCompanyClick(company));
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    static class CompanyViewHolder extends RecyclerView.ViewHolder {
        TextView companyNameText, companyDomainText;
        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            companyNameText = itemView.findViewById(R.id.companyNameText);
            companyDomainText = itemView.findViewById(R.id.companyDomainText);
        }
    }
}
