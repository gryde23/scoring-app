package com.gryde.applicationorchestrator.mapper;

import com.gryde.applicationorchestrator.dto.ApplicationDTO;
import com.gryde.applicationorchestrator.entity.Application;
import com.gryde.applicationorchestrator.entity.ApplicationDecision;
import com.gryde.applicationorchestrator.entity.User;

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

    public static Application toEntity(ApplicationDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        Application application = new Application();
        application.setId(dto.id());
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
        application.setStatus(dto.status());
        application.setCreatedAt(dto.createdAt());
        application.setUser(user);

        return application;
    }

    public static Application toEntity(ApplicationDTO dto, User user, ApplicationDecision decision) {
        Application application = toEntity(dto, user);
        if (application != null) {
            application.setDecision(decision);
        }
        return application;
    }

    public static void updateEntity(Application application, ApplicationDTO dto, User user) {
        if (application == null || dto == null) {
            return;
        }

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
        application.setStatus(dto.status());

        if (dto.createdAt() != null) {
            application.setCreatedAt(dto.createdAt());
        }

        if (user != null) {
            application.setUser(user);
        }
    }
}