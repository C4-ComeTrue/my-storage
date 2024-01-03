package com.c4cometrue.mystorage.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.c4cometrue.mystorage.api.dto.FileDeleteDto;
import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FileController.class)
class FileControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	FileService fileService;

	ObjectMapper mapper = new ObjectMapper();

	@Test
	void 유저아이디가_없다면_파일_업로드가_실패한다() throws Exception {
		var content = "hello";

		mockMvc.perform(
				multipart("/v1/files")
					.file("file", content.getBytes())
					.param("userId", "null")
			)
			.andExpectAll(status().isInternalServerError());
	}

	@Test
	void 파일을_업로드한다() throws Exception {
		// given
		var fileId = 1;
		var userId = 1;
		var uploadFileName = "abc.jpg";
		var fileSize = 1000L;
		var content = "hello";

		var response = new FileUploadDto.Response(fileId, userId, uploadFileName, fileSize);
		given(fileService.fileUpload(any(), anyLong(), anyLong())).willReturn(response);

		// when + then
		mockMvc.perform(
				multipart("/v1/files")
					.file("file", content.getBytes())
					.param("userId", "1")
					.param("folderId", "1")
			)
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.fileId").value(fileId),
				jsonPath("$.userId").value(userId),
				jsonPath("$.uploadFileName").value(uploadFileName),
				jsonPath("$.fileSize").value(fileSize)
			);
	}

	@Test
	void 유저아이디가_없다면_다운로드가_실패한다() throws Exception {
		var request = new FileDownloadDto.Request(1L, null);
		mockMvc.perform(
				get("/v1/files")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(status().is4xxClientError());
	}

	@Test
	void 파일아이디가_없다면_다운로드가_실패한다() throws Exception {
		var request = new FileDownloadDto.Request(null, 1L);
		mockMvc.perform(
				get("/v1/files")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(status().is4xxClientError());
	}

	@Test
	void 파일을_다운로드한다() throws Exception {
		// given
		var bytes = new byte[10];
		var byteDto = new FileDownloadDto.Bytes(bytes);
		var contentType = MediaType.IMAGE_JPEG_VALUE;

		var request = new FileDownloadDto.Request(1L, 1L);
		var response = new FileDownloadDto.Response(byteDto, contentType);
		given(fileService.fileDownLoad(anyLong(), anyLong())).willReturn(response);

		// when + then
		mockMvc.perform(
				get("/v1/files")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(
				status().isOk(),
				content().contentType(contentType),
				content().bytes(bytes)
			);
	}

	@Test
	void 파일을_삭제한다() throws Exception {
		var request = new FileDeleteDto.Request(1L, 1L);

		mockMvc.perform(
				delete("/v1/files")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk());
	}
}
