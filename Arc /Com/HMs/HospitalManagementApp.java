package com.hms.app;

import com.hms.database.DatabaseManager;
import com.hms.model.User;
import com.hms.model.Appointment;
import com.hms.model.AvailabilitySlot;
import com.hms.model.Doctor;
import com.hms.model.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID; 

public class HospitalManagementApp {

    private DatabaseManager dbManager;
    private Scanner scanner;
    private User loggedInUser;

    public HospitalManagementApp() {
        dbManager = new DatabaseManager(); 
        scanner = new Scanner(System.in); 
        dbManager.createTables();
    }

    public void start() {
        System.out.println("--------------------------------------------------");
        System.out.println("|       Hospital Management System (HMS)         |");
        System.out.println("--------------------------------------------------");

        int choice;
        do {
            displayMainMenu();
            choice = getUserChoice();

            switch (choice) {
                case 1:
                    loginUser();
                    if (loggedInUser != null) {
                        showUserDashboard();
                    }
                    break;
                case 2:
                    registerPatientAccount();
                    break;
                case 3:
                    System.out.println("Exiting HMS. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
        
    }
    

    private void displayMainMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("|                                                |");
        System.out.println("|   1. Login                                     |");
        System.out.println("|   2. Register New Patient Account              |");
        System.out.println("|   3. Exit                                      |");
        System.out.println("|                                                |");
        System.out.println("--------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); 
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    private void loginUser() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = dbManager.loadUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            System.out.println("Login successful! Welcome, " + user.getUsername() + "!");
            System.out.println("You are logged in as a " + user.getRole() + ".");
        } else {
            System.out.println("Login failed. Invalid username or password.");
            System.out.println("Please try again or register a new account.");
        }
    }

    private void registerPatientAccount() {
        System.out.println("\n--- Register New Patient Account ---");
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your mobile number (e.g., 1234567890): ");
        String mobileNumber = scanner.nextLine();
        System.out.print("Enter your email address: ");
        String email = scanner.nextLine();
        System.out.print("Enter your Date of Birth (YYYY-MM-DD): ");
        String dateOfBirth = scanner.nextLine();
        System.out.print("Enter a profile picture URL or path (optional, press Enter to skip): ");
        String profilePicture = scanner.nextLine(); // Can be empty string

        System.out.print("Choose a username: ");
        String username = scanner.nextLine();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine();

        
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || mobileNumber.isEmpty() || email.isEmpty() || dateOfBirth.isEmpty()) {
            System.out.println("Registration failed. Please fill in all required fields.");
            return;
        }

        
        String userId = "U-" + UUID.randomUUID().toString().substring(0, 8);
        String patientId = "P-" + UUID.randomUUID().toString().substring(0, 8); // Often patientId == userId for 1:1

        
        User newUser = new User(userId, username, password, User.UserRole.PATIENT);
        boolean userSaved = dbManager.saveUser(newUser);

        if (userSaved) {
            
            Patient newPatient = new Patient(patientId, name, mobileNumber, email, dateOfBirth, profilePicture, userId);
            boolean patientSaved = dbManager.savePatient(newPatient);

            if (patientSaved) {
                System.out.println("Account created successfully! Welcome, " + name + "!");
                System.out.println("Please login using your new username and password.");
            } else {
               
                System.out.println("Registration failed: Could not save patient details.");
            }
        } else {
            
            System.out.println("Registration failed. Please try again with a different username.");
        }
    }

    private void viewPatientAppointments() {
        System.out.println("\n--- My Appointments ---");
        
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.PATIENT) {
            System.out.println("Error: No patient logged in or incorrect role.");
            return;
        }
        Patient currentPatient = dbManager.loadPatientByUserId(loggedInUser.getUserId());

        if (currentPatient == null) {
            System.out.println("Error: Patient profile not found for your user account. Please ensure your patient details are registered.");
            return;
        }

        List<Appointment> patientAppointments = dbManager.loadAppointmentsByPatientId(currentPatient.getPatientId());

        if (patientAppointments.isEmpty()) {
            System.out.println("You have no appointments scheduled.");
        } else {
            System.out.println("Here are your appointments:");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("%-10s %-25s %-12s %-10s %-15s %-10s\n",
                              "Appt ID", "Doctor Name", "Date", "Time", "Status", "Patient ID");
            System.out.println("----------------------------------------------------------------------------------");
            for (Appointment appt : patientAppointments) {
                Doctor doctor = dbManager.loadDoctorById(appt.getDoctorId());
                String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
                System.out.printf("%-10s %-25s %-12s %-10s %-15s %-10s\n",
                                  appt.getAppointmentId(),
                                  doctorName,
                                  appt.getDate(),
                                  appt.getTime(),
                                  appt.getStatus().name(),
                                  appt.getPatientId());
            }
            System.out.println("----------------------------------------------------------------------------------");
        }
    }

