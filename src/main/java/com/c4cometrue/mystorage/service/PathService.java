package com.c4cometrue.mystorage.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PathService {

	private static final String DELIMITER = "/";

	@Value("${file.upload-dir}")
	private String baseDir;

	public String createUniqueFileName(String originFileName) {
		return UUID.randomUUID() + originFileName;
	}

	/**
	 * 파일의 논리적인 경로 반환
	 * @param parentDir
	 * @param parentName
	 * @return
	 */
	public String getFilePath(String parentDir, String parentName) {
		return parentDir + DELIMITER + parentName;
	}

	/**
	 * 파일의 물리적인 경로 반환
	 * @param parentDir
	 * @param fileName
	 * @return
	 */
	public String getFullFilePath(String parentDir, String fileName) {
		String validatedFileName = fileName.trim();
		if (Objects.equals(parentDir, "")) {
			return baseDir + validatedFileName;
		}
		return baseDir + parentDir + DELIMITER + validatedFileName;
	}

}
