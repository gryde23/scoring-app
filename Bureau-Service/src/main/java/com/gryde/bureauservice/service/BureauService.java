package com.gryde.bureauservice.service;

import com.gryde.bureauservice.dto.CreditAccountDto;
import com.gryde.bureauservice.dto.PaymentHistoryDto;
import com.gryde.bureauservice.entity.CreditAccount;
import com.gryde.bureauservice.enums.AccountStatus;
import com.gryde.bureauservice.mapper.CreditAccountMapper;
import com.gryde.bureauservice.mapper.PaymentHistoryMapper;
import com.gryde.bureauservice.repository.CreditAccountRepository;
import com.gryde.bureauservice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BureauService {

    private final CreditAccountRepository accountRepository;
    private final CreditAccountMapper creditAccountMapper;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;


    public void setUserUUID(UUID userId, String phone) {
        accountRepository.setUserId(userId, phone);
    }
// доработать
    public int calculateBureauScore(UUID userId) {
        int score = 1000;
        List<CreditAccountDto> accounts = creditAccountMapper.toDtoList(
                accountRepository.findAllByUserId(userId));
        if (accounts.size() < 5) {
            score -= 100;
        }

        for (CreditAccountDto account: accounts) {
            if (account.status().equals(AccountStatus.DEFAULT)) score -= 100;
            if (account.status().equals(AccountStatus.RESTRUCTURED)) score -= 50;

            List<PaymentHistoryDto> payments = paymentHistoryMapper.toDtoList(
                    paymentHistoryRepository.findAllOverduePayments(account.id())
            );

            if (payments.size() > 10) score -= 200;
        }


        return Math.max(0, score);
    }
}
