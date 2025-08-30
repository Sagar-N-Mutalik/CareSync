package com.securedhealthrecords.repository;

import com.securedhealthrecords.model.Node;
import com.securedhealthrecords.model.Node.NodeType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends MongoRepository<Node, String> {
    
    List<Node> findByOwnerIdAndParentId(String ownerId, String parentId);
    
    List<Node> findByOwnerId(String ownerId);
    
    List<Node> findByOwnerIdAndType(String ownerId, NodeType type);
    
    List<Node> findByParentId(String parentId);
    
    boolean existsByOwnerIdAndParentIdAndName(String ownerId, String parentId, String name);
    
    void deleteByParentId(String parentId); // For cascading delete of folder contents
}
