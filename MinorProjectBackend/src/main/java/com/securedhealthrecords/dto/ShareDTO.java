package com.securedhealthrecords.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareDTO {
    
    private String id;
    private String ownerId;
    
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;
    
    @NotEmpty(message = "At least one node must be shared")
    private List<String> nodeIds;
    
    private String accessToken;
    private String accessKey;
    
    @Positive(message = "Duration must be positive")
    private Integer durationHours;
    
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    
    // For response with node details
    private List<NodeDTO> sharedNodes;
}
