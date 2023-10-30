package com.c4cometrue.mystorage.folder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.FolderCreateRequest;

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
}
