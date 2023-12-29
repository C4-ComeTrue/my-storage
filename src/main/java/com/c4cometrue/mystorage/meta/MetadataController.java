package com.c4cometrue.mystorage.meta;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.meta.dto.DeleteFolderReq;
import com.c4cometrue.mystorage.folder.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.meta.dto.CursorMetaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MetadataController {
	private final StorageFacadeService storageFacadeService;

	@GetMapping("/metadata")
	public ResponseEntity<CursorMetaResponse> getFolderContents(@Valid FolderContentsRequest req) {
		CursorMetaResponse res = storageFacadeService.getFolderContents(req.parentId(), req.cursorId(), req.userId(),
			req.size(), req.cursorFlag());
		return ResponseEntity.ok(res);
	}

	@DeleteMapping("/metadata")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFolder(DeleteFolderReq req) {
		storageFacadeService.deleteFolderContents(req.folderId(), req.userId());
	}
}
