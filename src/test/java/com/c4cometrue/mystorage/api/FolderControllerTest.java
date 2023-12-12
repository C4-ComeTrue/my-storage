package com.c4cometrue.mystorage.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.c4cometrue.mystorage.api.dto.FolderGetDto;
import com.c4cometrue.mystorage.api.dto.FolderRenameDto;
import com.c4cometrue.mystorage.api.dto.FolderUploadDto;
import com.c4cometrue.mystorage.domain.FileType;
import com.c4cometrue.mystorage.service.FolderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FolderController.class)
class FolderControllerTest {

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
				post("/v1/folders/root")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.id").value(folderId)
			);
	}

	@Test
	void 폴더를_생성한다() throws Exception {
		// given
		var userId = 1L;
		var parentId = 1L;
		var folderId = 2L;
		var name = "name";

		var response = new FolderUploadDto.Res(folderId);
		var request = new FolderUploadDto.Req(parentId, userId, name);
		given(folderService.createFolder(anyLong(), anyLong(), anyString())).willReturn(response);

		// when + then
		mockMvc.perform(
				post("/v1/folders")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(
				status().isCreated(),
				jsonPath("$.id").value(folderId)
			);
	}

	@Test
	void 폴더_이름을_변경한다() throws Exception {
		// given
		var userId = 1L;
		var folderId = 2L;
		var name = "name1";

		var request = new FolderRenameDto.Req(userId, folderId, name);

		// when + then
		mockMvc.perform(
				patch("/v1/folders")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(
				status().isOk()
			);
	}

	@Test
	void 폴더_내용을_반환한다() throws Exception {
		// given
		var userId = 1L;
		var folderId = 2L;
		var folderName = "folder";
		var fileId = 3L;
		var fileName = "file";
		var createdAt = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());

		var response = new FolderGetDto.Res(folderId, folderName,
			List.of(
				new FolderGetDto.FileDto(
					fileId, FileType.FILE, fileName, createdAt, 1000
				)
			)
		);

		var request = new FolderGetDto.Req(userId, folderId);
		given(folderService.getFolderContents(anyLong(), anyLong())).willReturn(response);

		// when + then
		mockMvc.perform(
				get("/v1/folders")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
			)
			.andExpectAll(
				status().isOk(),
				jsonPath("$.folderId").value(folderId),
				jsonPath("$.folderName").value(folderName),
				jsonPath("$.subFileList[0].fileId").value(fileId),
				jsonPath("$.subFileList[0].fileName").value(fileName),
				jsonPath("$.subFileList[0].fileType").value(FileType.FILE.name())
			);

	}

}
