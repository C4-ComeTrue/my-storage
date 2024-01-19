package com.c4cometrue.mystorage.deletedmetadata;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteMetadataService {
	private final DeletedMetadataRepository deletedMetadataRepository;

	public void persist(List<DeletedMetadata> deletedMetadata) {
		deletedMetadataRepository.saveAll(deletedMetadata);
	}
}
