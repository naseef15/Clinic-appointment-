package com.clinic.controller;
import org.springframework.web.bind.annotation.*;
import com.clinic.model.Prescription;
import com.clinic.repository.PrescriptionRepository;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    private final PrescriptionRepository repo;
    public PrescriptionController(PrescriptionRepository repo){ this.repo = repo; }

    @PostMapping
    public Prescription create(@RequestBody Prescription p){ return repo.save(p); }

    @GetMapping
    public List<Prescription> list(){ return repo.findAll(); }
}