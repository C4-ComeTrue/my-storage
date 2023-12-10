package com.c4cometrue.mystorage.dto.response;

import com.c4cometrue.mystorage.entity.FileMetaData;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FileMetaData
 */
public record FileMetaDataRes(@NotBlank(message = "file name is blank") String fileStorageName,
							  @NotNull(message = "size is null") long size,
							  @NotBlank(message = "file content type is blank") String mime,
							  @NotBlank(message = "user name is blank") String userName) {
	public FileMetaDataRes(FileMetaData fileMetaData) {
		this(
			fileMetaData.getFileStorageName(),
			fileMetaData.getSize(),
			fileMetaData.getMime(),
			fileMetaData.getUserName()
		);
	}
}
