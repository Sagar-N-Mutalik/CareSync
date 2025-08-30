package com.securedhealthrecords.controller;

import com.securedhealthrecords.dto.NodeDTO;
import com.securedhealthrecords.exception.UnauthorizedException;
import com.securedhealthrecords.model.User;
import com.securedhealthrecords.repository.UserRepository;
import com.securedhealthrecords.service.NodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nodes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NodeController {
    
    private final NodeService nodeService;
    
    @GetMapping
    public ResponseEntity<List<NodeDTO>> getNodes(
            @RequestParam(required = false) String parentId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        // In a real implementation, you'd get userId from userEmail
        String userId = getUserIdFromEmail(userEmail);
        
        List<NodeDTO> nodes = nodeService.getNodesByParent(userId, parentId);
        return ResponseEntity.ok(nodes);
    }
    
    @PostMapping("/folder")
    public ResponseEntity<NodeDTO> createFolder(
            @Valid @RequestBody Map<String, String> request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        NodeDTO folder = nodeService.createFolder(
            userId,
            request.get("parentId"),
            request.get("name")
        );
        return ResponseEntity.ok(folder);
    }
    
    @PostMapping("/file")
    public ResponseEntity<NodeDTO> createFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("parentId") String parentId,
            @RequestParam("name") String name,
            @RequestParam("mimeType") String mimeType,
            @RequestParam("encryptedFileKey") String encryptedFileKey,
            Authentication authentication) throws IOException {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        NodeDTO fileNode = nodeService.createFileNode(
            userId,
            parentId,
            name,
            mimeType,
            encryptedFileKey,
            file
        );
        return ResponseEntity.ok(fileNode);
    }
    
    @PutMapping("/{nodeId}")
    public ResponseEntity<NodeDTO> updateNode(
            @PathVariable String nodeId,
            @Valid @RequestBody Map<String, String> request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        NodeDTO updatedNode = nodeService.updateNode(nodeId, userId, request.get("name"));
        return ResponseEntity.ok(updatedNode);
    }
    
    @DeleteMapping("/{nodeId}")
    public ResponseEntity<Void> deleteNode(
            @PathVariable String nodeId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        nodeService.deleteNode(nodeId, userId);
        return ResponseEntity.noContent().build();
    }
    
    private final UserRepository userRepository;
    
    // Get userId from email using UserRepository
    private String getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}
