package com.c4cometrue.mystorage.storage;

public enum Status {
	ACTIVE("활성"),
	DELETED("삭제");

	private final String describeName;

	Status(String describeName) {
		this.describeName = describeName;
	}

	public String getDescribeName() {
		return describeName;
	}
}
