package com.securedhealthrecords.controller;

import com.securedhealthrecords.dto.ShareDTO;
import com.securedhealthrecords.exception.UnauthorizedException;
import com.securedhealthrecords.model.User;
import com.securedhealthrecords.repository.UserRepository;
import com.securedhealthrecords.service.ShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShareController {
    
    private final ShareService shareService;
    
    @PostMapping
    public ResponseEntity<ShareDTO> createShare(
            @Valid @RequestBody ShareDTO shareRequest,
            Authentication authentication) {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        ShareDTO share = shareService.createShare(userId, shareRequest);
        return ResponseEntity.ok(share);
    }
    
    @GetMapping("/view/{accessToken}")
    public ResponseEntity<ShareDTO> viewShare(@PathVariable String accessToken) {
        ShareDTO share = shareService.getShareByToken(accessToken);
        return ResponseEntity.ok(share);
    }
    
    @GetMapping("/patient/shares")
    public ResponseEntity<List<ShareDTO>> getUserShares(Authentication authentication) {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        List<ShareDTO> shares = shareService.getUserShares(userId);
        return ResponseEntity.ok(shares);
    }
    
    @DeleteMapping("/patient/shares/{shareId}")
    public ResponseEntity<Void> revokeShare(
            @PathVariable String shareId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        String userId = getUserIdFromEmail(userEmail);
        
        shareService.revokeShare(shareId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @Autowired
    private UserRepository userRepository;
    
    // Get userId from email using UserRepository
    private String getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }
}
