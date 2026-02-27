package com.biblioteca.digital.domain.service.factory;

import java.util.EnumMap;
import java.util.Map;

import com.biblioteca.digital.domain.model.BookFormato;
import com.biblioteca.digital.domain.service.factory.creators.EpubUploaderCreator;
import com.biblioteca.digital.domain.service.factory.creators.FileUploaderCreator;
import com.biblioteca.digital.domain.service.factory.creators.PdfUploaderCreator;

import org.springframework.stereotype.Component; 

@Component
public class FileUploaderFactory {

    private final Map<BookFormato, FileUploaderCreator> creators = new EnumMap<>(BookFormato.class);

    public FileUploaderFactory() {
        creators.put(BookFormato.PDF,  new PdfUploaderCreator());
        creators.put(BookFormato.EPUB, new EpubUploaderCreator());
        creators.put(BookFormato.MOBI, new MobiUploaderCreator());
    }

    public FileUploaderCreator getCreator(BookFormato formato) {
        FileUploaderCreator creator = creators.get(formato);
        if (creator == null) {
            throw new IllegalArgumentException("Formato no soportado: " + formato);
        }
        return creator;
    }
}

