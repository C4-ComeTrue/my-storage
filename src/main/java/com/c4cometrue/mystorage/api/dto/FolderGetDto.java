package com.c4cometrue.mystorage.api.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.domain.FileType;

import jakarta.validation.constraints.NotNull;

public class FolderGetDto {

	public record Req(
		@NotNull(message = "유저 ID는 Null이 될 수 없습니다.") Long userId,
		@NotNull(message = "폴더 ID는 Null이 될 수 없습니다.") Long folderId
	) {
	}

	public record Res(
		Long folderId,
		String folderName,
		List<FileDto> subFileList
	) {
	}

	public record FileDto(
		long folderId,
		FileType fileType,
		String folderName,
		String createdAt,
		long fileSize
	) {
		public static FileDto from(FileMetaData data) {
			return new FileDto(
				data.getId(),
				data.getFileType(),
				data.getFileName(),
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(data.getCreatedAt()),
				data.getSize()
			);
		}
	}

}
