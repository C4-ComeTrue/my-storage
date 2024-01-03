package com.c4cometrue.mystorage.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.c4cometrue.mystorage.api.dto.FolderUploadDto;
import com.c4cometrue.mystorage.service.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RegisterController.class)
class RegisterControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	FolderService folderService;

	ObjectMapper mapper = new ObjectMapper();

	@Test
	void 루트_폴더를_생성한다() throws Exception {
		// given
		var userId = 1L;
		var folderId = 1L;
		var name = "name";

		var response = new FolderUploadDto.Res(folderId);
		var request = new FolderUploadDto.Req(null, userId, name);
		given(folderService.createRootFolder(anyLong(), anyString())).willReturn(response);

		// when + then
		mockMvc.perform(
				post("/v1/register/root-folder")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.id").value(folderId)
			);
	}

}
