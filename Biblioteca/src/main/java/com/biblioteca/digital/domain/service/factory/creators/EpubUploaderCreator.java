package com.biblioteca.digital.domain.service.factory.creators;

import com.biblioteca.digital.domain.service.FileUploader;
import com.biblioteca.digital.domain.service.factory.FileUploaderCreator;
import com.biblioteca.digital.domain.service.uploaders.EpubUploader;

public class EpubUploaderCreator extends FileUploaderCreator {
	@Override
	protected FileUploader createUploader() {
		return new EpubUploader();
	}
}