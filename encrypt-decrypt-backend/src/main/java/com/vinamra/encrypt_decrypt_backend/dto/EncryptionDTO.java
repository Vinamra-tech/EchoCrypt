package com.vinamra.encrypt_decrypt_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class EncryptionDTO {

    private String zipFileName;
    private String password;
}
