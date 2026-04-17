package com.biblioteca.digital.domain.service.upload.uploaders;

import java.nio.charset.StandardCharsets; // ⭐ IMPORT

import com.biblioteca.digital.domain.service.FileUploader;

public class MobUploader implements FileUploader {
	@Override
	public String upload(String isbn, byte[] content) {
		if (!validate(content))
			throw new IllegalArgumentException("No es MOBI válido");
		return "/uploads/" + isbn + ".mobi";
	}

	@Override
	public boolean validate(byte[] content) {
		if (content.length < 100)
			return false;

		String first200 = new String(content, 0, Math.min(200, content.length), StandardCharsets.UTF_8);
		return first200.contains("MOBI") || first200.contains("TEXT");
	}
}
