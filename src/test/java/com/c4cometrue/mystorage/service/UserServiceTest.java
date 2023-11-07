package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.file.Path;
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

import com.c4cometrue.mystorage.dto.request.SignUpReq;
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
		var signUpReq = new SignUpReq(mockUserName);
		var mockFolderPath = Path.of(mockRootPath).resolve(mockUserName);
		given(storagePathService.createBasicFolderPath(any())).willReturn(mockFolderPath);

		// when
		var signUpRes = userService.signUp(signUpReq);

		// then
		assertThat(signUpRes)
			.matches(res -> StringUtils.equals(res.userName(), mockUserName))
			.matches(res -> res.folderId() == 0L);
	}

	@Test
	@DisplayName("중복 이름으로 회원가입 실패")
	void signUpFail() {
		// given
		given(userDataRepository.findByUserName(mockUserName)).willReturn(Optional.of(mock(UserData.class)));
		var signUpReq = new SignUpReq(mockUserName);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> userService.signUp(signUpReq));

		// then
		verify(userDataRepository, times(1)).findByUserName(mockUserName);
		assertEquals(ErrorCd.DUPLICATE_USER.name(), exception.getErrCode());
	}
}
