package com.c4cometrue.mystorage.member;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(	indexes = {@Index(name = "idx_basePath", columnList = "basePath")})
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	private String basePath;

	public static String makeBasePath() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String formattedDate = dateFormat.format(new Date());
		return formattedDate + UUID.randomUUID();
	}

	@Builder
	public Member(String basePath) {
		this.basePath = basePath;
	}
}
