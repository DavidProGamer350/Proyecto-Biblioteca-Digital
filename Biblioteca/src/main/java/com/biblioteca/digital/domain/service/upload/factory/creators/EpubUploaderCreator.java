package com.biblioteca.digital.domain.service.upload.factory.creators;

import org.springframework.stereotype.Component;

import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.upload.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.upload.uploaders.EpubUploader;

@Component
public class EpubUploaderCreator extends FileUploaderCreator {
	@Override
	protected FileUploader createUploader() {
		return new EpubUploader();
	}

	@Override
	public BookFormato getFormato() {
	    return BookFormato.EPUB;
	}
}