    private void bookAppointment() {
        System.out.println("\n--- Book New Appointment ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.PATIENT) {
            System.out.println("Error: Only patients can book appointments. Please login as a patient.");
            return;
        }

        Patient currentPatient = dbManager.loadPatientByUserId(loggedInUser.getUserId());
        if (currentPatient == null) {
            System.out.println("Error: Patient profile not found for your user account. Cannot book appointment.");
            return;
        }


        List<Doctor> doctors = dbManager.loadAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors available in the system to book an appointment with.");
            return;
        }

        System.out.println("\n--- Available Doctors ---");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-5s %-10s %-25s %-15s %-10s\n", "No.", "ID", "Name", "Specialty", "Mobile"); // Added No. header
        System.out.println("----------------------------------------------------------------------------------");
        int doctorNumber = 1;
        for (Doctor doc : doctors) {
            System.out.printf("%-5d %-10s %-25s %-15s %-10s\n",
                              doctorNumber++, doc.getDoctorId(), doc.getName(), doc.getSpecialty(), doc.getMobileNumber());
        }
        System.out.println("----------------------------------------------------------------------------------");

        int chosenDoctorNumber;
        Doctor selectedDoctor = null;
        do {
            System.out.print("Select a doctor by number: ");
            chosenDoctorNumber = getUserChoice(); 
            if (chosenDoctorNumber >= 1 && chosenDoctorNumber <= doctors.size()) {
                selectedDoctor = doctors.get(chosenDoctorNumber - 1); 
            } else {
                System.out.println("Invalid number. Please enter a number from the list.");
            }
        } while (selectedDoctor == null);

        System.out.println("You selected: " + selectedDoctor.getName() + " (" + selectedDoctor.getSpecialty() + ")");


       
        List<AvailabilitySlot> doctorSlots = dbManager.loadAvailableSlotsByDoctorId(selectedDoctor.getDoctorId());
        List<AvailabilitySlot> availableSlots = new ArrayList<>(); 

        for (AvailabilitySlot slot : doctorSlots) {
            if (!slot.isBooked()) { 
                availableSlots.add(slot);
            }
        }

        if (availableSlots.isEmpty()) {
            System.out.println("No available slots for this doctor at the moment.");
            return;
        }

        System.out.println("\n--- Available Slots for " + selectedDoctor.getName() + " ---");
        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-5s %-12s %-8s %-8s %-10s\n", "No.", "Date", "Start", "End", "Slot ID"); // Added Slot ID for reference
        System.out.println("------------------------------------------------------------------");
        int slotNumber = 1;
        for (AvailabilitySlot slot : availableSlots) {
            System.out.printf("%-5d %-12s %-8s %-8s %-10s\n",
                              slotNumber++, slot.getDate(), slot.getStartTime(), slot.getEndTime(), slot.getSlotId());
        }
        System.out.println("------------------------------------------------------------------");

