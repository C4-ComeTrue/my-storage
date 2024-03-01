package com.c4cometrue.mystorage.folder;

import com.c4cometrue.mystorage.folder.dto.FolderCreateRequest;
import com.c4cometrue.mystorage.folder.dto.FolderMoveReq;
import com.c4cometrue.mystorage.folder.dto.FolderNameChangeRequest;
import com.c4cometrue.mystorage.folder.dto.FolderSummaryReq;
import com.c4cometrue.mystorage.folder.dto.FolderSummaryRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createFolder(@RequestBody @Valid FolderCreateRequest req) {
        folderService.createBy(req.userId(), req.userFolderName(), req.parentId(), req.rootId());
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeFolderNameBy(@RequestBody @Valid FolderNameChangeRequest req) {
        folderService.changeFolderNameBy(req.folderName(), req.folderId(), req.userId());
    }

    @PostMapping("/move")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moveFolder(@RequestBody FolderMoveReq req) {
        folderService.moveFolder(req.folderId(), req.userId(), req.destinationFolderId());
    }

    @GetMapping("/summary")
    public ResponseEntity<FolderSummaryRes> getFolderSummary(@RequestBody FolderSummaryReq req) {
        return ResponseEntity.ok(folderService.getFolderSummary(req.folderId(), req.userId()));
    }
}
