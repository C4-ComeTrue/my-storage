package com.c4cometrue.mystorage.deletemetadata;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.TestConstants;
import com.c4cometrue.mystorage.common.MetadataType;
import com.c4cometrue.mystorage.deletedmetadata.DeleteMetadataService;
import com.c4cometrue.mystorage.deletedmetadata.DeletedMetadata;
import com.c4cometrue.mystorage.deletedmetadata.DeletedMetadataRepository;

@DisplayName("삭제메타데이터 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class DeleteMetadataServiceTest {
	@InjectMocks
	private DeleteMetadataService deleteMetadataService;

	@Mock
	private DeletedMetadataRepository deletedMetadataRepository;

	@Test
	@DisplayName("삭제메타데이터 저장 테스트")
	void persistTest() {
		List<DeletedMetadata> deletedMetadataList = List.of(DELETE_METADATA);

		deleteMetadataService.persist(deletedMetadataList);

		verify(deletedMetadataRepository, times(1)).saveAll(deletedMetadataList);
	}
}