        int chosenSlotNumber;
        AvailabilitySlot chosenSlot = null;
        do {
            System.out.print("Select a slot by number: ");
            chosenSlotNumber = getUserChoice(); // Re-use getUserChoice
            if (chosenSlotNumber >= 1 && chosenSlotNumber <= availableSlots.size()) {
                chosenSlot = availableSlots.get(chosenSlotNumber - 1); // Get slot by index (0-based)
            } else {
                System.out.println("Invalid number. Please enter a number from the list.");
            }
        } while (chosenSlot == null);


        
        System.out.println("\n--- Confirm Your Appointment ---");
        System.out.println("Patient:     " + currentPatient.getName());
        System.out.println("Doctor:      " + selectedDoctor.getName() + " (" + selectedDoctor.getSpecialty() + ")");
        System.out.println("Date:        " + chosenSlot.getDate());
        System.out.println("Time:        " + chosenSlot.getStartTime() + " - " + chosenSlot.getEndTime());
        System.out.print("Confirm booking? (yes/no): ");
        String confirmation = scanner.nextLine(); 

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Appointment booking cancelled.");
            return;
        }


        
        String appointmentId = "APP-" + UUID.randomUUID().toString().substring(0, 8); // Generate unique appointment ID
        Appointment newAppointment = new Appointment(
            appointmentId,
            currentPatient.getPatientId(),
            selectedDoctor.getDoctorId(),
            chosenSlot.getDate(),
            chosenSlot.getStartTime(),
            Appointment.AppointmentStatus.SCHEDULED,
            chosenSlot.getSlotId()
        );

        boolean apptSaved = dbManager.saveAppointment(newAppointment);

        if (apptSaved) {
            boolean slotUpdated = dbManager.updateAvailabilitySlotStatus(chosenSlot.getSlotId(), true); // Mark as booked
            if (slotUpdated) {
                System.out.println("\nAppointment successfully booked!");
                System.out.println("Appointment ID: " + appointmentId);
            } else {
                System.err.println("Appointment booked, but failed to update slot status. Please contact support.");
            }
        } else {
            System.err.println("Failed to book appointment. Please try again.");
        }
    }

    private void cancelPatientAppointment() {
        System.out.println("\n--- Cancel My Appointment ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.PATIENT) {
            System.out.println("Error: Only patients can cancel appointments. Please login as a patient.");
            return;
        }

        Patient currentPatient = dbManager.loadPatientByUserId(loggedInUser.getUserId());
        if (currentPatient == null) {
            System.out.println("Error: Patient profile not found. Cannot cancel appointments.");
            return;
        }

        // Display existing appointments so the user knows what to cancel
        List<Appointment> patientAppointments = dbManager.loadAppointmentsByPatientId(currentPatient.getPatientId());
        if (patientAppointments.isEmpty()) {
            System.out.println("You have no appointments to cancel.");
            return;
        }

        System.out.println("Your current appointments:");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-10s %-25s %-12s %-10s %-15s\n",
                          "No.", "ID", "Doctor Name", "Date", "Time", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------");
        int appointmentNumber = 1;
        for (Appointment appt : patientAppointments) {
            Doctor doctor = dbManager.loadDoctorById(appt.getDoctorId());
            String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";
            System.out.printf("%-5d %-10s %-25s %-12s %-10s %-15s\n",
                              appointmentNumber++, appt.getAppointmentId(), doctorName, appt.getDate(), appt.getTime(), appt.getStatus().name());
        }
        System.out.println("--------------------------------------------------------------------------------------------------");

        int chosenAppointmentNumber;
        Appointment chosenAppointment = null;
        String apptIdToCancel;

        do {
            System.out.print("Select the appointment to cancel by number: ");
            chosenAppointmentNumber = getUserChoice(); // Re-use getUserChoice for integer input
            if (chosenAppointmentNumber >= 1 && chosenAppointmentNumber <= patientAppointments.size()) {
                chosenAppointment = patientAppointments.get(chosenAppointmentNumber - 1); // Get appointment by index
                apptIdToCancel = chosenAppointment.getAppointmentId();
                break;
            } else {
                System.out.println("Invalid number. Please enter a number from the list.");
                apptIdToCancel = null;
            }
        } while (chosenAppointment == null);

        System.out.print("Are you sure you want to cancel appointment " + apptIdToCancel + "? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Cancellation request aborted.");
            return;
        }

        boolean success = dbManager.cancelAppointment(apptIdToCancel);
        if (success) {
            System.out.println("Appointment " + apptIdToCancel + " has been successfully canceled.");
        } else {
            System.out.println("Failed to cancel appointment. Please try again.");
        }
    }

    private void setDoctorAvailability() {
        System.out.println("\n--- Set My Availability ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.DOCTOR) {
            System.out.println("Error: Only doctors can set availability. Please log in as a doctor.");
            return;
        }

        // Get the doctor ID for the logged-in user
        Doctor currentDoctor = dbManager.loadDoctorByUserId(loggedInUser.getUserId());
        if (currentDoctor == null) {
            System.out.println("Error: Doctor profile not found for your user account. Cannot set availability.");
            return;
        }

        System.out.print("Enter date for the slot (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter start time (HH:MM): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter end time (HH:MM): ");
        String endTime = scanner.nextLine();

        // Basic validation (you'll want to improve this later)
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}") || !startTime.matches("\\d{2}:\\d{2}") || !endTime.matches("\\d{2}:\\d{2}")) {
            System.out.println("Error: Invalid date or time format. Please use YYYY-MM-DD and HH:MM.");
            return; // Exit the method if validation fails
        }

        // Future validation for 10-minute slots would go here.

        String slotId = "SLOT-" + UUID.randomUUID().toString().substring(0, 8); // Generate unique ID
        boolean isBooked = false; // A newly created slot is always unbooked

        AvailabilitySlot newSlot = new AvailabilitySlot(slotId, currentDoctor.getDoctorId(), date, startTime, endTime, isBooked);

        boolean saved = dbManager.saveAvailabilitySlot(newSlot);
        if (saved) {
            System.out.println("Availability slot successfully added for " + currentDoctor.getName() + " on " + date + " from " + startTime + " to " + endTime + ".");
        } else {
            System.out.println("Failed to add availability slot. Please try again.");
        }
    }

    private void viewDoctorAppointments() {
        System.out.println("\n--- My Appointments ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.DOCTOR) {
            System.out.println("Error: No doctor logged in or incorrect role.");
            return;
        }

        // Get the doctor ID for the logged-in user
        Doctor currentDoctor = dbManager.loadDoctorByUserId(loggedInUser.getUserId());
        if (currentDoctor == null) {
            System.out.println("Error: Doctor profile not found for your user account.");
            return;
        }

        List<Appointment> doctorAppointments = dbManager.loadAppointmentsByDoctorId(currentDoctor.getDoctorId());

        if (doctorAppointments.isEmpty()) {
            System.out.println("You have no appointments scheduled.");
        } else {
            System.out.println("Here are your appointments:");
            System.out.println("--------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-25s %-12s %-10s %-15s %-10s\n",
                              "Appt ID", "Patient Name", "Date", "Time", "Status", "Doctor ID");
            System.out.println("--------------------------------------------------------------------------------------------------");
            for (Appointment appt : doctorAppointments) {
                // Load the Patient object to get the name
                Patient patient = dbManager.loadPatientById(appt.getPatientId());
                String patientName = (patient != null) ? patient.getName() : "Unknown Patient";

                System.out.printf("%-10s %-25s %-12s %-10s %-15s %-10s\n",
                                  appt.getAppointmentId(),
                                  patientName, // Use patient's name here
                                  appt.getDate(),
                                  appt.getTime(),
                                  appt.getStatus().name(),
                                  appt.getDoctorId());
            }
            System.out.println("--------------------------------------------------------------------------------------------------");
        }
    }

    private void editPatientProfile() {
        System.out.println("\n--- Edit My Profile ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.PATIENT) {
            System.out.println("Error: Only patients can edit their profile.");
            return;
        }

        Patient currentPatient = dbManager.loadPatientByUserId(loggedInUser.getUserId());
        if (currentPatient == null) {
            System.out.println("Error: Patient profile not found for your user account.");
            return;
        }

        System.out.println("Current Profile Details:");
        System.out.println("Full Name: " + currentPatient.getName());
        System.out.println("Mobile: " + currentPatient.getMobileNumber());
        System.out.println("Email: " + currentPatient.getEmail());
        System.out.println("Date of Birth: " + currentPatient.getDateOfBirth());

        System.out.println("\nEnter new details (press Enter to keep current value):");
        System.out.print("New Full Name: ");
        String newName = scanner.nextLine();
        System.out.print("New Mobile Number: ");
        String newMobile = scanner.nextLine();
        System.out.print("New Email Address: ");
        String newEmail = scanner.nextLine();
        System.out.print("New Date of Birth (YYYY-MM-DD): ");
        String newDob = scanner.nextLine();
        
        // Update the patient object with new values if provided
        if (!newName.isEmpty()) currentPatient.setName(newName);
        if (!newMobile.isEmpty()) currentPatient.setMobileNumber(newMobile);
        if (!newEmail.isEmpty()) currentPatient.setEmail(newEmail);
        if (!newDob.isEmpty()) currentPatient.setDateOfBirth(newDob);

        boolean success = dbManager.updatePatient(currentPatient);
        if (success) {
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Failed to update profile. Please try again.");
        }
    }

    private void editDoctorProfile() {
        System.out.println("\n--- Edit My Profile ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.DOCTOR) {
            System.out.println("Error: Only doctors can edit their profile.");
            return;
        }

        Doctor currentDoctor = dbManager.loadDoctorByUserId(loggedInUser.getUserId());
        if (currentDoctor == null) {
            System.out.println("Error: Doctor profile not found for your user account.");
            return;
        }

        System.out.println("Current Profile Details:");
        System.out.println("Full Name: " + currentDoctor.getName());
        System.out.println("Specialty: " + currentDoctor.getSpecialty());
        System.out.println("Mobile: " + currentDoctor.getMobileNumber());
        System.out.println("Email: " + currentDoctor.getEmail());

        System.out.println("\nEnter new details (press Enter to keep current value):");
        System.out.print("New Full Name: ");
        String newName = scanner.nextLine();
        System.out.print("New Specialty: ");
        String newSpecialty = scanner.nextLine();
        System.out.print("New Mobile Number: ");
        String newMobile = scanner.nextLine();
        System.out.print("New Email Address: ");
        String newEmail = scanner.nextLine();

        // Update the doctor object with new values if provided
        if (!newName.isEmpty()) currentDoctor.setName(newName);
        if (!newSpecialty.isEmpty()) currentDoctor.setSpecialty(newSpecialty);
        if (!newMobile.isEmpty()) currentDoctor.setMobileNumber(newMobile);
        if (!newEmail.isEmpty()) currentDoctor.setEmail(newEmail);

        boolean success = dbManager.updateDoctor(currentDoctor);
        if (success) {
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Failed to update profile. Please try again.");
        }
    }

    private void doctorCheckIn() {
        System.out.println("\n--- Doctor Check-in ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.DOCTOR) {
            System.out.println("Error: Only doctors can check in. Please log in as a doctor.");
            return;
        }
        
        System.out.println("You have successfully checked in for your shift, Dr. " + loggedInUser.getUsername() + ".");
        // In a more complex system, this would update a status in the DB or a log file.
    }

    private void addDoctorAccount() {
        System.out.println("\n--- Add New Doctor Account ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.ADMIN) {
            System.out.println("Error: Only administrators can add doctor accounts.");
            return;
        }

        System.out.print("Enter doctor's full name: ");
        String name = scanner.nextLine();
        System.out.print("Enter doctor's specialty: ");
        String specialty = scanner.nextLine();
        System.out.print("Enter doctor's mobile number: ");
        String mobileNumber = scanner.nextLine();
        System.out.print("Enter doctor's email address: ");
        String email = scanner.nextLine();

        System.out.print("Choose a username for the doctor: ");
        String username = scanner.nextLine();
        System.out.print("Choose a password for the doctor: ");
        String password = scanner.nextLine();
        
        // Basic input validation
        if (name.isEmpty() || specialty.isEmpty() || mobileNumber.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Registration failed. Please fill in all required fields.");
            return;
        }

        // Generate unique IDs for user and doctor
        String userId = "U-" + UUID.randomUUID().toString().substring(0, 8);
        String doctorId = "D-" + UUID.randomUUID().toString().substring(0, 8);

        // Create and save a new User with the DOCTOR role
        User newUser = new User(userId, username, password, User.UserRole.DOCTOR);
        boolean userSaved = dbManager.saveUser(newUser);

        if (userSaved) {
            // Create and save the corresponding Doctor profile
            Doctor newDoctor = new Doctor(doctorId, name, specialty, mobileNumber, email,"", userId);
            boolean doctorSaved = dbManager.saveDoctor(newDoctor);

            if (doctorSaved) {
                System.out.println("New doctor account for " + name + " created successfully!");
            } else {
                System.err.println("Failed to save doctor details. User account was created, but doctor profile failed.");
                dbManager.deleteUser(userId); // Rollback user creation
            }
        } else {
            System.err.println("Failed to save user account. This might be due to a duplicate username.");
        }
    }

    private void cancelDoctorAppointment() {
        System.out.println("\n--- Cancel an Appointment ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.DOCTOR) {
            System.out.println("Error: Only doctors can cancel their appointments. Please log in as a doctor.");
            return;
        }

        Doctor currentDoctor = dbManager.loadDoctorByUserId(loggedInUser.getUserId());
        if (currentDoctor == null) {
            System.out.println("Error: Doctor profile not found. Cannot cancel appointments.");
            return;
        }

        // Display existing appointments so the doctor knows what to cancel
        List<Appointment> doctorAppointments = dbManager.loadAppointmentsByDoctorId(currentDoctor.getDoctorId());
        if (doctorAppointments.isEmpty()) {
            System.out.println("You have no appointments to cancel.");
            return;
        }

        System.out.println("Your current appointments:");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-10s %-25s %-12s %-10s %-15s\n",
                          "No.", "ID", "Patient Name", "Date", "Time", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------");
        int appointmentNumber = 1;
        for (Appointment appt : doctorAppointments) {
            Patient patient = dbManager.loadPatientById(appt.getPatientId());
            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            System.out.printf("%-5d %-10s %-25s %-12s %-10s %-15s\n",
                              appointmentNumber++, appt.getAppointmentId(), patientName, appt.getDate(), appt.getTime(), appt.getStatus().name());
        }
        System.out.println("--------------------------------------------------------------------------------------------------");

        int chosenAppointmentNumber;
        Appointment chosenAppointment = null;
        String apptIdToCancel;

        do {
            System.out.print("Select the appointment to cancel by number: ");
            chosenAppointmentNumber = getUserChoice();
            if (chosenAppointmentNumber >= 1 && chosenAppointmentNumber <= doctorAppointments.size()) {
                chosenAppointment = doctorAppointments.get(chosenAppointmentNumber - 1);
                apptIdToCancel = chosenAppointment.getAppointmentId();
                break;
            } else {
                System.out.println("Invalid number. Please enter a number from the list.");
                apptIdToCancel = null;
            }
        } while (chosenAppointment == null);

        // Confirmation to prevent accidental cancellation
        System.out.print("Are you sure you want to cancel appointment " + apptIdToCancel + "? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Cancellation request aborted.");
            return;
        }

        // Call the DatabaseManager method to perform the cancellation
        boolean success = dbManager.cancelAppointment(apptIdToCancel);
        if (success) {
            System.out.println("Appointment " + apptIdToCancel + " has been successfully canceled.");
        } else {
            System.out.println("Failed to cancel appointment. Please check the ID and try again.");
        }
    }

    private void removeDoctorAccount() {
        System.out.println("\n--- Remove Doctor Account ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.ADMIN) {
            System.out.println("Error: Only administrators can remove doctor accounts.");
            return;
        }

        List<Doctor> doctors = dbManager.loadAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors in the system to remove.");
            return;
        }

        System.out.println("\n--- List of Doctors ---");
        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-5s %-10s %-25s %-15s\n", "No.", "ID", "Name", "Specialty");
        System.out.println("------------------------------------------------------------------");
        int doctorNumber = 1;
        for (Doctor doc : doctors) {
            System.out.printf("%-5d %-10s %-25s %-15s\n",
                              doctorNumber++, doc.getDoctorId(), doc.getName(), doc.getSpecialty());
        }
        System.out.println("------------------------------------------------------------------");

        int chosenDoctorNumber;
        Doctor selectedDoctor = null;
        do {
            System.out.print("Select a doctor to remove by number: ");
            chosenDoctorNumber = getUserChoice();
            if (chosenDoctorNumber >= 1 && chosenDoctorNumber <= doctors.size()) {
                selectedDoctor = doctors.get(chosenDoctorNumber - 1);
            } else {
                System.out.println("Invalid number. Please enter a number from the list.");
            }
        } while (selectedDoctor == null);

        System.out.print("Are you sure you want to remove " + selectedDoctor.getName() + "? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Removal aborted.");
            return;
        }

        boolean success = dbManager.deleteDoctorAndUser(selectedDoctor.getDoctorId());
        if (success) {
            System.out.println("Doctor account removed successfully.");
        } else {
            System.out.println("Failed to remove doctor account. Please try again.");
        }
    }

    private void showUserDashboard() {
        System.out.println("\n--- " + loggedInUser.getRole() + " Dashboard ---");
        System.out.println("Welcome, " + loggedInUser.getUsername() + "!");

        switch (loggedInUser.getRole()) {
            case PATIENT:
                displayPatientMenu();
                break;
            case DOCTOR:
                displayDoctorMenu();
                break;
            case ADMIN:
                displayAdminMenu();
                break;
            default:
                System.out.println("Unknown role. Please contact support.");
        }
    }

    private void displayPatientMenu() {
        int choice;
        do {
            System.out.println("\n--- Patient Menu ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. View My Appointments");
            System.out.println("3. Cancel My Appointment");
            System.out.println("4. Edit My Profile");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            choice = getUserChoice(); 

            switch (choice) {
                case 1:
                    bookAppointment();
                    break;
                case 2:
                    viewPatientAppointments();
                    break;
                case 3:
                    cancelPatientAppointment();
                    break;
                case 4:
                    editPatientProfile();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    loggedInUser = null; // Clear logged-in user
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5 && loggedInUser != null); // Loop until logout

       
    }

    private void displayDoctorMenu() {
        int choice;
        do {
            System.out.println("\n--- Doctor Menu ---");
            System.out.println("1. Doctor Check-in");
            System.out.println("2. Set My Availability");
            System.out.println("3. View My Appointments");
            System.out.println("4. Cancel Appointment"); // Doctor can cancel any of their appointments
            System.out.println("5. Edit My Profile");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            choice = getUserChoice();

            switch (choice) {
                case 1:
                    doctorCheckIn();
                    break;
                case 2:
                    setDoctorAvailability();
                    break;
                case 3:
                    viewDoctorAppointments();
                    break;
                case 4:
                    cancelDoctorAppointment();
                    break;
                case 5:
                    editDoctorProfile();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    loggedInUser = null;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6 && loggedInUser != null);
    }

    private void viewAllAppointmentsReport() {
        System.out.println("\n--- All Appointments Report ---");
        if (loggedInUser == null || loggedInUser.getRole() != User.UserRole.ADMIN) {
            System.out.println("Error: Only administrators can view this report.");
            return;
        }

        List<Appointment> allAppointments = dbManager.loadAllAppointments();

        if (allAppointments.isEmpty()) {
            System.out.println("No appointments found in the system.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-25s %-25s %-12s %-10s %-15s\n",
                          "Appt ID", "Patient Name", "Doctor Name", "Date", "Time", "Status");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (Appointment appt : allAppointments) {
            // Load Patient and Doctor to get their names for the report
            Patient patient = dbManager.loadPatientById(appt.getPatientId());
            Doctor doctor = dbManager.loadDoctorById(appt.getDoctorId());

            String patientName = (patient != null) ? patient.getName() : "Unknown Patient";
            String doctorName = (doctor != null) ? doctor.getName() : "Unknown Doctor";

            System.out.printf("%-10s %-25s %-25s %-12s %-10s %-15s\n",
                              appt.getAppointmentId(),
                              patientName,
                              doctorName,
                              appt.getDate(),
                              appt.getTime(),
                              appt.getStatus().name());
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    private void displayAdminMenu() {
        int choice;
        do {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Doctor Account");
            System.out.println("2. Remove Doctor Account");
            System.out.println("3. View All Appointments Report");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            choice = getUserChoice();

            switch (choice) {
                case 1:
                    addDoctorAccount();
                    break;
                case 2:
                    removeDoctorAccount();
                    break;
                case 3:
                    viewAllAppointmentsReport();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    loggedInUser = null;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4 && loggedInUser != null);
    }
    public static void main(String[] args) {
        HospitalManagementApp app = new HospitalManagementApp();
        app.start();
    }
}