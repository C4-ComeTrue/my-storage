package com.c4cometrue.mystorage.file.dto;

public record FileContent(Long fileId, String fileName) {
	public static FileContent of (Long fileId, String fileName) {
		return new FileContent(fileId, fileName);
	}
}
