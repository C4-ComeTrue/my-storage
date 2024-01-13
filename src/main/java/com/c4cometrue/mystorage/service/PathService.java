package com.c4cometrue.mystorage.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PathService {

	private static final String DELIMITER = "/";

	@Value("${file.upload-dir}")
	private String baseDir;

	public String createUniqueFileName() {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		return now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + UUID.randomUUID();
	}

	/**
	 * 파일의 물리적인 경로 반환
	 * @param parentDir
	 * @param fileName
	 * @return
	 */
	public String getFullFilePath(String parentDir, String fileName) {
		String validatedFileName = fileName.stripTrailing();
		if (!StringUtils.hasLength(parentDir)) {
			return baseDir + validatedFileName;
		}
		return baseDir + parentDir + DELIMITER + validatedFileName;
	}

}
