package com.c4cometrue.mystorage.file;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class TestConstants {
	public static final Long userId = 1L;
	public static final Long nonMatchingUserId = 2L;
	public static final Long fileId = 1L;
	public static final String userPath =  "C:\\Users\\g2c10\\OneDrive\\C4\\down";
	public static final String OriginalFileName = "청천";
	public static final String storedFileName = "청천12345";
	public static final Metadata METADATA = Metadata.of(OriginalFileName, storedFileName, userPath, userId);
	public static final MultipartFile mockMultipartFile = new MockMultipartFile("chungFile", "chung.txt", "text/plain", "chung".getBytes());
	public static final String uuidPattern = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";
}
