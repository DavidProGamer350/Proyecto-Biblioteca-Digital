package com.biblioteca.digital.domain.service.upload.factory;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import java.util.List;
import com.biblioteca.digital.domain.model.BookFormato;

@Component
public class FileUploaderFactory implements UploadAbstractFactory {

	private final Map<BookFormato, FileUploaderCreator> creators;

	public FileUploaderFactory(List<FileUploaderCreator> creatorList) {

		this.creators = new EnumMap<>(BookFormato.class);
		for (FileUploaderCreator creator : creatorList) {
			creators.put(creator.getFormato(), creator);
		}
	}

	public FileUploaderCreator getCreator(BookFormato formato) {
		System.out.println("🏭 ABSTRACT FACTORY: getCreator(" + formato + ")");
		FileUploaderCreator creator = creators.get(formato);
		if (creator == null) {
			throw new IllegalArgumentException("Formato no soportado: " + formato);
		}
		System.out.println("🏭 ABSTRACT FACTORY: → " + creator.getClass().getSimpleName());
		return creator;
	}
}
