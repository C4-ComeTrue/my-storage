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

@DisplayName("맴버 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Test
	@DisplayName("맴버 생성 테스트")
	void registerMemberTest() {
		Member member = Member.builder().basePath("testPath").build();
		doReturn(member).when(memberRepository).save(any(Member.class));
		doReturn(false).when(memberRepository).existsByBasePath(anyString());

		assertDoesNotThrow(() -> memberService.register());
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	@Test
	@DisplayName("맴버 생성 실패 테스트 - 중복된 basePath")
	void registerMemberFailTest() {
		doReturn(true).when(memberRepository).existsByBasePath(anyString());

		assertThrows(ServiceException.class, () -> memberService.register());
		verify(memberRepository, never()).save(any(Member.class));
	}
}
