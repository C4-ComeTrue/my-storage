package com.c4cometrue.mystorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.request.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.UpdateFolderNameReq;
import com.c4cometrue.mystorage.dto.response.CreateFolderRes;
import com.c4cometrue.mystorage.dto.response.FolderOverviewRes;
import com.c4cometrue.mystorage.service.FolderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

	private final FolderService folderService;

	@GetMapping()
	@ResponseStatus(HttpStatus.OK)
	public FolderOverviewRes getFolderData(@Valid GetFolderReq getFolderReq) {
		return folderService.getFolderData(getFolderReq);
	}

	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public CreateFolderRes createFolder(@RequestBody @Valid CreateFolderReq createFolderReq) {
		return folderService.createFolder(createFolderReq);
	}

	@PatchMapping("/name")
	@ResponseStatus(HttpStatus.OK)
	public void updateFolderName(@RequestBody @Valid UpdateFolderNameReq updateFolderNameReq) {
		folderService.updateFolderName(updateFolderNameReq);
	}
}
