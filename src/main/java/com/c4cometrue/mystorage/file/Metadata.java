package com.c4cometrue.mystorage.file;

import java.util.UUID;

import com.c4cometrue.mystorage.MetadataType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Metadata", indexes = @Index(name = "index_uploaderId", columnList = "uploaderId"))
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

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MetadataType metadataType;

	public static String storedName() {
		return UUID.randomUUID().toString();
	}

	private Metadata(String originalFileName, String storedFileName, String filePath, Long uploaderId) {
		this.originalFileName = originalFileName;
		this.storedFileName = storedFileName;
		this.filePath = filePath;
		this.uploaderId = uploaderId;
		this.metadataType = MetadataType.FILE;
	}

	public static Metadata of(String originalFileName, String storedFileName, String filePath, Long uploaderId) {
		return new Metadata(originalFileName, storedFileName, filePath, uploaderId);
	}
}
