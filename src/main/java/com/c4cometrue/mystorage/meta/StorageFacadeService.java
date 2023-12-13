package com.c4cometrue.mystorage.meta;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.dto.MetaResponse;
import com.c4cometrue.mystorage.file.FileMetadata;
import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.folder.FolderMetadata;
import com.c4cometrue.mystorage.folder.FolderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageFacadeService {
	private final FolderService folderService;
	private final FileService fileService;

	/**
	 * 폴더 하위 폴더, 파일을 조회한다
	 * folderId 를 parentId 로 가지는 폴더와 파일을 조회
	 * @param folderId
	 * @param userId
	 * @return
	 */
	public List<MetaResponse> getFolderContents(Long folderId, Long userId) {
		List<FolderMetadata> folderResponses = folderService.findChildBy(folderId, userId);
		List<FileMetadata> fileResponses = fileService.findChildBy(folderId, userId);

		return Stream.concat(
				folderResponses.stream().map(MetaResponse::from),
				fileResponses.stream().map(MetaResponse::from))
			.toList();
	}
}
