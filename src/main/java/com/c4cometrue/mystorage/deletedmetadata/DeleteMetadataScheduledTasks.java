package com.c4cometrue.mystorage.deletedmetadata;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.common.MetadataType;
import com.c4cometrue.mystorage.util.FileUtil;
import com.c4cometrue.mystorage.util.PagingUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteMetadataScheduledTasks {
	private final DeletedMetadataRepository deletedMetadataRepository;

	@Transactional
	@Scheduled(cron = "0 0 3 * * ?")
	public void hardDeleteMetadata() {
		Long cursorId = 0L;
		Pageable pageable = PagingUtil.createPageable();

		List<DeletedMetadata> deletedMetadataList = deletedMetadataRepository.findAllWithCursor(cursorId, pageable);

		while (!deletedMetadataList.isEmpty()) {
			// hardDelete 수행
			deletedMetadataList.stream()
				.filter(deletedMetadata -> deletedMetadata.getType().equals(MetadataType.FILE))
				.forEach(deletedMetadata -> FileUtil.delete(Paths.get(deletedMetadata.getFilePath()))
				);

			List<Long> metadataIds = deletedMetadataList.stream()
				.map(DeletedMetadata::getId)
				.toList();
			// 삭제된 파일들은 데이터 베이스에서도 지워준다
			deletedMetadataRepository.deleteAllById(metadataIds);

			cursorId = deletedMetadataList.get(deletedMetadataList.size() - 1).getId();
			deletedMetadataList = deletedMetadataRepository.findAllWithCursor(cursorId, pageable);
		}
	}
}
