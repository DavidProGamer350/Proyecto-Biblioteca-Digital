package com.biblioteca.digital.domain.service.upload.uploaders;

import com.biblioteca.digital.domain.service.FileUploader;

public class EpubUploader implements FileUploader {
	private static final String EPUB_MAGIC = "application/epub+zip";

	@Override
	public String upload(String isbn, byte[] content) {
		if (!validate(content))
			throw new IllegalArgumentException("No es EPUB válido");
		return "/uploads/" + isbn + ".epub";
	}

	@Override
	public boolean validate(byte[] content) {
		if (content.length < EPUB_MAGIC.length())
			return false;

		String full = new String(content, java.nio.charset.StandardCharsets.UTF_8);
		return full.contains(EPUB_MAGIC);
	}
}
