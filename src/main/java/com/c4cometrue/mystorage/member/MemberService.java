package com.c4cometrue.mystorage.member;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	public void register() {
		String memberBasePath = Member.makeBasePath();
		validateBy(memberBasePath);
		Member member = Member.builder()
			.basePath(memberBasePath)
			.build();
		memberRepository.save(member);
	}

	private void validateBy(String basePath) {
		if (memberRepository.existsByBasePath(basePath)) {
			throw ErrorCode.DUPLICATE_BASE_PATH.serviceException();
		}
	}
}
