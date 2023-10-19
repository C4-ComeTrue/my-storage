package com.c4cometrue.mystorage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StoragePathService {
    private final Path storagePath;

    public StoragePathService(@Value("${storage.dir}") String root) {
        this.storagePath = Paths.get(root);
    }

    public Path createTotalPath(String fileStorageName) {
        // 혹시나 이후에 파일 이름에 대한 검증 로직이 필요할 수 있지 않을까?
        return storagePath.resolve(fileStorageName);
    }
}
