package com.c4cometrue.mystorage.dto;

import com.c4cometrue.mystorage.file.FileMetadata;
import com.c4cometrue.mystorage.folder.FolderMetadata;
import com.c4cometrue.mystorage.meta.MetadataType;

public record MetaResponse(
	Long id,
	String metadataOriginalName,
	Long parentId,
	MetadataType type
) {
	public static MetaResponse from(FileMetadata metadata) {
		return new MetaResponse(
			metadata.getId(),
			metadata.getOriginalFileName(),
			metadata.getParentId(),
			metadata.getMetadataType()
		);
	}

	public static MetaResponse from(FolderMetadata metadata) {
		return new MetaResponse(
			metadata.getId(),
			metadata.getOriginalFolderName(),
			metadata.getParentId(),
			metadata.getMetadataType()
		);
	}
}
