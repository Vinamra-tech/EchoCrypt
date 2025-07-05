package com.vinamra.encrypt_decrypt_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinamra.encrypt_decrypt_backend.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    Optional<Admin> findByUserName(String userName);

}
