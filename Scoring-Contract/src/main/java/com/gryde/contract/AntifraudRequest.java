package com.gryde.contract;

import java.util.List;

public record AntifraudRequest(
        List<ApplicationResponse> applications,
        List<DecisionDTO> decisions
) {
}
