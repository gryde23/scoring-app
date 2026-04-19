package com.gryde.antifraudservice;

import com.gryde.contract.AntifraudRequest;
import com.gryde.contract.AntifraudResponse;
import com.gryde.contract.ApplicationResponse;
import com.gryde.contract.BureauSnapshotResponse;
import com.gryde.contract.DecisionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Service
public class AntifraudService {

    private static final int MAX_SCORE = 1000;

    public AntifraudResponse antifraudCheck(AntifraudRequest request) {
        ApplicationResponse newApp = request.newApplication();
        List<ApplicationResponse> previousApps = request.applications() == null ? List.of() : request.applications();
        List<DecisionDTO> previousDecisions = request.decisions() == null ? List.of() : request.decisions();
        BureauSnapshotResponse bureau = request.bureauSnapshot();

        int score = 0;
        Set<String> flags = new LinkedHashSet<>();

        long apps30d = previousApps.size();

        if (apps30d >= 3) {
            score = add(score, 120);
            flags.add("MULTIPLE_APPLICATIONS_30D");
        }

        if (apps30d >= 5) {
            score = add(score, 120);
            flags.add("HIGH_APPLICATION_VELOCITY_30D");
        }

        ApplicationResponse latestPrevious = previousApps.stream()
                .filter(app -> app.createdAt() != null)
                .max(Comparator.comparing(ApplicationResponse::createdAt))
                .orElse(null);

        if (latestPrevious != null) {
            long daysBetween = daysBetween(latestPrevious.createdAt(), newApp.createdAt());

            if (daysBetween <= 3) {
                score = add(score, 100);
                flags.add("REPEAT_APPLICATION_TOO_FAST");
            }

            if (!normalize(newApp.fullName()).equals(normalize(latestPrevious.fullName()))) {
                score = add(score, 100);
                flags.add("FULL_NAME_CHANGED");
            }

            if (!Objects.equals(newApp.employmentType(), latestPrevious.employmentType()) && daysBetween <= 7) {
                score = add(score, 70);
                flags.add("EMPLOYMENT_TYPE_CHANGED_TOO_FAST");
            }

            if (!Objects.equals(newApp.region(), latestPrevious.region()) && daysBetween <= 7) {
                score = add(score, 50);
                flags.add("REGION_CHANGED_TOO_FAST");
            }

            Integer newIncome = newApp.monthlyIncome();
            Integer oldIncome = latestPrevious.monthlyIncome();

            if (newIncome != null && oldIncome != null && oldIncome > 0) {
                double ratio = (double) newIncome / oldIncome;

                if (ratio >= 2.0 && daysBetween <= 30) {
                    score = add(score, 180);
                    flags.add("INCOME_DOUBLED_30D");
                } else if (ratio >= 1.5 && daysBetween <= 7) {
                    score = add(score, 120);
                    flags.add("INCOME_INCREASED_50_PERCENT_7D");
                }
            }
        }

        long highAntifraudCount = previousDecisions.stream()
                .map(DecisionDTO::antifraudScore)
                .filter(Objects::nonNull)
                .filter(value -> value >= 500)
                .count();

        long mediumAntifraudCount = previousDecisions.stream()
                .map(DecisionDTO::antifraudScore)
                .filter(Objects::nonNull)
                .filter(value -> value >= 250)
                .count();

        if (highAntifraudCount >= 1) {
            score = add(score, 150);
            flags.add("PREVIOUS_HIGH_ANTIFRAUD_SCORE");
        }

        if (mediumAntifraudCount >= 2) {
            score = add(score, 100);
            flags.add("MULTIPLE_SUSPICIOUS_PREVIOUS_DECISIONS");
        }

        if (bureau != null) {
            if (bureau.creditHistoryDays() != null && bureau.creditHistoryDays() < 180 && apps30d >= 2) {
                score = add(score, 80);
                flags.add("THIN_CREDIT_HISTORY_AND_REPEAT_APPLICATIONS");
            }

            if (bureau.totalAccounts() != null && bureau.totalAccounts() == 0 && apps30d >= 2) {
                score = add(score, 70);
                flags.add("NO_BUREAU_HISTORY_AND_REPEAT_APPLICATIONS");
            }

            if (bureau.recentOverdueCount() != null && bureau.recentOverdueCount() > 0 && apps30d >= 2) {
                score = add(score, 50);
                flags.add("RECENT_OVERDUE_AND_REPEAT_APPLICATIONS");
            }
        }

        if (bureau != null && newApp.age() != null && bureau.creditHistoryDays() != null) {
            double creditHistoryYears = bureau.creditHistoryDays() / 365.25;
            double creditHistoryStartAge = newApp.age() - creditHistoryYears;

            if (creditHistoryStartAge < 18) {
                score = add(score, 120);
                flags.add("CREDIT_HISTORY_STARTED_BEFORE_18");
            }
        }

        if (newApp.age() != null && newApp.employmentLength() != null) {
            double workStartAge = newApp.age() - newApp.employmentLength();

            if (workStartAge < 16) {
                score = add(score, 120);
                flags.add("EMPLOYMENT_STARTED_BEFORE_16");
            } else if (workStartAge < 18) {
                score = add(score, 70);
                flags.add("EMPLOYMENT_STARTED_BEFORE_18");
            }
        }

        return new AntifraudResponse(score, List.copyOf(flags));
    }

    private int add(int current, int delta) {
        return Math.min(MAX_SCORE, current + delta);
    }

    private long daysBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null || from.isAfter(to)) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(from, to);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}