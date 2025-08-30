package com.securedhealthrecords.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "nodes")
public class Node {
    
    @Id
    private String id;
    
    @Indexed
    private String ownerId; // Reference to User._id
    
    @Indexed
    private String parentId; // Reference to parent Node._id (null for root folders)
    
    @Indexed
    private NodeType type; // FOLDER or FILE
    
    private String name;
    
    // File-specific fields (null for folders)
    private String mimeType;
    private String storageKey; // S3 path to encrypted blob
    private String encryptedFileKey; // AES key encrypted with user's password
    
    private LocalDateTime createdAt;
    
    public Node(String ownerId, String parentId, NodeType type, String name) {
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.type = type;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }
    
    public Node(String ownerId, String parentId, String name, String mimeType, String storageKey, String encryptedFileKey) {
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.type = NodeType.FILE;
        this.name = name;
        this.mimeType = mimeType;
        this.storageKey = storageKey;
        this.encryptedFileKey = encryptedFileKey;
        this.createdAt = LocalDateTime.now();
    }
    
    public enum NodeType {
        FOLDER, FILE
    }
}
