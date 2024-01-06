package com.c4cometrue.mystorage.storage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.folder.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.storage.dto.CursorMetaRes;
import com.c4cometrue.mystorage.storage.dto.DeleteFolderReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class StorageController {
	private final StorageFacadeService storageFacadeService;

	@GetMapping("/metadata")
	public ResponseEntity<CursorMetaRes> getFolderContents(@Valid FolderContentsRequest req) {
		CursorMetaRes res = storageFacadeService.getFolderContents(req.parentId(), req.cursorId(), req.userId(),
			req.size(), req.cursorFlag());
		return ResponseEntity.ok(res);
	}

	@DeleteMapping("/metadata")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFolder(DeleteFolderReq req) {
		storageFacadeService.deleteFolderContents(req.folderId(), req.userId());
	}
}
