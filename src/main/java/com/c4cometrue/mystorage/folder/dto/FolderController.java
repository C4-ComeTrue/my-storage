package com.c4cometrue.mystorage.folder.dto;

import com.c4cometrue.mystorage.folder.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/summary")
    public ResponseEntity<FolderSummaryRes> getFolderSummary(FolderSummaryReq req) {
        return ResponseEntity.ok(folderService.getFolderSummary(req.folderId(), req.userId()));
    }
}
