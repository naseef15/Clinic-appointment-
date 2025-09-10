package com.clinic.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.clinic.model.*;
import com.clinic.repository.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentRepository apptRepo;
    private final DoctorRepository docRepo;
    private final PatientRepository patRepo;

    public AppointmentController(AppointmentRepository apptRepo, DoctorRepository docRepo, PatientRepository patRepo){
        this.apptRepo = apptRepo;
        this.docRepo = docRepo;
        this.patRepo = patRepo;
    }

    @PostMapping
    public Object create(@RequestBody Appointment req){
        Long doctorId = req.getDoctor().getId();
        OffsetDateTime start = req.getStartAt();
        OffsetDateTime end = req.getEndAt();

        var conflicts = apptRepo.findConflicting(doctorId, start, end);
        if(!conflicts.isEmpty())
            return ResponseEntity.status(400).body(Map.of("error","Time slot conflict"));

        req.setDoctor(docRepo.findById(doctorId).orElseThrow());
        req.setPatient(patRepo.findById(req.getPatient().getId()).orElseThrow());
        return apptRepo.save(req);
    }

    @GetMapping
    public List<Appointment> list(){ return apptRepo.findAll(); }

    @GetMapping("/doctor/{id}/schedule")
    public List<Appointment> schedule(@PathVariable Long id, @RequestParam String date){
        OffsetDateTime start = OffsetDateTime.parse(date + "T00:00:00Z");
        OffsetDateTime end = OffsetDateTime.parse(date + "T23:59:59Z");
        return apptRepo.findByDoctorIdAndStartAtBetweenOrderByStartAt(id,start,end);
    }
}