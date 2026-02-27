package com.biblioteca.digital.domain.service.factory.creators;

import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.uploaders.PdfUploader;

public class PdfUploaderCreator extends FileUploaderCreator {
    @Override
    protected FileUploader createUploader() {
        return new PdfUploader();
    }
}