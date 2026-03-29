package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.dto.ApplicationCreateRequest;
import com.gryde.applicationorchestrator.dto.ApplicationDTO;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.User;
import com.gryde.applicationorchestrator.enums.ApplicationStatus;

public final class ApplicationMapper {

    private ApplicationMapper() {
    }

    public static ApplicationDTO toDTO(Application application) {
        if (application == null) {
            return null;
        }

        return new ApplicationDTO(
                application.getId(),
                application.getFullName(),
                application.getAge(),
                application.getGender(),
                application.getMaritalStatus(),
                application.getDependents(),
                application.getEducation(),
                application.getRegion(),
                application.getEmploymentType(),
                application.getEmploymentLength(),
                application.getMonthlyIncome(),
                application.getAdditionalIncome(),
                application.isHasProperty(),
                application.isHasCar(),
                application.getExistingCards(),
                application.getExistingLoans(),
                application.getTotalMonthlyDebt(),
                application.isHasSalaryProject(),
                application.isHasDeposit(),
                application.getCardTypeRequested(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUser() != null ? application.getUser().getId() : null,
                application.getDecision() != null ? application.getDecision().getId() : null
        );
    }

    public static Application toEntity(ApplicationCreateRequest dto, User user) {
        if (dto == null) {
            return null;
        }

        Application application = new Application();
        application.setFullName(dto.fullName());
        application.setAge(dto.age());
        application.setGender(dto.gender());
        application.setMaritalStatus(dto.maritalStatus());
        application.setDependents(dto.dependents());
        application.setEducation(dto.education());
        application.setRegion(dto.region());
        application.setEmploymentType(dto.employmentType());
        application.setEmploymentLength(dto.employmentLength());
        application.setMonthlyIncome(dto.monthlyIncome());
        application.setAdditionalIncome(dto.additionalIncome());
        application.setHasProperty(dto.hasProperty());
        application.setHasCar(dto.hasCar());
        application.setExistingCards(dto.existingCards());
        application.setExistingLoans(dto.existingLoans());
        application.setTotalMonthlyDebt(dto.totalMonthlyDebt());
        application.setHasSalaryProject(dto.hasSalaryProject());
        application.setHasDeposit(dto.hasDeposit());
        application.setCardTypeRequested(dto.cardTypeRequested());

        application.setUser(user);
        application.setStatus(ApplicationStatus.IN_PROGRESS);

        return application;
    }

}