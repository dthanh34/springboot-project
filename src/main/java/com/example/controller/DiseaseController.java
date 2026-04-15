package com.example.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.entity.Disease;

import com.example.repository.DiseaseRepository;

import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin


public class DiseaseController {
    @Autowired
    private DiseaseRepository diseaseRepository ;
    @GetMapping("/diseases")
    public List<Disease> getAllDiseases(){
        return diseaseRepository.findAll();
    }

}
