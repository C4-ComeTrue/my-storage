package com.c4cometrue.mystorage.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private FileMetaData parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE) // 읽기 전용 메서드
	private List<FileMetaData> childList;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String fileName;

	private String uploadName;

	private long size;

	private String type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FileType fileType;

	private String folderPath;

	@Builder(builderMethodName = "fileBuilder")
	public FileMetaData(Long userId, String fileName, String uploadName, long size, String type, String folderPath,
		FileType fileType) {
		this.userId = userId;
		this.fileName = fileName;
		this.uploadName = uploadName;
		this.size = size;
		this.type = type;
		this.folderPath = folderPath;
		this.fileType = fileType;
	}

	@Builder(builderMethodName = "folderBuilder")
	public FileMetaData(Long userId, String fileName, String uploadName, String folderPath, FileType fileType) {
		this.userId = userId;
		this.fileName = fileName;
		this.uploadName = uploadName;
		this.folderPath = folderPath;
		this.fileType = fileType;
	}

	public void addParentFolder(FileMetaData fileMetaData) {
		this.parent = fileMetaData;
		if (parent != null) {
			parent.childList.add(this);
		}
	}

	public void rename(String newName) {
		this.fileName = newName;
	}

	public void updateFolderPath(String newFolderPath) {
		this.folderPath = newFolderPath;
	}
}
