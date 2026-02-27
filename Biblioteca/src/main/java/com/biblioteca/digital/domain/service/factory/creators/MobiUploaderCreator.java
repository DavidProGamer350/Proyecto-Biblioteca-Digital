package com.biblioteca.digital.domain.service.factory.creators;

import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.uploaders.MobUploader;

public class MobiUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
        return new MobUploader();
    }
}