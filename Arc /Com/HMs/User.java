

package com.hms.model; 

public class User {
    private String userId;
    private String username;
    private String password;
    private UserRole role; 

    
    public User(String userId, String username, String password, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() { 
        return role;
    }

    
    public void setRole(UserRole role) {
        this.role = role;
    }

    
    public enum UserRole { 
        PATIENT,
        DOCTOR,
        ADMIN
    }
}