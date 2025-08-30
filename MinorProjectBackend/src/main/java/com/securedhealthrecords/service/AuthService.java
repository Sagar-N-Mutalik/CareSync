package com.securedhealthrecords.service;

import com.securedhealthrecords.dto.AuthRequestDTO;
import com.securedhealthrecords.dto.AuthResponseDTO;
import com.securedhealthrecords.exception.InvalidRequestException;
import com.securedhealthrecords.exception.UnauthorizedException;
import com.securedhealthrecords.model.User;
import com.securedhealthrecords.repository.UserRepository;
import com.securedhealthrecords.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailerSendService mailerSendService;
    
    // In-memory OTP storage (in production, use Redis or database)
    private final Map<String, String> otpStorage = new HashMap<>();
    
    public AuthResponseDTO register(AuthRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("Email already exists");
        }
        
        User user = new User(
            request.getFullName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword())
        );
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());
        
        return new AuthResponseDTO(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName());
    }
    
    public AuthResponseDTO login(AuthRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDTO(token, user.getId(), user.getEmail(), user.getFullName());
    }
    
    public AuthResponseDTO sendOtp(String email) {
        String otp = generateOtp();
        otpStorage.put(email, otp);
        
        // Send OTP via email
        mailerSendService.sendOtpEmail(email, otp);
        
        return new AuthResponseDTO("OTP sent successfully to " + email);
    }
    
    public AuthResponseDTO verifyOtp(AuthRequestDTO request) {
        String storedOtp = otpStorage.get(request.getEmail());
        
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new InvalidRequestException("Invalid or expired OTP");
        }
        
        // Remove OTP after successful verification
        otpStorage.remove(request.getEmail());
        
        // Check if user exists, if not create account
        User user = userRepository.findByEmail(request.getEmail())
            .orElseGet(() -> {
                User newUser = new User(
                    request.getFullName() != null ? request.getFullName() : "User",
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword())
                );
                return userRepository.save(newUser);
            });
        
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponseDTO(token, user.getId(), user.getEmail(), user.getFullName());
    }
    
    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
