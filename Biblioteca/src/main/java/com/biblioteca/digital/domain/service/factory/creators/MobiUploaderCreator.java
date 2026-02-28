package com.biblioteca.digital.domain.service.factory.creators;

import org.springframework.stereotype.Component;

import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.uploaders.MobUploader;

@Component
public class MobiUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
        return new MobUploader();
    }
    
    @Override
    public BookFormato getFormato() {
        return BookFormato.MOBI;
    }
}