package com.splitgo.api.expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    Optional<Expense> findByIdAndGroup_Id(UUID id, UUID groupId);

    List<Expense> findByGroup_IdOrderByExpenseDateDescCreatedAtDesc(UUID groupId);

}
