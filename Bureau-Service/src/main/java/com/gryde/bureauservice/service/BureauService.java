package com.gryde.bureauservice.service;

import com.gryde.bureauservice.dto.CreditAccountDto;
import com.gryde.bureauservice.dto.PaymentHistoryDto;
import com.gryde.bureauservice.entity.CreditAccount;
import com.gryde.bureauservice.enums.AccountStatus;
import com.gryde.bureauservice.mapper.CreditAccountMapper;
import com.gryde.bureauservice.mapper.PaymentHistoryMapper;
import com.gryde.bureauservice.repository.SelfBanDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BureauService {

    private final CreditAccountMapper creditAccountMapper;
    private final PaymentHistoryMapper paymentHistoryMapper;
    private final SelfBanDao selfBanDao;

    public boolean hasSelfBan(UUID userId) {
        return selfBanDao.isBanned(userId);
    }

    // доработать
    public int calculateBureauScore(UUID userId) {
        int score = 1000;

        return score;
    }
}
