package com.c4cometrue.mystorage.file.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FilePathService {
	private final String dirPath;

	public FilePathService(@Value("${storage.dir}") String dirPath) {
		this.dirPath = dirPath;
	}

	public String createSavedPath(String fileName) {

		return dirPath + UUID.randomUUID() + "." + fileName;
	}
}
