package com.biblioteca.digital.domain.service;

public class PdfUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
        return new PdfUploader();
    }
}