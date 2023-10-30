package com.c4cometrue.mystorage.folder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.FolderCreateRequest;
import com.c4cometrue.mystorage.dto.FolderNameChangeRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {
	private final FolderService folderService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createFolder(FolderCreateRequest req) {
		folderService.createBy(req.userId(), req.userFolderName(), req.parentId());
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changeFolderNameBy(FolderNameChangeRequest req) {
		folderService.changeFolderNameBy(req.folderName(), req.folderId(), req.userId());
	}
}
