package com.biblioteca.digital.domain.service.upload.factory.creators;

import org.springframework.stereotype.Component;

import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.upload.uploaders.PdfUploader;

@Component
public class PdfUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
    	 System.out.println("🔧 FACTORY METHOD: PdfUploaderCreator.createUploader()");
        return new PdfUploader();
    }
    
    @Override
    public BookFormato getFormato() {
        return BookFormato.PDF;
    }
}