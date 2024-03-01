package com.c4cometrue.mystorage.rootfolder.dto;

import java.math.BigDecimal;

public record RootInfo(
    Long rootId,
    String originalFolderName,
    BigDecimal availableSpaceInGb,
    BigDecimal usedSpaceInGb,
    BigDecimal remainingSpaceInGb
) {
    public static RootInfo of(long rootId, String folderName, BigDecimal availableSpaceInGb, BigDecimal usedSpaceInGb,
                              BigDecimal remainingSpaceInGb) {
        return new RootInfo(rootId, folderName, availableSpaceInGb, usedSpaceInGb, remainingSpaceInGb);
    }
}
