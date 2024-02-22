package com.c4cometrue.mystorage.rootfile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
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
    public void createRootFolder(CreateRootFolderReq req) {
        rootFolderService.createBy(req.userId(), req.userFolderName());
    }
}
