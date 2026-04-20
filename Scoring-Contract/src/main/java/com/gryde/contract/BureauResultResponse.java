package com.gryde.contract;

public record BureauResultResponse(
        boolean selfBanned,
        BureauSnapshotResponse bureauData
) {}