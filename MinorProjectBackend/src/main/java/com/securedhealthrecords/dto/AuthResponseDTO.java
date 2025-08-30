package com.securedhealthrecords.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    
    private String token;
    private String type = "Bearer";
    private String userId;
    private String email;
    private String fullName;
    private String message;
    
    public AuthResponseDTO(String token, String userId, String email, String fullName) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }
    
    public AuthResponseDTO(String message) {
        this.message = message;
    }
}
