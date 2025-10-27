

package com.hms.database; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.hms.model.Appointment;
import com.hms.model.AvailabilitySlot;
import com.hms.model.Doctor;
import com.hms.model.Patient;
import com.hms.model.User; 

public class DatabaseManager {

    private static final String DATABASE_NAME = "hms.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_NAME;

    public DatabaseManager() {
        createTables();
    }

    /**
     * Establishes a connection to the SQLite database.
     * @return A Connection object if successful, null otherwise.
     */
    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("SQLite JDBC driver not found.");
        }
        return DriverManager.getConnection(DATABASE_URL);
    }

    public boolean saveUser(User user) {
        String SQL_INSERT_USER = "INSERT INTO USERS(userId, username, password, role) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_USER)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().name()); 

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User '" + user.getUsername() + "' saved successfully.");
                return true;
            } else {
                System.err.println("Failed to save user '" + user.getUsername() + "'.");
                return false;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: USERS.username")) {
                System.err.println("Error: Username '" + user.getUsername() + "' already exists.");
            } else {
                System.err.println("Error saving user: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean savePatient(Patient patient) {
        String SQL_INSERT_PATIENT = "INSERT INTO PATIENTS(patientId, name, mobileNumber, email, dateOfBirth, profilePicture, userId) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_PATIENT)) {

            pstmt.setString(1, patient.getPatientId());
            pstmt.setString(2, patient.getName());
            pstmt.setString(3, patient.getMobileNumber());
            pstmt.setString(4, patient.getEmail());
            pstmt.setString(5, patient.getDateOfBirth());
            pstmt.setString(6, patient.getProfilePicture());
            pstmt.setString(7, patient.getUserId()); 

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient '" + patient.getName() + "' saved successfully.");
                return true;
            } else {
                System.err.println("Failed to save patient '" + patient.getName() + "'.");
                return false;
            }
        } catch (SQLException e) {
           
            System.err.println("Error saving patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveDoctor(Doctor doctor) {
        String SQL_INSERT_DOCTOR = "INSERT INTO DOCTORS(doctorId, name, specialty, mobileNumber, email, profilePicture, userId) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_DOCTOR)) {

            pstmt.setString(1, doctor.getDoctorId());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getSpecialty());
            pstmt.setString(4, doctor.getMobileNumber());
            pstmt.setString(5, doctor.getEmail());
            pstmt.setString(6, doctor.getProfilePicture());
            pstmt.setString(7, doctor.getUserId()); 

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Doctor '" + doctor.getName() + "' saved successfully.");
                return true;
            } else {
                System.err.println("Failed to save doctor '" + doctor.getName() + "'.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error saving doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAppointment(Appointment appointment) {
        String SQL_INSERT_APPOINTMENT = "INSERT INTO APPOINTMENTS(appointmentId, patientId, doctorId, date, time, status, slotId) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_APPOINTMENT)) {

            pstmt.setString(1, appointment.getAppointmentId());
            pstmt.setString(2, appointment.getPatientId());
            pstmt.setString(3, appointment.getDoctorId());
            pstmt.setString(4, appointment.getDate());
            pstmt.setString(5, appointment.getTime());
            pstmt.setString(6, appointment.getStatus().name()); 
            pstmt.setString(7, appointment.getSlotId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Appointment " + appointment.getAppointmentId() + " saved successfully.");
                return true;
            } else {
                System.err.println("Failed to save appointment " + appointment.getAppointmentId() + ".");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAvailabilitySlot(AvailabilitySlot slot) {
        String SQL_INSERT_AVAILABILITY_SLOT = "INSERT INTO AVAILABILITY_SLOTS(slotId, doctorId, date, startTime, endTime, isBooked) VALUES(?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_AVAILABILITY_SLOT)) {

            pstmt.setString(1, slot.getSlotId());
            pstmt.setString(2, slot.getDoctorId());
            pstmt.setString(3, slot.getDate());
            pstmt.setString(4, slot.getStartTime());
            pstmt.setString(5, slot.getEndTime());
            pstmt.setInt(6, slot.isBooked() ? 1 : 0); 

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Availability Slot " + slot.getSlotId() + " saved successfully.");
                return true;
            } else {
                System.err.println("Failed to save availability slot " + slot.getSlotId() + ".");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error saving availability slot: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User loadUserByUsername(String username) {
        String SQL_SELECT_USER_BY_USERNAME = "SELECT userId, username, password, role FROM USERS WHERE username = ?";
        User user = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_USER_BY_USERNAME)) {

            pstmt.setString(1, username); 

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    
                    String userId = rs.getString("userId");
                    String retrievedUsername = rs.getString("username");
                    String password = rs.getString("password");
                    String roleString = rs.getString("role");

                    
                    User.UserRole role = User.UserRole.valueOf(roleString);

                    
                    user = new User(userId, retrievedUsername, password, role);
                    System.out.println("User '" + retrievedUsername + "' loaded successfully.");
                } else {
                    System.out.println("User with username '" + username + "' not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    public Patient loadPatientById(String patientId) {
        String SQL_SELECT_PATIENT_BY_ID = "SELECT patientId, name, mobileNumber, email, dateOfBirth, profilePicture, userId FROM PATIENTS WHERE patientId = ?";
        Patient patient = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_PATIENT_BY_ID)) {

            pstmt.setString(1, patientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String retrievedPatientId = rs.getString("patientId");
                    String name = rs.getString("name");
                    String mobileNumber = rs.getString("mobileNumber");
                    String email = rs.getString("email");
                    String dateOfBirth = rs.getString("dateOfBirth");
                    String profilePicture = rs.getString("profilePicture");
                    String userId = rs.getString("userId");

                    patient = new Patient(retrievedPatientId, name, mobileNumber, email, dateOfBirth, profilePicture, userId);
                    System.out.println("Patient '" + name + "' loaded successfully.");
                } else {
                    System.out.println("Patient with ID '" + patientId + "' not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading patient by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return patient;
    }

    public Doctor loadDoctorById(String doctorId) {
        String SQL_SELECT_DOCTOR_BY_ID = "SELECT doctorId, name, specialty, mobileNumber, email, profilePicture, userId FROM DOCTORS WHERE doctorId = ?";
        Doctor doctor = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_DOCTOR_BY_ID)) {

            pstmt.setString(1, doctorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String retrievedDoctorId = rs.getString("doctorId");
                    String name = rs.getString("name");
                    String specialty = rs.getString("specialty");
                    String mobileNumber = rs.getString("mobileNumber");
                    String email = rs.getString("email");
                    String profilePicture = rs.getString("profilePicture");
                    String userId = rs.getString("userId");

                    doctor = new Doctor(retrievedDoctorId, name, specialty, mobileNumber, email, profilePicture, userId);
                    
                } else {
                    System.out.println("Doctor with ID '" + doctorId + "' not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading doctor by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return doctor;
    }

    public boolean updatePatient(Patient patient) {
        String SQL_UPDATE_PATIENT = "UPDATE PATIENTS SET name = ?, mobileNumber = ?, email = ?, dateOfBirth = ?, profilePicture = ? WHERE patientId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_PATIENT)) {

            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getMobileNumber());
            pstmt.setString(3, patient.getEmail());
            pstmt.setString(4, patient.getDateOfBirth());
            pstmt.setString(5, patient.getProfilePicture());
            pstmt.setString(6, patient.getPatientId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient profile for " + patient.getPatientId() + " updated successfully.");
                return true;
            } else {
                System.err.println("Failed to update patient profile. Patient not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating patient profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor) {
        String SQL_UPDATE_DOCTOR = "UPDATE DOCTORS SET name = ?, specialty = ?, mobileNumber = ?, email = ? WHERE doctorId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_DOCTOR)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialty());
            pstmt.setString(3, doctor.getMobileNumber());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setString(5, doctor.getDoctorId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Doctor profile for " + doctor.getName() + " updated successfully.");
                return true;
            } else {
                System.err.println("Failed to update doctor profile. Doctor not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating doctor profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        String SQL_DELETE_USER = "DELETE FROM USERS WHERE userId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_USER)) {

            pstmt.setString(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User with ID " + userId + " deleted successfully.");
                return true;
            } else {
                System.err.println("Failed to delete user with ID " + userId + ". User not found.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<AvailabilitySlot> loadAvailableSlotsByDoctorId(String doctorId) {
        
        List<AvailabilitySlot> slots = new ArrayList<>();
        String SQL_SELECT_SLOTS_BY_DOCTOR_ID = "SELECT slotId, doctorId, date, startTime, endTime, isBooked FROM AVAILABILITY_SLOTS WHERE doctorId = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_SLOTS_BY_DOCTOR_ID)) {

            pstmt.setString(1, doctorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String slotId = rs.getString("slotId");
                    String retrievedDoctorId = rs.getString("doctorId");
                    String date = rs.getString("date");
                    String startTime = rs.getString("startTime");
                    String endTime = rs.getString("endTime");
                    boolean isBooked = rs.getInt("isBooked") == 1; 

                    AvailabilitySlot slot = new AvailabilitySlot(slotId, retrievedDoctorId, date, startTime, endTime, isBooked);
                    slots.add(slot);
                }
                if (slots.isEmpty()) {
                    System.out.println("No availability slots found for Doctor ID '" + doctorId + "'.");
                } else {
                    System.out.println(slots.size() + " availability slot(s) loaded for Doctor ID '" + doctorId + "'.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading availability slots by Doctor ID: " + e.getMessage());
            e.printStackTrace();
        }
        return slots;
    }

    public List<Appointment> loadAppointmentsByPatientId(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String SQL_SELECT_APPOINTMENTS_BY_PATIENT = "SELECT appointmentId, patientId, doctorId, date, time, status FROM APPOINTMENTS WHERE patientId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_APPOINTMENTS_BY_PATIENT)) {

            pstmt.setString(1, patientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String apptId = rs.getString("appointmentId");
                    String retrievedPatientId = rs.getString("patientId");
                    String doctorId = rs.getString("doctorId");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String statusString = rs.getString("status");

                    Appointment.AppointmentStatus status = Appointment.AppointmentStatus.valueOf(statusString);
                    Appointment appointment = new Appointment(apptId, retrievedPatientId, doctorId, date, time, status, null);
                    appointments.add(appointment);
                }
                if (appointments.isEmpty()) {
                    //System.out.println("No appointments found for Patient ID '" + patientId + "'.");
                } else {
                    System.out.println(appointments.size() + " appointment(s) loaded for Patient ID '" + patientId + "'.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading appointments by Patient ID: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Appointment> loadAppointmentsByDoctorId(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String SQL_SELECT_APPOINTMENTS_BY_DOCTOR = "SELECT appointmentId, patientId, doctorId, date, time, status FROM APPOINTMENTS WHERE doctorId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_APPOINTMENTS_BY_DOCTOR)) {

            pstmt.setString(1, doctorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String apptId = rs.getString("appointmentId");
                    String patientId = rs.getString("patientId");
                    String retrievedDoctorId = rs.getString("doctorId");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String statusString = rs.getString("status");

                    Appointment.AppointmentStatus status = Appointment.AppointmentStatus.valueOf(statusString);
                    Appointment appointment = new Appointment(apptId, patientId, retrievedDoctorId, date, time, status, null);
                    appointments.add(appointment);
                }
                if (appointments.isEmpty()) {
                    System.out.println("No appointments found for Doctor ID '" + doctorId + "'.");
                } else {
                    System.out.println(appointments.size() + " appointment(s) loaded for Doctor ID '" + doctorId + "'.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading appointments by Doctor ID: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    public Patient loadPatientByUserId(String userId) {
        String SQL_SELECT_PATIENT_BY_USER_ID = "SELECT patientId, name, mobileNumber, email, dateOfBirth, profilePicture, userId FROM PATIENTS WHERE userId = ?";
        Patient patient = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_PATIENT_BY_USER_ID)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String patientId = rs.getString("patientId");
                    String name = rs.getString("name");
                    String mobileNumber = rs.getString("mobileNumber");
                    String email = rs.getString("email");
                    String dateOfBirth = rs.getString("dateOfBirth");
                    String profilePicture = rs.getString("profilePicture");
                    String retrievedUserId = rs.getString("userId");

                    patient = new Patient(patientId, name, mobileNumber, email, dateOfBirth, profilePicture, retrievedUserId);
                    //System.out.println("Patient profile for User ID '" + userId + "' loaded successfully.");
                } else {
                    System.out.println("Patient profile for User ID '" + userId + "' not found. This user might not be a registered patient.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading patient by User ID: " + e.getMessage());
            e.printStackTrace();
        }
        return patient;
    }

    public Doctor loadDoctorByUserId(String userId) {
        String SQL = "SELECT * FROM DOCTORS WHERE userId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                        rs.getString("doctorId"),
                        rs.getString("name"),
                        rs.getString("specialty"),
                        rs.getString("mobileNumber"),
                        rs.getString("email"),
                        rs.getString("profilePicture"),
                        rs.getString("userId")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading doctor profile by userId: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteDoctorAndUser(String doctorId) {
        String SQL_GET_USER_ID = "SELECT userId FROM DOCTORS WHERE doctorId = ?";
        String SQL_DELETE_DOCTOR = "DELETE FROM DOCTORS WHERE doctorId = ?";
        String SQL_DELETE_USER = "DELETE FROM USERS WHERE userId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmtGetUserId = conn.prepareStatement(SQL_GET_USER_ID);
             PreparedStatement pstmtDeleteDoctor = conn.prepareStatement(SQL_DELETE_DOCTOR);
             PreparedStatement pstmtDeleteUser = conn.prepareStatement(SQL_DELETE_USER)) {

            conn.setAutoCommit(false); // Begin transaction

            // Step 1: Find the userId associated with the doctorId
            pstmtGetUserId.setString(1, doctorId);
            String userId = null;
            try (ResultSet rs = pstmtGetUserId.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getString("userId");
                }
            }
            if (userId == null) {
                System.err.println("Error: Doctor with ID " + doctorId + " not found.");
                conn.rollback();
                return false;
            }

            // Step 2: Delete the doctor's profile
            pstmtDeleteDoctor.setString(1, doctorId);
            int doctorRowsAffected = pstmtDeleteDoctor.executeUpdate();

            // Step 3: Delete the user's account
            pstmtDeleteUser.setString(1, userId);
            int userRowsAffected = pstmtDeleteUser.executeUpdate();

            if (doctorRowsAffected > 0 && userRowsAffected > 0) {
                conn.commit(); // Commit transaction
                System.out.println("Doctor and corresponding user account deleted successfully.");
                return true;
            } else {
                conn.rollback(); // Rollback on failure
                System.err.println("Failed to delete doctor or user account.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting doctor account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Doctor> loadAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String SQL_SELECT_ALL_DOCTORS = "SELECT doctorId, name, specialty, mobileNumber, email, profilePicture, userId FROM DOCTORS";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL_DOCTORS)) {

            while (rs.next()) {
                String doctorId = rs.getString("doctorId");
                String name = rs.getString("name");
                String specialty = rs.getString("specialty");
                String mobileNumber = rs.getString("mobileNumber");
                String email = rs.getString("email");
                String profilePicture = rs.getString("profilePicture");
                String userId = rs.getString("userId");

                Doctor doctor = new Doctor(doctorId, name, specialty, mobileNumber, email, profilePicture, userId);
                doctors.add(doctor);
            }
            if (doctors.isEmpty()) {
                System.out.println("No doctors found in the system.");
            } else {
                System.out.println(doctors.size() + " doctor(s) loaded.");
            }
        } catch (SQLException e) {
            System.err.println("Error loading all doctors: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    public List<Appointment> loadAllAppointments() {
        String SQL = "SELECT * FROM APPOINTMENTS";
        List<Appointment> allAppointments = new ArrayList<>();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getString("appointmentId"),
                    rs.getString("patientId"),
                    rs.getString("doctorId"),
                    rs.getString("date"),
                    rs.getString("time"),
                    Appointment.AppointmentStatus.valueOf(rs.getString("status")),
                    rs.getString("slotId") // This line correctly gets the slotId
                );
                allAppointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error loading all appointments: " + e.getMessage());
        }
        return allAppointments;
    }
    
    public boolean updateAvailabilitySlotStatus(String slotId, boolean isBooked) {
        String SQL_UPDATE_SLOT_STATUS = "UPDATE AVAILABILITY_SLOTS SET isBooked = ? WHERE slotId = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE_SLOT_STATUS)) {

            pstmt.setInt(1, isBooked ? 1 : 0); 
            pstmt.setString(2, slotId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Availability Slot " + slotId + " booking status updated to " + isBooked + ".");
                return true;
            } else {
                System.err.println("Failed to update booking status for slot " + slotId + ". Slot not found?");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating availability slot status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelAppointment(String appointmentId) {
        // SQL to get the slotId before we cancel the appointment
        String SQL_GET_SLOT_ID = "SELECT slotId FROM APPOINTMENTS WHERE appointmentId = ?";

        // SQL to update the appointment status to CANCELED
        String SQL_UPDATE_APPOINTMENT_STATUS = "UPDATE APPOINTMENTS SET status = ? WHERE appointmentId = ?";

        // SQL to update the availability slot status to unbooked
        String SQL_UPDATE_SLOT_STATUS = "UPDATE AVAILABILITY_SLOTS SET isBooked = 0 WHERE slotId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmtGetSlotId = conn.prepareStatement(SQL_GET_SLOT_ID);
             PreparedStatement pstmtUpdateAppt = conn.prepareStatement(SQL_UPDATE_APPOINTMENT_STATUS);
             PreparedStatement pstmtUpdateSlot = conn.prepareStatement(SQL_UPDATE_SLOT_STATUS)) {

            // First, get the slotId from the appointment
            pstmtGetSlotId.setString(1, appointmentId);
            String slotId = null;
            try (ResultSet rs = pstmtGetSlotId.executeQuery()) {
                if (rs.next()) {
                    slotId = rs.getString("slotId");
                }
            }
            if (slotId == null) {
                System.err.println("Error: Appointment not found or slotId is missing.");
                return false;
            }

            // Second, update the appointment status
            pstmtUpdateAppt.setString(1, Appointment.AppointmentStatus.CANCELED.name());
            pstmtUpdateAppt.setString(2, appointmentId);
            int apptRowsAffected = pstmtUpdateAppt.executeUpdate();

            // Third, update the slot status
            pstmtUpdateSlot.setString(1, slotId);
            int slotRowsAffected = pstmtUpdateSlot.executeUpdate();

            if (apptRowsAffected > 0 && slotRowsAffected > 0) {
                System.out.println("Appointment " + appointmentId + " successfully canceled, and slot " + slotId + " is now available.");
                return true;
            } else {
                System.err.println("Failed to cancel appointment. Database update issue.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error canceling appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void createTables() {
        String SQL_CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS USERS ("
                + "userId TEXT PRIMARY KEY,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL"
                + ");";

        String SQL_CREATE_PATIENTS_TABLE = "CREATE TABLE IF NOT EXISTS PATIENTS ("
                + "patientId TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "mobileNumber TEXT,"
                + "email TEXT,"
                + "dateOfBirth TEXT,"
                + "profilePicture TEXT,"
                + "userId TEXT UNIQUE,"
                + "FOREIGN KEY (userId) REFERENCES USERS(userId)"
                + ");";

        String SQL_CREATE_DOCTORS_TABLE = "CREATE TABLE IF NOT EXISTS DOCTORS ("
                + "doctorId TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "specialty TEXT,"
                + "mobileNumber TEXT,"
                + "email TEXT,"
                + "profilePicture TEXT,"
                + "userId TEXT UNIQUE,"
                + "FOREIGN KEY (userId) REFERENCES USERS(userId)"
                + ");";

        String SQL_CREATE_APPOINTMENTS_TABLE = "CREATE TABLE IF NOT EXISTS APPOINTMENTS ("
                + "appointmentId TEXT PRIMARY KEY,"
                + "patientId TEXT NOT NULL,"
                + "doctorId TEXT NOT NULL,"
                + "date TEXT NOT NULL,"
                + "time TEXT NOT NULL,"
                + "status TEXT NOT NULL,"
                + "slotId TEXT NOT NULL,"
                + "FOREIGN KEY (patientId) REFERENCES PATIENTS(patientId),"
                + "FOREIGN KEY (doctorId) REFERENCES DOCTORS(doctorId)"
                + ");";

        String SQL_CREATE_AVAILABILITY_SLOTS_TABLE = "CREATE TABLE IF NOT EXISTS AVAILABILITY_SLOTS ("
                + "slotId TEXT PRIMARY KEY,"
                + "doctorId TEXT NOT NULL,"
                + "date TEXT NOT NULL,"
                + "startTime TEXT NOT NULL,"
                + "endTime TEXT NOT NULL,"
                + "isBooked INTEGER NOT NULL,"
                + "FOREIGN KEY (doctorId) REFERENCES DOCTORS(doctorId)"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_CREATE_USERS_TABLE);
            stmt.execute(SQL_CREATE_PATIENTS_TABLE);
            stmt.execute(SQL_CREATE_DOCTORS_TABLE);
            stmt.execute(SQL_CREATE_APPOINTMENTS_TABLE);
            stmt.execute(SQL_CREATE_AVAILABILITY_SLOTS_TABLE);
            System.out.println("Tables created (or already exist) successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        
        User testDoctorUser = new User("D-testall", "doctor", "doctorpass", User.UserRole.DOCTOR);
        dbManager.saveUser(testDoctorUser);
        Doctor testDoctor = new Doctor("D-testall", "Dr. Test All", "General", "111222333", "test@all.com", "pic.jpg", "D-testall");
        dbManager.saveDoctor(testDoctor);
        User patientUser = new User("P-002", "patient", "patientpass", User.UserRole.PATIENT);
        dbManager.saveUser(patientUser);

        // A corresponding Patient profile
        Patient testPatient = new Patient("P-002", "Test Patient", "1112223333", "patient@email.com", "1990-01-01", "", "P-002");
        dbManager.savePatient(testPatient);
        AvailabilitySlot testSlot = new AvailabilitySlot("SLOT-TEST", "D-testall", "2025-08-11", "09:00", "09:10", false);
        dbManager.saveAvailabilitySlot(testSlot);

        User adminUser = new User("A-001", "admin", "adminpass", User.UserRole.ADMIN);
        dbManager.saveUser(adminUser);

        System.out.println("\n--- Testing loadAllDoctors() ---");
        List<Doctor> allDoctors = dbManager.loadAllDoctors();
        if (!allDoctors.isEmpty()) {
            System.out.println("All Doctors in System:");
            for (Doctor doc : allDoctors) {
                System.out.println("  ID: " + doc.getDoctorId() + ", Name: " + doc.getName() + ", Specialty: " + doc.getSpecialty());
            }
        }
        System.out.println("--- Supporting Methods Test Complete ---");
    }
}