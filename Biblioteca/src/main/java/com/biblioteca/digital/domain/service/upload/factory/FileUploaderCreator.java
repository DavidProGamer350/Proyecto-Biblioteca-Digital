package com.biblioteca.digital.domain.service.upload.factory;

import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.service.FileUploader;

public abstract class FileUploaderCreator {

	public String upload(String isbn, byte[] content) {
		System.out.println("🔧 FACTORY METHOD: Cliente llama upload() en creator abstracto");
		FileUploader uploader = createUploader();
		System.out.println("🔧 FACTORY METHOD: createUploader() ejecutado → " + uploader.getClass().getSimpleName());
		if (!uploader.validate(content)) {
			throw new IllegalArgumentException("Archivo no válido para este formato");
		}
		return uploader.upload(isbn, content);
	}

	protected abstract FileUploader createUploader(); // Factory Method

	public abstract BookFormato getFormato();
	
}
