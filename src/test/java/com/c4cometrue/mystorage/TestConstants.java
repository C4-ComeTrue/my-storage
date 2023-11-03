package com.c4cometrue.mystorage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.FolderNameChangeRequest;
import com.c4cometrue.mystorage.file.FileMetadata;
import com.c4cometrue.mystorage.folder.FolderMetadata;

public class TestConstants {
	public static final Long USER_ID = 1L;
	public static final Long PARENT_ID = 1L;
	public static final Long NON_MATCHING_USER_ID = 2L;
	public static final Long FILE_ID = 1L;
	public static final String USER_PATH = "C:\\Users\\g2c10\\OneDrive\\C4\\down";
	public static final String ORIGINAL_FILE_NAME = "청천";
	public static final String STORED_FILE_NAME = "청천12345";
	public static final FileMetadata FILE_METADATA = FileMetadata.builder()
		.originalFileName(ORIGINAL_FILE_NAME)
		.storedFileName(STORED_FILE_NAME)
		.filePath(USER_PATH)
		.uploaderId(USER_ID)
		.parentId(PARENT_ID)
		.build();
	public static final MultipartFile MOCK_MULTIPART_FILE = new MockMultipartFile("chungFile", "chung.txt",
		"text/plain",
		"chung".getBytes());
	public static final String UUID_PATTERN = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";

	public static final String DEBUG_MESSAGE_TEMPLATE = "fileId : %s, userId : %s";
	public static final String DEBUG_MESSAGE = String.format(DEBUG_MESSAGE_TEMPLATE, FILE_ID, USER_ID);

	public static final String ERR_CODE = "UNAUTHORIZED_FILE_ACCESS";
	public static final String ERR_MESSAGE = "비정상적인 요청입니다.";
	public static final Throwable CAUSE = new RuntimeException();

	public static final String USER_FOLDER_NAME = "폴더";
	public static final String STORED_FOLDER_NAME = "폴더@";
	public static final String PARENT_PATH = FolderMetadata.storedName(USER_PATH);
	public static final Path FOLDER_PATH = Paths.get(PARENT_PATH, STORED_FOLDER_NAME);
	public static final FolderMetadata FOLDER_METADATA = FolderMetadata.builder().build();

	public static final Long FOLDER_ID = 1L;

	public static final FolderNameChangeRequest FOLDER_NAME_CHANGE_REQUEST = FolderNameChangeRequest.of(USER_ID, FOLDER_ID, USER_FOLDER_NAME);
}
