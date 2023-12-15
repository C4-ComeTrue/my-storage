package com.c4cometrue.mystorage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetaData extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_meta_data_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private FileMetaData parent;

	// @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE) // 읽기 전용 메서드
	// private List<FileMetaData> childList;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String fileName;

	private String uploadName;

	private long size = 0;

	private String type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FileType fileType;

	@Builder(builderMethodName = "rootBuilder")
	public FileMetaData(
		Long id, Long userId, String fileName, String uploadName, FileType fileType
	) {
		this.id = id;
		this.userId = userId;
		this.fileName = fileName;
		this.uploadName = uploadName;
		this.fileType = fileType;
	}

	@Builder(builderMethodName = "fileBuilder")
	public FileMetaData(

		Long userId, String fileName, String uploadName, long size, String type, FileType fileType,
		FileMetaData parent
	) {
		this.userId = userId;
		this.fileName = fileName;
		this.uploadName = uploadName;
		this.size = size;
		this.type = type;
		this.fileType = fileType;
		this.parent = parent;
	}

	// public void addParentFolder(FileMetaData fileMetaData) {
	// 	this.parent = fileMetaData;
	// 	if (parent != null) {
	// 		parent.childList.add(this);
	// 	}
	// }

	public void rename(String newName) {
		this.fileName = newName;
	}
}
