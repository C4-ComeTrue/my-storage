package com.c4cometrue.mystorage.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("MemberController 테스트")
@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberService memberService;

	@Test
	@DisplayName("맴버 생성 API 호출 테스트")
	void registerMemberApiTest() throws Exception {
		String basePath = "mockBasePath";
		doNothing().when(memberService).register();
		when(Member.makeBasePath()).thenReturn(basePath);

		mockMvc.perform(post("/members"))
			.andExpect(status().isCreated());

		verify(memberService, times(1)).register();
	}
}
