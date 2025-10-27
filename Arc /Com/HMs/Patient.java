package com.hms.model;

public class Patient {
    private String patientId;
    private String name;
    private String mobileNumber;
    private String email;
    private String dateOfBirth; 
    private String profilePicture;
    private String userId; 

    
    public Patient(String patientId, String name, String mobileNumber,
                   String email, String dateOfBirth, String profilePicture, String userId) {
        this.patientId = patientId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.profilePicture = profilePicture;
        this.userId = userId;
    }

    
    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getUserId() {
        return userId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    


    
}