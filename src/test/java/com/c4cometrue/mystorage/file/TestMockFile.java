package com.c4cometrue.mystorage.file;

import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.file.entity.FileMetaData;

public class TestMockFile {

	public static final MultipartFile MOCK_MULTIPLE_FILE = mock(MultipartFile.class);

	public static final String MOCK_ROOT = "/Users/sinhyeyeon/Desktop/storage/";

	public static final Path MOCK_ROOT_PATH = Paths.get("/Users/sinhyeyeon/Desktop/storage/");
	public static final String MOCK_DOWN_ROOT_PATH = "/Users/sinhyeyeon/Desktop/storage_down/";
	public static final Long MOCK_FILE_ID = 1L;
	public static final String MOCK_USER_NAME = "userName";
	public static final String MOCK_FILE_NAME = "test.txt";
	public static final String MOCK_FILE_PATH = UUID.randomUUID() + MOCK_FILE_NAME;
	public static final Long MOCK_SIZE = 100L;
	public static final String MOCK_CONTENT_TYPE = "text/plain";
	public static final FileMetaData MOCK_FILE_META_DATA =
		FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.savedPath(MOCK_FILE_PATH)
			.fileSize(MOCK_SIZE)
			.userName(MOCK_USER_NAME)
			.fileMine(MOCK_CONTENT_TYPE)
			.build();
	public static final Path MOCK_UPLOAD_PATH = MOCK_ROOT_PATH.resolve(MOCK_FILE_PATH);
	public static final Path MOCK_DELETE_PATH = Paths.get(MOCK_FILE_PATH).toAbsolutePath();
	public static final Path MOCK_DOWNLOAD_PATH = Path.of(MOCK_DOWN_ROOT_PATH).resolve(MOCK_FILE_NAME);

	public static final int MOCK_BUFFER_SIZE = 1024;
	public static final int MOCK_READ_CNT = 0;

	public static final byte MOCK_BUFFER[] = new byte[MOCK_BUFFER_SIZE];

}
