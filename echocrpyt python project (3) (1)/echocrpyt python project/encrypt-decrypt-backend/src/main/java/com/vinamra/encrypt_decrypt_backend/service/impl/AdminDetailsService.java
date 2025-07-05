package com.vinamra.encrypt_decrypt_backend.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vinamra.encrypt_decrypt_backend.entity.Admin;
import com.vinamra.encrypt_decrypt_backend.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 

        Admin admin = adminRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

             

        return User.builder()
                .username(admin.getUserName())
                .password(admin.getPassword())
                .roles("ADMIN")
                .build();
    }
}
