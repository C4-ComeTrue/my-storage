package com.c4cometrue.mystorage.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StoragePathService {
	private final Path storagePath;

	public StoragePathService(@Value("${storage.dir}") String root) {
		this.storagePath = Paths.get(root);
	}

	public Path createBasicFolderPath(String folderPath) {
		return storagePath.resolve(folderPath);
	}

	public Path createFolderPath(String parentFolderPath, String folderPath) {
		return Path.of(parentFolderPath).resolve(folderPath);
	}
}
