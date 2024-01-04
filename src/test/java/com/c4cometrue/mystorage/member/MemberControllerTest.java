package com.c4cometrue.mystorage.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("맴버 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {
	@InjectMocks
	private MemberController memberController;

	@Mock
	private MemberService memberService;

	@Test
	@DisplayName("맴버 생성 테스트")
	void registerMemberTest() {
		doNothing().when(memberService).register();

		assertDoesNotThrow(() -> memberController.registerMember());
		verify(memberService, times(1)).register();
	}
}
