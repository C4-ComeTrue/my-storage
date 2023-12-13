package com.c4cometrue.mystorage.meta;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.dto.MetaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MetadataController {
	private final StorageFacadeService storageFacadeService;
	@GetMapping("/metadata")
	public ResponseEntity<List<MetaResponse>> getFolderContents(@Valid FolderContentsRequest req) {
		List<MetaResponse> res = storageFacadeService.getFolderContents(req.folderId(), req.userId());
		return ResponseEntity.ok(res);
	}
}
