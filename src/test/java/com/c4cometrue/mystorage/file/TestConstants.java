package com.c4cometrue.mystorage.file;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class TestConstants {
	public static final Long USER_ID = 1L;
	public static final Long NON_MATCHING_USER_ID = 2L;
	public static final Long FILE_ID = 1L;
	public static final String USER_PATH = "C:\\Users\\g2c10\\OneDrive\\C4\\down";
	public static final String ORIGINAL_FILE_NAME = "청천";
	public static final String STORED_FILE_NAME = "청천12345";
	public static final Metadata METADATA = Metadata.of(ORIGINAL_FILE_NAME, STORED_FILE_NAME, USER_PATH, USER_ID);
	public static final MultipartFile MOCK_MULTIPART_FILE = new MockMultipartFile("chungFile", "chung.txt",
		"text/plain",
		"chung".getBytes());
	public static final String UUID_PATTERN = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";

	public static final String DEBUG_MESSAGE_TEMPLATE = "fileId : %s, userId : %s";
	public static final String DEBUG_MESSAGE = String.format(DEBUG_MESSAGE_TEMPLATE, FILE_ID, USER_ID);

	public static final String ERR_CODE = "UNAUTHORIZED_FILE_ACCESS";
	public static final String ERR_MESSAGE = "비정상적인 요청입니다.";
	public static final Throwable CAUSE = new RuntimeException();
}
