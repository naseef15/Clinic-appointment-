

package com.hms.model;

public class AvailabilitySlot {
    private String slotId;
    private String doctorId;  
    private String date;      
    private String startTime; 
    private String endTime;   
    private boolean isBooked; 


    public AvailabilitySlot(String slotId, String doctorId, String date,
                            String startTime, String endTime, boolean isBooked) {
        this.slotId = slotId;
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

   
    public String getSlotId() {
        return slotId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isBooked() { 
        return isBooked;
    }

    
    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}