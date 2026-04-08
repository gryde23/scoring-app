package com.gryde.contract;

import java.util.List;

public record AntifraudRequest(
        List<ApplicationDTO> applications,
        List<ApplicationDecisionDTO> decisions
) {
}
