package com.c4cometrue.mystorage.service;

import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.dto.request.SignUpReq;
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

	@Transactional
	public SignUpRes signUp(SignUpReq signUpReq) {
		if (userDataRepository.findByUserName(signUpReq.userName()).isPresent()) {
			throw ErrorCd.DUPLICATE_USER.serviceException();
		}
		// 유저 root 폴더 생성
		Path userFolderPath = storagePathService.createBasicFolderPath(signUpReq.userName());
		FileUtil.createFolder(userFolderPath);

		// 유저 생성
		UserData user = UserData.builder()
			.userName(signUpReq.userName())
			.build();
		userDataRepository.save(user);

		// 유저 폴더 MetaData 생성
		// parentfolderId에 임의로 0 주입
		FolderMetaData folderMetaData = FolderMetaData.builder()
			.folderName(signUpReq.userName())
			.folderPath(userFolderPath.toString())
			.userName(signUpReq.userName())
			.parentFolderId(0)
			.build();
		folderRepository.save(folderMetaData);

		return new SignUpRes(
			signUpReq.userName(), folderMetaData.getFolderId()
		);
	}
}
