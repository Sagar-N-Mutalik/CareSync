package com.securedhealthrecords.service;

import com.securedhealthrecords.dto.NodeDTO;
import com.securedhealthrecords.dto.ShareDTO;
import com.securedhealthrecords.exception.InvalidRequestException;
import com.securedhealthrecords.exception.ResourceNotFoundException;
import com.securedhealthrecords.model.Node;
import com.securedhealthrecords.model.Share;
import com.securedhealthrecords.model.User;
import com.securedhealthrecords.repository.NodeRepository;
import com.securedhealthrecords.repository.ShareRepository;
import com.securedhealthrecords.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShareService {
    
    private final NodeRepository nodeRepository;
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final MailerSendService mailerSendService;
    private final String frontendUrl;
    
    public ShareService(NodeRepository nodeRepository, ShareRepository shareRepository, 
                       UserRepository userRepository, MailerSendService mailerSendService,
                       @Value("${frontend.url}") String frontendUrl) {
        this.nodeRepository = nodeRepository;
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.mailerSendService = mailerSendService;
        this.frontendUrl = frontendUrl;
    }
    
    @Transactional
    public ShareDTO createShare(String ownerId, ShareDTO shareRequest) {
        // Validate that all nodes belong to the owner
        List<Node> nodesToShare = nodeRepository.findAllById(shareRequest.getNodeIds());
        
        if (nodesToShare.size() != shareRequest.getNodeIds().size()) {
            throw new ResourceNotFoundException("Some nodes were not found");
        }
        
        boolean allNodesOwnedByUser = nodesToShare.stream()
            .allMatch(node -> node.getOwnerId().equals(ownerId));
        
        if (!allNodesOwnedByUser) {
            throw new InvalidRequestException("You can only share your own files");
        }
        
        // Generate secure access token
        String accessToken = generateSecureToken();
        
        // Generate access key for encryption (placeholder - implement proper key derivation)
        String accessKey = generateSecureToken();
        
        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(shareRequest.getDurationHours());
        
        // Create share record
        Share share = new Share(
            ownerId,
            shareRequest.getRecipientEmail(),
            shareRequest.getNodeIds(),
            accessToken,
            accessKey,
            expiresAt
        );
        
        Share savedShare = shareRepository.save(share);
        
        // Send notification email
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String shareLink = generateShareLink(accessToken);
        mailerSendService.sendShareNotificationEmail(
            shareRequest.getRecipientEmail(),
            shareLink,
            owner.getFullName()
        );
        
        // Convert to DTO
        ShareDTO responseDTO = convertToDTO(savedShare);
        responseDTO.setSharedNodes(nodesToShare.stream()
            .map(this::convertNodeToDTO)
            .collect(Collectors.toList()));
        
        return responseDTO;
    }
    
    public ShareDTO getShareByToken(String accessToken) {
        Share share = shareRepository.findByAccessToken(accessToken)
            .orElseThrow(() -> new ResourceNotFoundException("Share not found"));
        
        if (share.isExpired()) {
            throw new InvalidRequestException("Share has expired");
        }
        
        // Get shared nodes
        List<Node> sharedNodes = nodeRepository.findAllById(share.getNodeIds());
        
        ShareDTO shareDTO = convertToDTO(share);
        shareDTO.setSharedNodes(sharedNodes.stream()
            .map(this::convertNodeToDTO)
            .collect(Collectors.toList()));
        
        return shareDTO;
    }
    
    public List<ShareDTO> getUserShares(String ownerId) {
        LocalDateTime now = LocalDateTime.now();
        List<Share> activeShares = shareRepository.findByOwnerIdAndExpiresAtAfter(ownerId, now);
        
        return activeShares.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void revokeShare(String shareId, String ownerId) {
        Share share = shareRepository.findById(shareId)
            .orElseThrow(() -> new ResourceNotFoundException("Share not found"));
        
        if (!share.getOwnerId().equals(ownerId)) {
            throw new InvalidRequestException("You can only revoke your own shares");
        }
        
        shareRepository.deleteById(shareId);
    }
    
    @Transactional
    public void cleanupExpiredShares() {
        LocalDateTime now = LocalDateTime.now();
        shareRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up expired shares before: {}", now);
    }
    
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private String generateShareLink(String accessToken) {
        return String.format("%s/share/%s", frontendUrl, accessToken);
    }
    
    private ShareDTO convertToDTO(Share share) {
        ShareDTO dto = new ShareDTO();
        dto.setId(share.getId());
        dto.setOwnerId(share.getOwnerId());
        dto.setRecipientEmail(share.getRecipientEmail());
        dto.setNodeIds(share.getNodeIds());
        dto.setAccessToken(share.getAccessToken());
        dto.setAccessKey(share.getAccessKey());
        dto.setExpiresAt(share.getExpiresAt());
        dto.setCreatedAt(share.getCreatedAt());
        return dto;
    }
    
    private NodeDTO convertNodeToDTO(Node node) {
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
        return dto;
    }
}
