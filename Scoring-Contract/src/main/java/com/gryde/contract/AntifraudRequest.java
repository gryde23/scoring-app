package com.gryde.contract;

import java.util.List;

public record AntifraudRequest(
        ApplicationResponse newApplication,
        List<ApplicationResponse> applications,
        List<DecisionDTO> decisions,
        BureauSnapshotResponse bureauSnapshot
) {
}
