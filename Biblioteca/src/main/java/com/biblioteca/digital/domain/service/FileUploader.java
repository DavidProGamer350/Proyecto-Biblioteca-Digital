package com.biblioteca.digital.domain.service;

public interface FileUploader {
    String upload(String isbn, byte[] content);
    boolean validate(byte[] content);
}
