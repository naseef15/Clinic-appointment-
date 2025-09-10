package com.clinic.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import com.clinic.repository.AppointmentRepository;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ReminderService {
    private final AppointmentRepository apptRepo;
    private final JavaMailSender mailSender;
    public ReminderService(AppointmentRepository apptRepo, JavaMailSender mailSender){
        this.apptRepo = apptRepo;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0 * * * *")  // runs every hour
    public void sendReminders(){
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime in24 = now.plusHours(24);

        List<com.clinic.model.Appointment> upcoming = apptRepo.findAll().stream()
            .filter(a -> a.getStartAt().isAfter(now) && a.getStartAt().isBefore(in24))
            .toList();

        for(var a : upcoming){
            if(a.getPatient().getEmail() != null){
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(a.getPatient().getEmail());
                msg.setSubject("Appointment Reminder");
                msg.setText("Reminder: Your appointment with Dr. " + a.getDoctor().getName() + " at " + a.getStartAt());
                mailSender.send(msg);
            }
        }
    }
}