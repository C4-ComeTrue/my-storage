package com.c4cometrue.mystorage.service;

import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.dto.response.SignUpRes;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.entity.UserData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.repository.UserDataRepository;
import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final StoragePathService storagePathService;
	private final UserDataRepository userDataRepository;
	private final FolderRepository folderRepository;

	/**
	 * 회원가입 로직
	 * @param userName 가입하는 사용자 이름
	 * @return SignUpRes(유저 이름, 유저 폴더 pk)
	 */
	@Transactional
	public SignUpRes signUp(String userName) {
		// 유저명 중복 검사
		checkDuplicate(userName);

		// 유저 데이터 / 폴더 데이터 DB 저장
		userDataRepository.save(new UserData(userName));

		// parentfolderId에 임의로 0 주입
		FolderMetaData folderMetaData = FolderMetaData.builder()
			.folderName(userName)
			.userName(userName)
			.parentFolderId(0)
			.build();
		folderMetaData = folderRepository.save(folderMetaData);
		// 유저 이름의 폴더를 생성하고, 해당 유저의 root폴더로 설정함
		Path userFolderPath = storagePathService.createPathByUser(userName);

		// 유저 이름의 물리적 폴더 생성
		FileUtil.createFolder(userFolderPath);

		// 사용자의 폴더 기본키와 함께 응답
		return new SignUpRes(userName, folderMetaData.getFolderId());
	}

	private void checkDuplicate(String userName) {
		if (userDataRepository.findByUserName(userName).isPresent()) {
			throw ErrorCd.DUPLICATE_USER.serviceException();
		}
	}
}
