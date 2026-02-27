package com.biblioteca.digital.domain.service;

public abstract class FileUploaderCreator {

	public String upload(String isbn, byte[] content) {
		FileUploader uploader = createUploader();
		if (!uploader.validate(content)) {
			throw new IllegalArgumentException("Archivo no válido para este formato");
		}
		return uploader.upload(isbn, content);
	}

	protected abstract FileUploader createUploader(); // Factory Method
}
