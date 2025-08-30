package com.securedhealthrecords.repository;

import com.securedhealthrecords.model.Share;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShareRepository extends MongoRepository<Share, String> {
    
    Optional<Share> findByAccessToken(String accessToken);
    
    List<Share> findByOwnerId(String ownerId);
    
    List<Share> findByOwnerIdAndExpiresAtAfter(String ownerId, LocalDateTime currentTime);
    
    void deleteByExpiresAtBefore(LocalDateTime currentTime); // For cleanup of expired shares
}
