package com.c4cometrue.mystorage.entity;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;


class FileMetaDataTest {

    @Test
    void builderTest() {
        // given
        var fileMetaData = FileMetaData.builder()
            .fileName("file.txt")
            .fileStorageName(mockFileStorageName)
            .size(100L)
            .mime("text/plain")
            .userName("userName")
            .folderId(123)
            .build();

        assertThat(fileMetaData)
            .matches(metadata -> metadata.getFileId() == 0)
            .matches(metadata -> StringUtils.equals(metadata.getFileName(), mockFileName))
            .matches(metadata -> StringUtils.equals(metadata.getFileStorageName(), mockFileStorageName))
            .matches(metadata -> metadata.getSize() == mockSize)
            .matches(metadata -> StringUtils.equals(metadata.getMime(), mockContentType))
            .matches(metadata -> StringUtils.equals(metadata.getUserName(), mockUserName))
            .matches(metadata -> metadata.getFolderId() == 123);
    }
}
