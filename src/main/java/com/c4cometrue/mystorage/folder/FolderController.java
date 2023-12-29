package com.c4cometrue.mystorage.folder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.folder.dto.FolderCreateRequest;
import com.c4cometrue.mystorage.folder.dto.FolderMoveReq;
import com.c4cometrue.mystorage.folder.dto.FolderNameChangeRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {
	private final FolderService folderService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createFolder(@Valid FolderCreateRequest req) {
		folderService.createBy(req.userId(), req.userFolderName(), req.parentId());
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changeFolderNameBy(@Valid FolderNameChangeRequest req) {
		folderService.changeFolderNameBy(req.folderName(), req.folderId(), req.userId());
	}

	@PostMapping("/move")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void moveFolder(FolderMoveReq req) {
		folderService.moveFolder(req.folderId(), req.userId(), req.destinationFolderId());
	}
}
