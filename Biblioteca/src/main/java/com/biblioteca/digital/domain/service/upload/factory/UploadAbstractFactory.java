package com.biblioteca.digital.domain.service.upload.factory;

import com.biblioteca.digital.domain.model.BookFormato;

public interface UploadAbstractFactory {
	
	 FileUploaderCreator getCreator(BookFormato formato);

}
