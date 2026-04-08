package com.gryde.scoringservice;

import com.gryde.contract.ScoringRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InternalScoringEngine {

    public InternalScoringResponse calculateInternalScore(ScoringRequest request) {
        int score = 0;
        List<String> scoringReasons = new ArrayList<>();
        boolean stopFactor = checkStopFactor(request, scoringReasons);

        if (stopFactor) {
            return new InternalScoringResponse(score, scoringReasons);
        }

        if (request.age() >= 25 && request.age() <= 45) {
            score += 100;
        } else if ((request.age() >= 18 && request.age() < 25) ||
                   (request.age() > 45 && request.age() <= 60)) {
            score += 70;
        } else if (request.age() > 60 && request.age() <= 70) {
            scoringReasons.add("Возраст больше 60");
            score += 50;
        }

        switch (request.maritalStatus()) {
            case "в браке" -> score += 20;
            case "разведен/а" -> score -= 20;
        }

        if (request.dependents() > 5) {
            scoringReasons.add("Много иждивенцев");
            score -= 50;
        } else if (request.dependents() > 3) {
            score -= 25;
        } else if (request.dependents() > 1) {
            score -= 10;
        }

        int totalIncome = request.monthlyIncome() + request.additionalIncome();
        float DTI = (float) request.totalMonthlyDebt() / totalIncome;

        if (DTI > 0.5) {
            scoringReasons.add("Высокая долговая нагрузка");
            score += 50;
        } else if (DTI > 0.35) {
            scoringReasons.add("Средняя долговая нагрузка");
            score += 130;
        } else if (DTI > 0.20) {
            score += 220;
        } else {
            score += 300;
        }

        switch (request.employmentType()) {
            case "наемный работник" -> score += 120;
            case "бизнес" -> score += 100;
            case "самозанятый" -> score += 80;
            case "пенсионер" -> {
                scoringReasons.add("Пенсионер");
                score += 40;
            }
        }

        if (request.employmentLength() >= 10) {
            score += 130;
        } else if (request.employmentLength() >= 5) {
            score += 80;
        } else if (request.employmentLength() >= 3) {
            scoringReasons.add("Стаж работы меньше 5 лет");
            score += 50;
        } else if (request.employmentLength() >= 1) {
            scoringReasons.add("Малый стаж");
            score += 30;
        }

        switch (request.education()) {
            case "ученая степень" -> score += 100;
            case "высшее" -> score += 60;
            case "среднее специальное" -> score += 40;
            case "среднее" -> {
                scoringReasons.add("Отсутствует профильное образование");
                score += 20;
            }
        }

        switch (request.region()) {
            case "Москва", "Санкт-Петербург" -> score += 50;
            case "региональный центр" -> score += 25;
            case "другой город" -> score += 10;
        }

        if (request.hasProperty()) {
            score += 60;
        } else {
            scoringReasons.add("Отсутствие имущества");
        }

        if (request.hasCar()) {
            score += 40;
        }

        if (request.hasSalaryProject()) {
            score += 70;
        } else {
            scoringReasons.add("Отсутствие зарплатного проекта");
        }

        if (request.hasDeposit()) {
            score += 50;
        } else {
            scoringReasons.add("Отсутствие депозита");
        }

        if (request.existingCards() > 0 && request.existingCards() < 4) {
            score += 30;
        }

        if (request.existingLoans() > 2) {
            scoringReasons.add("Много действующих кредитов");
            score -= 50;
        } else if (request.existingLoans() >= 1) {
            score += 10;
        } else {
            score += 30;
        }

        switch (request.cardTypeRequested()) {
            case "платиновая" -> score = (int) (score * 0.8);
            case "золотая" -> score = (int) (score * 0.9);
        }

        score = Math.min(1000, Math.max(0, score));
        return new InternalScoringResponse(score, scoringReasons);
    }

    private boolean checkStopFactor(ScoringRequest request, List<String> scoringReasons) {
        if (request.age() < 18 || request.age() > 70) {
            scoringReasons.add("Неподходящий возраст");
            return true;
        }

        if (request.monthlyIncome() < 20000) {
            scoringReasons.add("Слишком низкий доход");
            return true;
        }

        int totalIncome = request.monthlyIncome() + request.additionalIncome();
        float DTI = (float) request.totalMonthlyDebt() / totalIncome;

        if (request.employmentType().equals("безработный") && totalIncome < 40000) {
            scoringReasons.add("Ненадежный тип занятости");
            return true;
        }

        if (DTI > 0.65) {
            scoringReasons.add("Высокая долговая нагрузка");
            return true;
        }

        return false;
    }
}
