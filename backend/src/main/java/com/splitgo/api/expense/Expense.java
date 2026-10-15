package com.splitgo.api.expense;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.splitgo.api.group.Group;
import com.splitgo.api.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payer_id", nullable = false)
    private Member payer;

    @Column(nullable = false, length = 140)
    private String description;

    @Column(name = "original_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal originalAmount;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "original_currency", nullable = false, length = 3)
    private String originalCurrency;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 8)
    private BigDecimal exchangeRate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "split_type", nullable = false, length = 20)
    private SplitType splitType;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseSplit> splits = new ArrayList<>();

    protected Expense() {
    }

    public Expense(Group group, Member payer, String description, BigDecimal originalAmount,
            String originalCurrency, BigDecimal exchangeRate, BigDecimal amount,
            SplitType splitType, LocalDate expenseDate) {
        this.group = group;
        this.payer = payer;
        this.description = description;
        this.originalAmount = originalAmount;
        this.originalCurrency = originalCurrency;
        this.exchangeRate = exchangeRate;
        this.amount = amount;
        this.splitType = splitType;
        this.expenseDate = expenseDate;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public void addSplit(Member member, BigDecimal amount, BigDecimal percentage) {
        splits.add(new ExpenseSplit(this, member, amount, percentage));
    }

    public UUID getId() {
        return id;
    }

    public Group getGroup() {
        return group;
    }

    public Member getPayer() {
        return payer;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<ExpenseSplit> getSplits() {
        return List.copyOf(splits);
    }
}