package com.c4cometrue.mystorage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FileMetaData extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_meta_data_id")
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String fileName;

	@Column(nullable = false)
	private String uploadName;

	@Column(nullable = false)
	private long size;

	@Column(nullable = false)
	private String type;

	@Builder
	public FileMetaData(Long userId, String fileName, String uploadName, long size, String type) {
		this.userId = userId;
		this.fileName = fileName;
		this.uploadName = uploadName;
		this.size = size;
		this.type = type;
	}

}
