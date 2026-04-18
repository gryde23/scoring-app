package com.gryde.scoringservice;

import com.gryde.contract.ScoringRequest;
import com.gryde.contract.enums.MaritalStatus;
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
            return new InternalScoringResponse(-1, scoringReasons);
        }

        if (request.age() >= 25 && request.age() <= 45) {
            score += 100;
        } else if ((request.age() >= 18 && request.age() < 25) ||
                   (request.age() > 45 && request.age() <= 60)) {
            score += 70;
        } else if (request.age() > 60 && request.age() <= 70) {
            scoringReasons.add("Возраст больше 60");
        }

        switch (request.maritalStatus()) {
            case "married" -> score += 50;
            case "single" -> score += 20;
            case "divorced" -> score -= 20;
        }

        score -= request.dependents() * 20;

        int totalIncome = request.monthlyIncome() + request.additionalIncome();
        float DTI = (float) request.monthlyDebtPayment().doubleValue() / totalIncome;

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
            case "employee" -> score += 120;
            case "business" -> score += 100;
            case "self_employed" -> score += 40;
            case "pensioner" -> {
                scoringReasons.add("Пенсионер");
                score -= 10;
            }
            case "unemployed" -> {
                scoringReasons.add("Безработный");
                score -= 100;
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
            case "phd" -> score += 100;
            case "higher" -> score += 60;
            case "secondary_special" -> score += 40;
            case "secondary" -> {
                scoringReasons.add("Отсутствует профильное образование");
                score -= 50;
            }
        }

        switch (request.region()) {
            case "moscow", "saint_petersburg" -> score += 50;
            case "regional_center" -> score += 25;
            case "other" -> score += 10;
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
            scoringReasons.add("Отсутствует зарплатный проект");
        }

        if (request.hasDeposit()) {
            score += 50;
        } else {
            scoringReasons.add("Отсутствует депозит");
        }

        switch (request.cardTypeRequested()) {
            case "platinum" -> score = (int) (score * 0.8);
            case "gold" -> score = (int) (score * 0.9);
        }

        score = Math.min(1000, Math.max(0, score));
        return new InternalScoringResponse(score, scoringReasons);
    }

    private boolean checkStopFactor(ScoringRequest request, List<String> scoringReasons) {

        if (request.monthlyIncome() < 20000) {
            scoringReasons.add("Слишком низкий доход");
            return true;
        }

        int totalIncome = request.monthlyIncome() + request.additionalIncome();
        float DTI = (float) request.monthlyDebtPayment().doubleValue() / totalIncome;

        if (request.employmentType().equals("unemployed") && totalIncome < 40000) {
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
