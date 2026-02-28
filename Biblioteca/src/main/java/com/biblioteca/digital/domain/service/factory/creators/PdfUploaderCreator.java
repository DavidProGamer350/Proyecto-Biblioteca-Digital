package com.biblioteca.digital.domain.service.factory.creators;

import org.springframework.stereotype.Component;

import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.uploaders.PdfUploader;

@Component
public class PdfUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
        return new PdfUploader();
    }
    
    @Override
    public BookFormato getFormato() {
        return BookFormato.PDF;
    }
}