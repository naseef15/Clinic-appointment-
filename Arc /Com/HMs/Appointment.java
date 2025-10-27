

package com.hms.model;

public class Appointment {
    private String appointmentId;
    private String patientId; 
    private String doctorId;  
    private String date;      
    private String time;      
    private AppointmentStatus status; 
    private String slotId;

    
    public Appointment(String appointmentId, String patientId, String doctorId,
                       String date, String time, AppointmentStatus status, String slotId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.slotId = slotId;  
    }

    
    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getSlotId() {
        return slotId;
    }

    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

   
    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED, 
        COMPLETED,
        CANCELED
    }
}