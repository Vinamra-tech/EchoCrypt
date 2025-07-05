package com.vinamra.encrypt_decrypt_backend.service;


public interface EmailService {

    public void sendEncryptedFileEmail(String toEmail, byte[] fileContent, String attachmentFileName , String password);

}
