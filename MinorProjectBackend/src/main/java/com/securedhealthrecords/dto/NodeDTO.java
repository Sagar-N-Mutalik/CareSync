package com.securedhealthrecords.dto;

import com.securedhealthrecords.model.Node.NodeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeDTO {
    
    private String id;
    private String ownerId;
    private String parentId;
    private NodeType type;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    // File-specific fields
    private String mimeType;
    private String storageKey;
    private String encryptedFileKey;
    private String downloadUrl; // Pre-signed URL for file download
    
    private LocalDateTime createdAt;
}
