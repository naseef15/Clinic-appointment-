package com.clinic.controller;
import org.springframework.web.bind.annotation.*;
import com.clinic.model.Doctor;
import com.clinic.repository.DoctorRepository;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorRepository repo;
    public DoctorController(DoctorRepository repo){ this.repo = repo; }

    @PostMapping
    public Doctor create(@RequestBody Doctor d){ return repo.save(d); }

    @GetMapping
    public List<Doctor> list(){ return repo.findAll(); }
}