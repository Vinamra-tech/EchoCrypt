package com.vinamra.encrypt_decrypt_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinamra.encrypt_decrypt_backend.dto.JwtDTO;
import com.vinamra.encrypt_decrypt_backend.dto.LoginDTO;
import com.vinamra.encrypt_decrypt_backend.service.impl.AdminDetailsService;
import com.vinamra.encrypt_decrypt_backend.util.JwtUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AdminDetailsService adminDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request){

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
            UserDetails userDetails =  adminDetailsService.loadUserByUsername(request.getUserName());
            String token = jwtUtils.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new JwtDTO(token));  
            
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("these are Invalid Credentials");
            
        }
    }
}
