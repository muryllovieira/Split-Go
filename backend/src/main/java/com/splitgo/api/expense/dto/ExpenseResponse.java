package com.splitgo.api.expense.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.splitgo.api.expense.Expense;
import com.splitgo.api.expense.ExpenseSplit;
import com.splitgo.api.expense.SplitType;

public record ExpenseResponse(
        UUID id,
        String description,
        MemberSummary payer,
        BigDecimal amount,
        BigDecimal originalAmount,
        String originalCurrency,
        BigDecimal exchangeRate,
        SplitType splitType,
        LocalDate expenseDate,
        Instant createdAt,
        List<SplitResponse> splits) {

    public record MemberSummary(UUID id, String name) {
    }

    public record SplitResponse(MemberSummary member, BigDecimal amount, BigDecimal percentage) {

        static SplitResponse from(ExpenseSplit split) {
            return new SplitResponse(
                    new MemberSummary(split.getMember().getId(), split.getMember().getName()),
                    split.getAmount(),
                    split.getPercentage());
        }
    }

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                new MemberSummary(expense.getPayer().getId(), expense.getPayer().getName()),
                expense.getAmount(),
                expense.getOriginalAmount(),
                expense.getOriginalCurrency(),
                expense.getExchangeRate(),
                expense.getSplitType(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getSplits().stream().map(SplitResponse::from).toList());
    }
}