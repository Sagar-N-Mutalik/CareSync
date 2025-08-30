package com.securedhealthrecords.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shares")
public class Share {
    
    @Id
    private String id;
    
    @Indexed
    private String ownerId; // Reference to User._id who created the share
    
    private String recipientEmail; // Email of the recipient
    
    private List<String> nodeIds; // List of Node._id being shared
    
    @Indexed(unique = true)
    private String accessToken; // Unique secure random token for access
    
    private String accessKey; // Encrypted key for shared files
    
    @Indexed
    private LocalDateTime expiresAt; // When the share expires
    
    private LocalDateTime createdAt;
    
    public Share(String ownerId, String recipientEmail, List<String> nodeIds, String accessToken, String accessKey, LocalDateTime expiresAt) {
        this.ownerId = ownerId;
        this.recipientEmail = recipientEmail;
        this.nodeIds = nodeIds;
        this.accessToken = accessToken;
        this.accessKey = accessKey;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
