package com.biblioteca.digital.domain.service;

public class EpubUploaderCreator extends FileUploaderCreator {
	@Override
	protected FileUploader createUploader() {
		return new EpubUploader();
	}
}