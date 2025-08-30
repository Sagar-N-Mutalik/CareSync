package com.securedhealthrecords.repository;

import com.securedhealthrecords.model.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    
    List<Hospital> findByNameContainingIgnoreCase(String name);
    
    List<Hospital> findByCityIgnoreCase(String city);
    
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'city': { $regex: ?1, $options: 'i' } } ] }")
    List<Hospital> findByNameOrCity(String name, String city);
}
