import joblib
import numpy as np
import pandas as pd


class ModelPredictor:
    CAT_COLS = [
        "marital_status",
        "education",
        "region",
        "employment_type",
        "card_type_requested",
    ]

    FEATURE_COLS = [
        "age",
        "marital_status",
        "education",
        "region",
        "employment_type",
        "employment_length",
        "monthly_income",
        "additional_income",
        "dependents",
        "has_property",
        "has_car",
        "has_salary_project",
        "has_deposit",
        "card_type_requested",
        "total_accounts",
        "active_accounts",
        "closed_accounts",
        "default_accounts",
        "restructured_accounts",
        "credit_history_days",
        "total_credit_limit",
        "total_active_debt",
        "utilization_ratio",
        "total_payments",
        "dpd30",
        "dpd60",
        "dpd60_90",
        "dpd90_plus",
        "max_days_overdue",
        "payment_ratio",
        "partial_payments_count",
        "recent_overdue_count",
        "debt_to_income",
        "monthly_debt_payment",
    ]

    CARD_LIMITS = {
        "standard": 250_000,
        "gold":     500_000,
        "platinum": 1_000_000,
    }

    def __init__(self):
        self.model_default = joblib.load("models/model_default_risk.pkl")
        self.model_limit   = joblib.load("models/model_credit_limit.pkl")
        self.encoder       = joblib.load("models/ordinal_encoder.pkl")

    def _to_model_dataframe(self, request: dict) -> pd.DataFrame:
        df = pd.DataFrame([{
            "age":                    request["age"],
            "marital_status":         request["maritalStatus"],
            "education":              request["education"],
            "region":                 request["region"],
            "employment_type":        request["employmentType"],
            "employment_length":      request["employmentLength"],
            "monthly_income":         request["monthlyIncome"],
            "additional_income":      request["additionalIncome"],
            "dependents":             request["dependents"],
            "has_property":           int(request["hasProperty"]),
            "has_car":                int(request["hasCar"]),
            "has_salary_project":     int(request["hasSalaryProject"]),
            "has_deposit":            int(request["hasDeposit"]),
            "card_type_requested":    request["cardTypeRequested"],
            "total_accounts":         request["totalAccounts"],
            "active_accounts":        request["activeAccounts"],
            "closed_accounts":        request["closedAccounts"],
            "default_accounts":       request["defaultAccounts"],
            "restructured_accounts":  request["restructuredAccounts"],
            "credit_history_days":    request["creditHistoryDays"],
            "total_credit_limit":     request["totalCreditLimit"],
            "total_active_debt":      request["totalActiveDebt"],
            "utilization_ratio":      request["utilizationRatio"],
            "total_payments":         request["totalPayments"],
            "dpd30":                  request["dpd30"],
            "dpd60":                  request["dpd60"],
            "dpd60_90":               request["dpd90"],
            "dpd90_plus":             request["dpd90Plus"],
            "max_days_overdue":       request["maxDaysOverdue"],
            "payment_ratio":          request["paymentRatio"],
            "partial_payments_count": request["partialPaymentsCount"],
            "recent_overdue_count":   request["recentOverdueCount"],
            "debt_to_income":         request["debtToIncome"],
            "monthly_debt_payment":   request["monthlyDebtPayment"],
        }])

        df[self.CAT_COLS] = self.encoder.transform(df[self.CAT_COLS]).astype(int)
        return df[self.FEATURE_COLS]

    def _apply_card_max_limit(self, limit: int, card_type: str) -> int:
        max_limit = self.CARD_LIMITS.get(card_type.strip().lower(), 1_000_000)
        return max(0, min(limit, max_limit))

    def predict(self, request: dict) -> dict:
        df = self._to_model_dataframe(request)

        default_probability = float(np.clip(self.model_default.predict(df)[0], 0.0, 1.0))

        recommended_limit   = int(np.round(self.model_limit.predict(df)[0] / 5_000) * 5_000)
        recommended_limit   = self._apply_card_max_limit(recommended_limit, request["cardTypeRequested"])

        return {
            "mlDefaultProbability": round(default_probability, 6),
            "recommendedLimit":     recommended_limit,
        }