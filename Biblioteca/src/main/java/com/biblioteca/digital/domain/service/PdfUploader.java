package com.biblioteca.digital.domain.service;

public class PdfUploader implements FileUploader {
	@Override
	public String upload(String isbn, byte[] content) {
		if (!validate(content))
			throw new IllegalArgumentException("No es PDF válido");
		String path = "/uploads/" + isbn + ".pdf";
		// Files.write(Paths.get("src/main/resources/static" + path), content);
		return path;
	}

    @Override
    public boolean validate(byte[] content) {
        if (content.length < 8) return false;
        String header = new String(content, 0, 8);
        return header.startsWith("%PDF-");  // ⭐ Flexible: 1.3, 1.4, 1.7, 2.0
    }

}

