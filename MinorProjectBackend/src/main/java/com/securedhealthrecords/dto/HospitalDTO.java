package com.securedhealthrecords.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    
    private String id;
    
    @NotBlank(message = "Hospital name is required")
    private String name;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String address;
    private String phone;
}
