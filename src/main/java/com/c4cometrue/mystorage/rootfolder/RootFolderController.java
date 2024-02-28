package com.c4cometrue.mystorage.rootfolder;

import com.c4cometrue.mystorage.rootfolder.dto.CreateRootFolderReq;
import com.c4cometrue.mystorage.rootfolder.dto.RootInfo;
import com.c4cometrue.mystorage.rootfolder.dto.RootInfoReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rootFolders")
@RequiredArgsConstructor
public class RootFolderController {
    private final RootFolderService rootFolderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRootFolder(@RequestBody @Valid CreateRootFolderReq req) {
        rootFolderService.createBy(req.userId(), req.userFolderName());
    }

    @GetMapping
    public ResponseEntity<RootInfo> getRootInfo(@RequestBody @Valid RootInfoReq req) {
        RootInfo rootInfo = rootFolderService.getRootInfo(req.rootId(), req.userId());
        return ResponseEntity.ok(rootInfo);
    }
}
