package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.c4cometrue.mystorage.dto.request.file.SignUpReq;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.entity.UserData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.repository.UserDataRepository;
import com.c4cometrue.mystorage.util.FileUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	UserService userService;

	@Mock
	StoragePathService storagePathService;
	@Mock
	UserDataRepository userDataRepository;
	@Mock
	FolderRepository folderRepository;

	private static MockedStatic<FileUtil> fileUtilMockedStatic;

	@BeforeAll
	public static void setup() {
		fileUtilMockedStatic = mockStatic(FileUtil.class);
	}

	@AfterAll
	public static void tearDown() {
		fileUtilMockedStatic.close();
	}

	@Test
	@DisplayName("회원가입 성공")
	void signUp() {
		// given
		var req = new SignUpReq(MOCK_USER_NAME);
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();
		ReflectionTestUtils.setField(mockFolderMetaData, "folderId", 1L);

		given(folderRepository.save(any())).willReturn(mockFolderMetaData);

		// when
		var signUpRes = userService.signUp(req.userName());

		// then
		assertThat(signUpRes)
			.matches(res -> StringUtils.equals(res.userName(), MOCK_USER_NAME))
			.matches(res -> res.folderId() == 1L);
	}

	@Test
	@DisplayName("중복 이름으로 회원가입 실패")
	void signUpFail() {
		// given
		given(userDataRepository.findByUserName(MOCK_USER_NAME)).willReturn(Optional.of(mock(UserData.class)));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> userService.signUp(MOCK_USER_NAME));

		// then
		verify(userDataRepository, times(1)).findByUserName(MOCK_USER_NAME);
		assertEquals(ErrorCd.DUPLICATE_USER.name(), exception.getErrCode());
	}
}
