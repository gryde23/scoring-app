package com.gryde.contract;

import java.util.UUID;

public record AddAccountToBureauRequest(
        UUID userId,
        Integer approvedLimit
) {
}
