package com.gryde.antifrodservice;

import com.gryde.contract.AntifraudRequest;
import com.gryde.contract.AntifraudResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AntifraudService {

    public AntifraudResponse antifraudCheck(AntifraudRequest request) {
        return new AntifraudResponse(1000, List.of("None"));
    }
}
