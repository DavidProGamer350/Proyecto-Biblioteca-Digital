package com.biblioteca.digital.domain.service;

import java.nio.charset.StandardCharsets; // ⭐ IMPORT

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

		// MOBI: busca "MOBI" o "TEXT" en primeros 200 bytes
		String first200 = new String(content, 0, Math.min(200, content.length), StandardCharsets.UTF_8);
		boolean isMobi = first200.contains("MOBI") || first200.contains("TEXT");

		System.out.println("MOBI first200 contains: " + isMobi);
		return isMobi;
	}
}
