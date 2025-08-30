package com.securedhealthrecords.controller;

import com.securedhealthrecords.dto.HospitalDTO;
import com.securedhealthrecords.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HospitalController {
    
    private final HospitalService hospitalService;
    
    @GetMapping("/search")
    public ResponseEntity<List<HospitalDTO>> searchHospitals(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city) {
        List<HospitalDTO> hospitals = hospitalService.searchHospitals(name, city);
        return ResponseEntity.ok(hospitals);
    }
    
    @GetMapping
    public ResponseEntity<List<HospitalDTO>> getAllHospitals() {
        List<HospitalDTO> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(hospitals);
    }
}
