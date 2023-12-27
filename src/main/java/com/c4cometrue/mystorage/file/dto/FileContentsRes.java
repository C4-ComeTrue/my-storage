package com.c4cometrue.mystorage.file.dto;

public record FileContentsRes(Long fileId, String fileName) {
	public static FileContentsRes of (Long fileId, String fileName) {
		return new FileContentsRes(fileId, fileName);
	}
}
