package com.clinic.controller;
import org.springframework.web.bind.annotation.*;
import com.clinic.model.Patient;
import com.clinic.repository.PatientRepository;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientRepository repo;
    public PatientController(PatientRepository repo){ this.repo = repo; }

    @PostMapping
    public Patient create(@RequestBody Patient p){ return repo.save(p); }

    @GetMapping
    public List<Patient> list(){ return repo.findAll(); }
}