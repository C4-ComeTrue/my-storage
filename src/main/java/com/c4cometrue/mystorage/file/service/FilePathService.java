package com.c4cometrue.mystorage.file.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathService {
	private final Path dirPath;

	public FilePathService(@Value("${storage.dir}") String root) {
		this.dirPath = Paths.get(root);
	}

	public Path createSavedPath(String fileName) {
		return dirPath.resolve(fileName);
	}
}
