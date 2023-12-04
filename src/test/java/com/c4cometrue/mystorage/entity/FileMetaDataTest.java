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
            .fileStorageName(MOCK_FILE_STORAGE_NAME)
            .size(100L)
            .mime("text/plain")
            .userName("userName")
            .folderId(123)
            .build();

        // then
        assertThat(fileMetaData)
            .matches(metadata -> StringUtils.equals(metadata.getFileName(), MOCK_FILE_NAME))
            .matches(metadata -> StringUtils.equals(metadata.getFileStorageName(), MOCK_FILE_STORAGE_NAME))
            .matches(metadata -> metadata.getSize() == MOCK_SIZE)
            .matches(metadata -> StringUtils.equals(metadata.getMime(), MOCK_CONTENT_TYPE))
            .matches(metadata -> StringUtils.equals(metadata.getUserName(), MOCK_USER_NAME))
            .matches(metadata -> metadata.getFolderId() == 123);
    }
}
