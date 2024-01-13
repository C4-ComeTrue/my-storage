package com.c4cometrue.mystorage.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.api.dto.FolderGetDto;
import com.c4cometrue.mystorage.api.dto.FolderRenameDto;
import com.c4cometrue.mystorage.api.dto.FolderUploadDto;
import com.c4cometrue.mystorage.service.FolderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/folders")
public class FolderController {

	private final FolderService folderService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FolderUploadDto.Res createFolder(
		@RequestBody @Valid FolderUploadDto.Req req
	) {
		return folderService.createFolder(req.userId(), req.parentId(), req.name());
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public void renameFolder(
		@RequestBody @Valid FolderRenameDto.Req req
	) {
		folderService.renameFolder(req.userId(), req.folderId(), req.name());
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public FolderGetDto.Res getFolderContents(
		@RequestBody @Valid FolderGetDto.Req req
	) {
		return folderService.getFolderContents(req.userId(), req.folderId());
	}

}
