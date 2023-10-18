package com.c4cometrue.mystorage.file;

import java.util.UUID;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Metadata {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String originalFileName;
	@Column(nullable = false)
	private String storedFileName;
	@Column(nullable = false)
	private String filePath;
	@Column(nullable = false)
	private Long uploaderId;

	public void validate(Long userId) {
		if (!this.uploaderId.equals(userId)){
			throw new ServiceException(ErrorCode.UNAUTHORIZED_FILE_ACCESS);
		}
	}

	public static String storedName(String fileName){
		return fileName + UUID.randomUUID();
	}

	private Metadata(String originalFileName, String storedFileName, String filePath, Long uploaderId) {
		this.originalFileName = originalFileName;
		this.storedFileName = storedFileName;
		this.filePath = filePath;
		this.uploaderId = uploaderId;
	}

	public static Metadata of(String originalFileName, String storedFileName, String filePath, Long uploaderId){
		return new Metadata(originalFileName, storedFileName, filePath, uploaderId);
	}
}