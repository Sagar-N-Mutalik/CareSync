package com.securedhealthrecords.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @SuppressWarnings("unchecked")
    public String uploadFile(MultipartFile file, String userId, String folder) throws IOException {
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Upload to Cloudinary with folder structure
            String publicId = String.format("health-records/%s/%s/%s", userId, folder, uniqueFilename);
            
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", "auto",
                "folder", String.format("health-records/%s/%s", userId, folder),
                "use_filename", false,
                "unique_filename", true
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("File uploaded successfully to Cloudinary: {}", secureUrl);
            
            return secureUrl;
            
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary: {}", e.getMessage());
            throw new IOException("Failed to upload file to Cloudinary", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteFile(String publicId) {
        try {
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("File deleted from Cloudinary: {}", result);
        } catch (IOException e) {
            log.error("Error deleting file from Cloudinary: {}", e.getMessage());
        }
    }

    public String generateDownloadUrl(String publicId) {
        return cloudinary.url().secure(true).generate(publicId);
    }

    // Extract public ID from Cloudinary URL for deletion
    public String extractPublicIdFromUrl(String cloudinaryUrl) {
        if (cloudinaryUrl == null || !cloudinaryUrl.contains("cloudinary.com")) {
            return null;
        }
        
        try {
            // Extract public ID from URL
            String[] parts = cloudinaryUrl.split("/");
            int uploadIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if ("upload".equals(parts[i])) {
                    uploadIndex = i;
                    break;
                }
            }
            
            if (uploadIndex != -1 && uploadIndex + 2 < parts.length) {
                // Skip version (v1234567890) and get the rest
                StringBuilder publicId = new StringBuilder();
                for (int i = uploadIndex + 2; i < parts.length; i++) {
                    if (i > uploadIndex + 2) {
                        publicId.append("/");
                    }
                    String part = parts[i];
                    // Remove file extension from last part
                    if (i == parts.length - 1 && part.contains(".")) {
                        part = part.substring(0, part.lastIndexOf("."));
                    }
                    publicId.append(part);
                }
                return publicId.toString();
            }
        } catch (Exception e) {
            log.error("Error extracting public ID from Cloudinary URL: {}", e.getMessage());
        }
        
        return null;
    }
}
