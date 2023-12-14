package com.c4cometrue.mystorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.request.folder.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.DeleteFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.MoveFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.UpdateFolderNameReq;
import com.c4cometrue.mystorage.dto.response.folder.CreateFolderRes;
import com.c4cometrue.mystorage.dto.response.folder.FolderOverviewRes;
import com.c4cometrue.mystorage.service.FolderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {

	private final FolderService folderService;

	/**
	 * 폴더의 개략적인 정보 요청
	 * @param req (폴더 기본키, 폴더 이름, 사용자 이름, 부모 폴더 기본키)
	 * @return {@link FolderOverviewRes}
	 */
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public FolderOverviewRes getFolderData(@Valid GetFolderReq req) {
		return folderService.getFolderData(req.folderId(), req.userName());
	}

	/**
	 * 폴더 생성 요청이 성공하면 해당 폴더 pk를 포함한 정보 반환
	 * @param req (폴더 이름, 사용자 이름, 부모 폴더 기본키)
	 * @return {@link CreateFolderRes}
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CreateFolderRes createFolder(@RequestBody @Valid CreateFolderReq req) {
		return folderService.createFolder(req.parentFolderId(), req.userName(), req.folderName());
	}

	/**
	 * 폴더 이름 수정 요청
	 * @param updateFolderNameReq (이전 폴더 이름, 사용자 이름, 새로운 폴더 이름, 부모 폴더 기본키)
	 */
	@PatchMapping("/name")
	@ResponseStatus(HttpStatus.OK)
	public void updateFolderName(@RequestBody @Valid UpdateFolderNameReq updateFolderNameReq) {
		folderService.updateFolderName(updateFolderNameReq.folderId(), updateFolderNameReq.parentFolderId(),
			updateFolderNameReq.userName(),	updateFolderNameReq.newFolderName());
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFolder(@RequestBody @Valid DeleteFolderReq deleteFolderReq) {
		// TODO: 폴더 삭제 구현
	}

	@PatchMapping
	@ResponseStatus(HttpStatus.OK)
	public void moveFolder(@RequestBody @Valid MoveFolderReq moveFolderReq) {
		// TODO: 폴더 이동 구현
	}
}
