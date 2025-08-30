package com.securedhealthrecords.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hospitals")
public class Hospital {
    
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    @Indexed
    private String city;
    
    private String address;
    
    private String phone;
    
    public Hospital(String name, String city, String address, String phone) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
    }
}
