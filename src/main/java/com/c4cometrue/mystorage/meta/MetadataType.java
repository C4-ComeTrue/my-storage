package com.c4cometrue.mystorage.meta;

import lombok.Getter;

@Getter
public enum MetadataType {
	FILE("File"),
	FOLDER("Folder");

	private String type;

	private MetadataType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
