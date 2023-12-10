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

	/**
	 * 루트 경로에서 주어진 폴더 이름에 해당하는 경로를 만든다.
	 * @param userName 사용자 이름
	 * @return 해당 폴더 절대 경로
	 */
	public Path createPathByUser(String userName) {
		return storagePath.resolve(userName);
	}
}
