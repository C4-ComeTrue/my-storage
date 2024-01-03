package com.c4cometrue.mystorage.api.dto;

import java.util.Arrays;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FileDownloadDto {

	public record Request(
		@NotNull(message = "유저 ID는 null이 될 수 없습니다.") @Positive Long userId,
		@NotNull(message = "파일 ID는 null이 될 수 없습니다.") @Positive Long fileId
	) {

	}

	public record Response(
		Bytes data,
		String contentType
	) {
	}

	public record Bytes(
		byte[] bytes
	) {
		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof Bytes bytes1)) {
				return false;
			}

			return Arrays.equals(bytes, bytes1.bytes);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(bytes);
		}

		@Override
		public String toString() {
			return String.format("bytes = %s", Arrays.toString(bytes));
		}
	}

}
