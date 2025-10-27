

package com.hms.model;

public class Doctor {
    private String doctorId;
    private String name;
    private String specialty;
    private String mobileNumber;
    private String email;
    private String profilePicture;
    private String userId; 

    
    public Doctor(String doctorId, String name, String specialty,
                  String mobileNumber, String email, String profilePicture, String userId) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialty = specialty;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.profilePicture = profilePicture;
        this.userId = userId;
    }

  
    public String getDoctorId() {
        return doctorId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
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

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }  
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }   
    public void setEmail(String email) {
        this.email = email;
    }
}