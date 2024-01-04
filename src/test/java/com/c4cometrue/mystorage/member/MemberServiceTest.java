package com.c4cometrue.mystorage.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("MemberService 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Test
	@DisplayName("맴버 등록 테스트")
	void registerTest() {
		String basePath = "mockBasePath";
		when(Member.makeBasePath()).thenReturn(basePath);
		when(memberRepository.existsByBasePath(basePath)).thenReturn(false);

		memberService.register();

		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@Test
	@DisplayName("중복 맴버 등록 예외 테스트")
	void registerDuplicateTest() {
		String basePath = "mockBasePath";
		when(Member.makeBasePath()).thenReturn(basePath);
		when(memberRepository.existsByBasePath(basePath)).thenReturn(true);

		assertThrows(ServiceException.class, () -> memberService.register());
	}
}
