package com.securedhealthrecords.service;

import com.securedhealthrecords.dto.NodeDTO;
import com.securedhealthrecords.exception.InvalidRequestException;
import com.securedhealthrecords.exception.ResourceNotFoundException;
import com.securedhealthrecords.model.Node;
import com.securedhealthrecords.model.Node.NodeType;
import com.securedhealthrecords.repository.NodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NodeService {
    
    private final NodeRepository nodeRepository;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    public List<NodeDTO> getNodesByParent(String ownerId, String parentId) {
        List<Node> nodes = nodeRepository.findByOwnerIdAndParentId(ownerId, parentId);
        return nodes.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public NodeDTO createFolder(String ownerId, String parentId, String name) {
        // Check if folder with same name exists in parent
        if (nodeRepository.existsByOwnerIdAndParentIdAndName(ownerId, parentId, name)) {
            throw new InvalidRequestException("Folder with name '" + name + "' already exists");
        }
        
        Node folder = new Node(ownerId, parentId, NodeType.FOLDER, name);
        Node savedFolder = nodeRepository.save(folder);
        
        return convertToDTO(savedFolder);
    }
    
    public NodeDTO createFileNode(String ownerId, String parentId, String name, String mimeType, String encryptedFileKey) {
        // Check if file with same name exists in parent
        if (nodeRepository.existsByOwnerIdAndParentIdAndName(ownerId, parentId, name)) {
            throw new InvalidRequestException("File with name '" + name + "' already exists");
        }
        
        // Generate unique storage key for S3
        String storageKey = generateStorageKey(ownerId, name);
        
        Node file = new Node(ownerId, parentId, name, mimeType, storageKey, encryptedFileKey);
        Node savedFile = nodeRepository.save(file);
        
        return convertToDTO(savedFile);
    }
    
    public NodeDTO updateNode(String nodeId, String ownerId, String newName) {
        Node node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new ResourceNotFoundException("Node not found"));
        
        if (!node.getOwnerId().equals(ownerId)) {
            throw new InvalidRequestException("Unauthorized access to node");
        }
        
        // Check if new name conflicts with existing nodes in same parent
        if (!node.getName().equals(newName) && 
            nodeRepository.existsByOwnerIdAndParentIdAndName(ownerId, node.getParentId(), newName)) {
            throw new InvalidRequestException("Name '" + newName + "' already exists in this location");
        }
        
        node.setName(newName);
        Node updatedNode = nodeRepository.save(node);
        
        return convertToDTO(updatedNode);
    }
    
    @Transactional
    public void deleteNode(String nodeId, String ownerId) {
        Node node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new ResourceNotFoundException("Node not found"));
        
        if (!node.getOwnerId().equals(ownerId)) {
            throw new InvalidRequestException("Unauthorized access to node");
        }
        
        if (node.getType() == NodeType.FOLDER) {
            // Delete all children recursively
            deleteNodeRecursively(nodeId);
        } else {
            // For files, we should also delete from S3 (implement S3 service later)
            nodeRepository.deleteById(nodeId);
        }
    }
    
    private void deleteNodeRecursively(String nodeId) {
        // Find all children
        List<Node> children = nodeRepository.findByParentId(nodeId);
        
        for (Node child : children) {
            if (child.getType() == NodeType.FOLDER) {
                deleteNodeRecursively(child.getId());
            } else {
                // Delete file from S3 (implement S3 service later)
                nodeRepository.deleteById(child.getId());
            }
        }
        
        // Delete the parent node
        nodeRepository.deleteById(nodeId);
    }
    
    private String generateStorageKey(String ownerId, String fileName) {
        return String.format("%s/%s_%s", ownerId, UUID.randomUUID().toString(), fileName);
    }
    
    private NodeDTO convertToDTO(Node node) {
        NodeDTO dto = new NodeDTO();
        dto.setId(node.getId());
        dto.setOwnerId(node.getOwnerId());
        dto.setParentId(node.getParentId());
        dto.setType(node.getType());
        dto.setName(node.getName());
        dto.setMimeType(node.getMimeType());
        dto.setStorageKey(node.getStorageKey());
        dto.setEncryptedFileKey(node.getEncryptedFileKey());
        dto.setCreatedAt(node.getCreatedAt());
        
        // Generate pre-signed URL for files (implement S3 service later)
        if (node.getType() == NodeType.FILE && node.getStorageKey() != null) {
            // dto.setDownloadUrl(s3Service.generatePresignedUrl(node.getStorageKey()));
        }
        
        return dto;
    }
}
