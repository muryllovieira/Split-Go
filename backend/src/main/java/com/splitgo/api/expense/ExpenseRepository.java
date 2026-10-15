package com.splitgo.api.expense;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    boolean existsByGroup_IdAndId(UUID groupId, UUID expenseId);

}
