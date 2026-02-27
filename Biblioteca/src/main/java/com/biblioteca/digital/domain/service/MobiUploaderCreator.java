package com.biblioteca.digital.domain.service;

public class MobiUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
        return new MobUploader();
    }
}