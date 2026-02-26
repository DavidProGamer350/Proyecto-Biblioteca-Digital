package com.biblioteca.digital.domain.service;

import com.biblioteca.digital.domain.model.BookFormato;

public class FileUploaderFactory {

	public static FileUploader getUploader(BookFormato formato) {
		return switch (formato) {
		case PDF -> new PdfUploader();
		case EPUB -> new EpubUploader();
		case MOBI -> new MobUploader();
		default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
		};
	}

	public String upload(String isbn, byte[] content, BookFormato formato) {
		FileUploader uploader = FileUploaderFactory.getUploader(formato);
		if (!uploader.validate(content)) {
			throw new IllegalArgumentException("Archivo no coincide con formato " + formato);
		}
		return "/uploads/" + isbn + "." + formato.name().toLowerCase();
	}
}
