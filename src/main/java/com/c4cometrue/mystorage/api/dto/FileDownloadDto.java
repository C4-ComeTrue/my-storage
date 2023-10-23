package com.c4cometrue.mystorage.api.dto;

public class FileDownloadDto {

	public record Response(
		Bytes data,
		String contentType
	) {
	}

	public record Bytes(
		byte[] bytes
	) {
	}

}
