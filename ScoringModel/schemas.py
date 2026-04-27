from decimal import Decimal

from pydantic import BaseModel, Field


class ScoringRequest(BaseModel):
    age: int = Field(..., ge=18, le=100)
    maritalStatus: str
    education: str
    region: str
    employmentType: str
    employmentLength: int = Field(..., ge=0)
    monthlyIncome: int = Field(..., ge=0)
    additionalIncome: int = Field(..., ge=0)
    dependents: int = Field(..., ge=0)

    hasProperty: bool
    hasCar: bool
    hasSalaryProject: bool
    hasDeposit: bool

    cardTypeRequested: str

    totalAccounts: int = Field(..., ge=0)
    activeAccounts: int = Field(..., ge=0)
    closedAccounts: int = Field(..., ge=0)
    defaultAccounts: int = Field(..., ge=0)
    restructuredAccounts: int = Field(..., ge=0)

    creditHistoryDays: int = Field(..., ge=0)
    totalCreditLimit: int = Field(..., ge=0)
    totalActiveDebt: int = Field(..., ge=0)

    utilizationRatio: float = Field(..., ge=0.0, le=1.0)

    totalPayments: int = Field(..., ge=0)

    dpd30: int = Field(..., ge=0)
    dpd60: int = Field(..., ge=0)
    dpd90: int = Field(..., ge=0)
    dpd90Plus: int = Field(..., ge=0)

    maxDaysOverdue: int = Field(..., ge=0)

    paymentRatio: float = Field(..., ge=0.0)
    partialPaymentsCount: int = Field(..., ge=0)
    recentOverdueCount: int = Field(..., ge=0)

    monthlyDebtPayment: int = Field(..., ge=0)

    debtToIncome: float = Field(..., ge=0.0)


class MlScoringResponse(BaseModel):
    mlDefaultProbability: Decimal
    recommendedLimit: int