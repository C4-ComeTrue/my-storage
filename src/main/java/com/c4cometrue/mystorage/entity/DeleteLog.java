package com.c4cometrue.mystorage.entity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
public class DeleteLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long logId;

	@NotBlank(message = "file storage name is blank")
	private String fileStorageName;

	@NotNull
	private ZonedDateTime deleteTime;

	public DeleteLog(String fileStorageName) {
		this.fileStorageName = fileStorageName;
		this.deleteTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
	}
}
