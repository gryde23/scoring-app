package com.gryde.contract;

import java.util.List;

public record AntifraudResponse(
        Integer antifraudScore,
        List<String> antifraudFlags
) {
}
