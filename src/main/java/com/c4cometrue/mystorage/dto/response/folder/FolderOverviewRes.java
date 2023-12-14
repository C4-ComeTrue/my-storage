package com.c4cometrue.mystorage.dto.response.folder;

import java.util.List;

import com.c4cometrue.mystorage.dto.response.file.FileMetaDataRes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 한 폴더 전체 정보 반환
 * @param folderId 폴더 기본키
 * @param folderName 폴더 이름
 * @param userName 소유자 이름
 * @param fileList 배열 : 파일 목록
 * @param folderList 배열 : 폴더 목록 (하위 폴더의 파일, 폴더 정보는 포함하지 않음)
 */
public record FolderOverviewRes(
	@NotNull(message = "folder pk is null") long folderId,
	@NotBlank(message = "folder name is blank") String folderName,
	@NotBlank(message = "user name is blank") String userName,
	@NotNull List<FileMetaDataRes> fileList,
	@NotNull List<FolderMetaDataRes> folderList
) {
}
