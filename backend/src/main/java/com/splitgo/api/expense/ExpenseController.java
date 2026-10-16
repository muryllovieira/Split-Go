package com.splitgo.api.expense;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.splitgo.api.expense.dto.CreateExpenseRequest;
import com.splitgo.api.expense.dto.ExpenseResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.create(groupId, request);
    }

    @GetMapping
    public List<ExpenseResponse> list(
            @PathVariable UUID groupId) {
        return expenseService.list(groupId);
    }

    @GetMapping("/{expenseId}")
    public ExpenseResponse get(
            @PathVariable UUID groupId,
            @PathVariable UUID expenseId) {
        return expenseService.get(groupId, expenseId);
    }
}
