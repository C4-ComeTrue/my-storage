package com.c4cometrue.mystorage.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
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
import com.c4cometrue.mystorage.dto.response.file.FileMetaDataRes;
import com.c4cometrue.mystorage.dto.response.folder.CreateFolderRes;
import com.c4cometrue.mystorage.dto.response.folder.FolderMetaDataRes;
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
		return folderService.getFolderTotalInfo(req.folderId(), req.userName());
	}

	/**
	 * 폴더의 하위 폴더들을 페이징으로 조회
	 * @param folderId 폴더 기본키
	 * @param pageable paging을 위한 파라미터
	 * @return page 번호에 맞는 하위 폴더 목록
	 */
	@GetMapping("/subFolder")
	@ResponseStatus(HttpStatus.OK)
	public List<FolderMetaDataRes> getSubFolders(long folderId, Pageable pageable) {
		return folderService.getFolders(folderId, pageable.getPageNumber());
	}

	/**
	 * 폴더의 하위 파일들을 페이징으로 조회
	 * @param folderId 폴더 기본키
	 * @param pageable paging을 위한 파라미터
	 * @return page 번호에 맞는 하위 파일 목록
	 */
	@GetMapping("/subFile")
	@ResponseStatus(HttpStatus.OK)
	public List<FileMetaDataRes> getSubFiles(long folderId, Pageable pageable) {
		return folderService.getFiles(folderId, pageable.getPageNumber());
	}

	/**
	 * 폴더 생성 요청이 성공하면 해당 폴더 pk를 포함한 정보 반환
	 * @param req (폴더 기본키, 사용자 이름, 부모 폴더 기본키)
	 * @return {@link CreateFolderRes}
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CreateFolderRes createFolder(@RequestBody @Valid CreateFolderReq req) {
		return folderService.createFolder(req.parentFolderId(), req.userName(), req.folderName());
	}

	/**
	 * 폴더 이름을 수정하는 요청
	 * @param req (폴더 기본키, 부모 폴더 기본키, 사용자 이름, 새로운 폴더 이름)
	 */
	@PatchMapping("/name")
	@ResponseStatus(HttpStatus.OK)
	public void updateFolderName(@RequestBody @Valid UpdateFolderNameReq req) {
		folderService.updateFolderName(req.folderId(), req.parentFolderId(),
			req.userName(),	req.newFolderName());
	}

	/**
	 * 폴더를 삭제하는 요청
	 * @param req (폴더 기본키)
	 */
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFolder(@RequestBody @Valid DeleteFolderReq req) {
		folderService.deleteFolder(req.folderId(), req.userName());
	}

	/**
	 * 폴더를 특정 폴더 위치로 이동하는 요청
	 * @param req (폴더 기본키, 이동할 폴더 기본키, 사용자 이름)
	 */
	@PatchMapping
	@ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
	public void moveFolder(@RequestBody @Valid MoveFolderReq req) {
		folderService.moveFolder(req.folderId(), req.targetFolderId(), req.userName());
	}
}
