package com.c4cometrue.mystorage.rootfolder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.rootfolder.dto.RootInfo;
import com.c4cometrue.mystorage.util.DataSizeConverter;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@DisplayName("루트폴더 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class RootFolderServiceTest {
    @InjectMocks
    private RootFolderService rootFolderService;

    @Mock
    private RootFolderRepository rootFolderRepository;

    @Value("${file.storage-path}")
    private String STORAGE_PATH;

    @Test
    @DisplayName("루트폴더 생성 테스트")
    void updateUsedSpaceForUploadTest() {
        given(rootFolderRepository.findByIdAndOwnerId(ROOT_ID, USER_ID)).willReturn(
            Optional.of(ROOT_FOLDER_METADATA));
        rootFolderService.updateUsedSpaceForUpload(USER_ID, ROOT_ID, BigDecimal.TEN);
        verify(rootFolderRepository, times(1)).findByIdAndOwnerId(ROOT_ID, USER_ID);
    }

    @Test
    @DisplayName("업로드 공간 부족 시 예외 발생 테스트")
    void updateUsedSpaceForUploadFailTest() {
        BigDecimal uploadFileSize = ROOT_FOLDER_METADATA.getAvailableSpace().add(BigDecimal.ONE);
        given(rootFolderRepository.findByIdAndOwnerId(ROOT_ID, USER_ID)).willReturn(
            Optional.of(ROOT_FOLDER_METADATA));

        assertThrows(ServiceException.class, () -> {
            rootFolderService.updateUsedSpaceForUpload(USER_ID, ROOT_ID, uploadFileSize);
        }, ErrorCode.EXCEEDED_CAPACITY.getMessage());
        verify(rootFolderRepository, times(1)).findByIdAndOwnerId(ROOT_ID, USER_ID);
    }

    @Test
    @DisplayName("파일 삭제시 루트 폴더 사용 공간 변화 테스트")
    void updateUsedSpaceForDeletionTest() {
        given(rootFolderRepository.findByIdAndOwnerId(ROOT_ID, USER_ID)).willReturn(
            Optional.of(ROOT_FOLDER_METADATA));
        rootFolderService.updateUsedSpaceForDeletion(USER_ID, ROOT_ID, BigDecimal.TEN);
        verify(rootFolderRepository, times(1)).findByIdAndOwnerId(ROOT_ID, USER_ID);
    }

    @Test
    @DisplayName("삭제 공간 초과 예외 발생 테스트")
    void updateUsedSpaceForDeletionFailTest() {
        BigDecimal uploadFileSize = ROOT_FOLDER_METADATA.getAvailableSpace().add(BigDecimal.ONE);
        given(rootFolderRepository.findByIdAndOwnerId(ROOT_ID, USER_ID)).willReturn(
            Optional.of(ROOT_FOLDER_METADATA));

        assertThrows(ServiceException.class, () -> {
            rootFolderService.updateUsedSpaceForDeletion(USER_ID, ROOT_ID, uploadFileSize);
        }, ErrorCode.EXCEEDED_CAPACITY.getMessage());
        verify(rootFolderRepository, times(1)).findByIdAndOwnerId(ROOT_ID, USER_ID);
    }

    @Test
    @DisplayName("유효하지 않는 폴더에 접근 테스트")
    void checkValidateByFailTest() {
        assertThrows(ServiceException.class, () -> {
            rootFolderService.checkValidateBy(NOT_USERS_ROOT_ID, USER_ID);
        }, ErrorCode.CANNOT_FOUND_FOLDER.getMessage());
        verify(rootFolderRepository, times(1)).existsByIdAndOwnerId(NOT_USERS_ROOT_ID, USER_ID);
    }

    @Test
    @DisplayName("루트폴더 생성 테스트")
    void createByTest() {
        when(rootFolderRepository.existsByStoredFolderName(any())).thenReturn(false);
        when(rootFolderRepository.existsByOwnerIdAndOriginalFolderName(USER_ID, USER_FOLDER_NAME)).thenReturn(false);

        rootFolderService.createBy(USER_ID, USER_FOLDER_NAME);

        verify(rootFolderRepository, times(1)).save(any(RootFolderMetadata.class));
    }

    @Test
    @DisplayName("루트폴더 생성 실패 테스트 - 서버 폴더 이름 중복")
    void createByTest_Fail_DuplicateServerFolderName() {
        when(rootFolderRepository.existsByStoredFolderName(any())).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            rootFolderService.createBy(USER_ID, USER_FOLDER_NAME);
        });

        assertEquals(ErrorCode.DUPLICATE_SERVER_FOLDER_NAME.name(), exception.getErrCode());

        verify(rootFolderRepository, times(0)).save(any(RootFolderMetadata.class));
    }

    @Test
    @DisplayName("루트폴더 생성 실패 테스트 - 사용자 폴더 이름 중복")
    void createByTest_Fail_DuplicateUserFolderName() {
        when(rootFolderRepository.existsByOwnerIdAndOriginalFolderName(USER_ID, USER_FOLDER_NAME)).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            rootFolderService.createBy(USER_ID, USER_FOLDER_NAME);
        });

        assertEquals(ErrorCode.DUPLICATE_FOLDER_NAME.name(), exception.getErrCode());

        verify(rootFolderRepository, times(0)).save(any(RootFolderMetadata.class));
    }

    @Test
    @DisplayName("루트 폴더 정보 조회 테스트")
    void getRootInfoTest() {
        BigDecimal availableSpaceInBytes = DataSizeConverter.gigabytesToBytes(2);
        BigDecimal availableSpaceInGb = DataSizeConverter.bytesToGigaBytes(availableSpaceInBytes);
        BigDecimal usedSpace = BigDecimal.ZERO;
        BigDecimal remainingSpace = availableSpaceInGb.subtract(usedSpace);

        RootFolderMetadata metadata = RootFolderMetadata.builder()
            .ownerId(USER_ID)
            .originalFolderName(USER_FOLDER_NAME)
            .build();

        when(rootFolderRepository.findByIdAndOwnerId(ROOT_ID, USER_ID)).thenReturn(Optional.of(metadata));


        RootInfo rootInfo = rootFolderService.getRootInfo(ROOT_ID, USER_ID);

        assertEquals(USER_ID, rootInfo.rootId());
        assertEquals(USER_FOLDER_NAME, rootInfo.originalFolderName());
        assertEquals(0, availableSpaceInGb.compareTo(rootInfo.availableSpaceInGb()));
        assertEquals(0, usedSpace.compareTo(rootInfo.usedSpaceInGb()));
        assertEquals(0, remainingSpace.compareTo(rootInfo.remainingSpaceInGb()));
    }
}
