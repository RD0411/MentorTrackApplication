package com.sveri.mentortrack_student;

public class CompanyModel {
    private String id;
    private String companyName;
    private String type;

    public CompanyModel() {}

    public CompanyModel(String id, String companyName, String type) {
        this.id = id;
        this.companyName = companyName;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
