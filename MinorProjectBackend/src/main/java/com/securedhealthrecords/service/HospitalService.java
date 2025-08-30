package com.securedhealthrecords.service;

import com.securedhealthrecords.dto.HospitalDTO;
import com.securedhealthrecords.model.Hospital;
import com.securedhealthrecords.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;
    
    public List<HospitalDTO> searchHospitals(String name, String city) {
        List<Hospital> hospitals;
        
        if (name != null && city != null) {
            hospitals = hospitalRepository.findByNameOrCity(name, city);
        } else if (name != null) {
            hospitals = hospitalRepository.findByNameContainingIgnoreCase(name);
        } else if (city != null) {
            hospitals = hospitalRepository.findByCityIgnoreCase(city);
        } else {
            hospitals = hospitalRepository.findAll();
        }
        
        return hospitals.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<HospitalDTO> getAllHospitals() {
        return hospitalRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private HospitalDTO convertToDTO(Hospital hospital) {
        return new HospitalDTO(
            hospital.getId(),
            hospital.getName(),
            hospital.getCity(),
            hospital.getAddress(),
            hospital.getPhone()
        );
    }
}
