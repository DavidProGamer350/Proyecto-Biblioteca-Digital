package com.biblioteca.digital.domain.service.factory;

import java.util.EnumMap;
import java.util.Map;
import java.util.List;
import com.biblioteca.digital.domain.model.BookFormato;

public class FileUploaderFactory {

	private final Map<BookFormato, FileUploaderCreator> creators;

	public FileUploaderFactory(List<FileUploaderCreator> creatorList) {

		this.creators = new EnumMap<>(BookFormato.class);
		for (FileUploaderCreator creator : creatorList) {
			creators.put(creator.getFormato(), creator);
		}
	}

	public FileUploaderCreator getCreator(BookFormato formato) {
		FileUploaderCreator creator = creators.get(formato);
		if (creator == null) {
			throw new IllegalArgumentException("Formato no soportado: " + formato);
		}
		return creator;
	}
}
