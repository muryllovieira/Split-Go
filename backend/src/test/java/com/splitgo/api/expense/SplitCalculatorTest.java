package com.splitgo.api.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.splitgo.api.shared.BusinessException;

public class SplitCalculatorTest {

    private final SplitCalculator calculator = new SplitCalculator();

    private final UUID ana = UUID.randomUUID();
    private final UUID bruno = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();
    private final UUID diego = UUID.randomUUID();

    @org.junit.jupiter.api.Test
    void percentageSplitCalculatesShares() {
        UUID ana = UUID.randomUUID(), bruno = UUID.randomUUID(), carla = UUID.randomUUID();

        Map<UUID, BigDecimal> shares = calculator.calculate(
                new BigDecimal("200.00"), SplitType.PERCENTAGE,
                List.of(new Participant(ana, new BigDecimal("50")),
                        new Participant(bruno, new BigDecimal("30")),
                        new Participant(carla, new BigDecimal("20"))));

        assertThat(shares.get(ana)).isEqualByComparingTo("100.00");
        assertThat(shares.get(bruno)).isEqualByComparingTo("60.00");
        assertThat(shares.get(carla)).isEqualByComparingTo("40.00");
    }

    @org.junit.jupiter.api.Test
    void percentageSplitDistributesRoundingRemainder() {
        UUID ana = UUID.randomUUID(), bruno = UUID.randomUUID(), carla = UUID.randomUUID();

        Map<UUID, BigDecimal> shares = calculator.calculate(
                new BigDecimal("10.00"), SplitType.PERCENTAGE,
                List.of(new Participant(ana, new BigDecimal("33.33")),
                        new Participant(bruno, new BigDecimal("33.33")),
                        new Participant(carla, new BigDecimal("33.34"))));

        assertThat(shares.values()).containsExactly(
                new BigDecimal("3.33"), new BigDecimal("3.33"), new BigDecimal("3.34"));
        assertThat(shares.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualByComparingTo("10.00");
    }

    @org.junit.jupiter.api.Test
    void percentageSplitRequiresPercentagesToSumToOneHundred() {
        assertThatThrownBy(() -> calculator.calculate(
                new BigDecimal("100.00"), SplitType.PERCENTAGE,
                List.of(new Participant(UUID.randomUUID(), new BigDecimal("50")),
                        new Participant(UUID.randomUUID(), new BigDecimal("40")))))
                .isInstanceOf(BusinessException.class);
    }

    @org.junit.jupiter.api.Test
    void exactSplitReturnsFixedAmounts() {
        UUID ana = UUID.randomUUID(), bruno = UUID.randomUUID();

        Map<UUID, BigDecimal> shares = calculator.calculate(
                new BigDecimal("100.00"), SplitType.EXACT,
                List.of(new Participant(ana, new BigDecimal("30")),
                        new Participant(bruno, new BigDecimal("70"))));

        assertThat(shares.get(ana)).isEqualByComparingTo("30.00");
        assertThat(shares.get(bruno)).isEqualByComparingTo("70.00");
    }

    @org.junit.jupiter.api.Test
    void exactSplitRequiresFixedAmountsToMatchExpense() {
        assertThatThrownBy(() -> calculator.calculate(
                new BigDecimal("100.00"), SplitType.EXACT,
                List.of(new Participant(UUID.randomUUID(), new BigDecimal("30")),
                        new Participant(UUID.randomUUID(), new BigDecimal("60")))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void equalSplitDividesEvenly() {
        Map<UUID, BigDecimal> shares = calculator.calculate(new BigDecimal("100.00"), SplitType.EQUAL,
                List.of(new Participant(ana, null), new Participant(bruno, null),
                        new Participant(carla, null), new Participant(diego, null)));

        assertThat(shares.values()).allSatisfy(share -> assertThat(share).isEqualByComparingTo("25.00"));
    }

    @Test
    void equalSplitDistributesLeftoverCents() {
        Map<UUID, BigDecimal> shares = calculator.calculate(new BigDecimal("100.00"), SplitType.EQUAL,
                List.of(new Participant(ana, null), new Participant(bruno, null), new Participant(carla, null)));

        assertThat(shares.get(ana)).isEqualByComparingTo("33.34");
        assertThat(shares.get(bruno)).isEqualByComparingTo("33.33");
        assertThat(shares.get(carla)).isEqualByComparingTo("33.33");
    }

    @Test
    void equalSplitOfTinyAmountCanGiveZeroShare() {
        Map<UUID, BigDecimal> shares = calculator.calculate(new BigDecimal("0.02"), SplitType.EQUAL,
                List.of(new Participant(ana, null), new Participant(bruno, null), new Participant(carla, null)));

        assertThat(shares.get(ana)).isEqualByComparingTo("0.01");
        assertThat(shares.get(bruno)).isEqualByComparingTo("0.01");
        assertThat(shares.get(carla)).isEqualByComparingTo("0.00");
    }

    @Test
    void rejectsDuplicatedParticipant() {
        assertThatThrownBy(() -> calculator.calculate(new BigDecimal("10.00"), SplitType.EQUAL,
                List.of(new Participant(ana, null), new Participant(ana, null))))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectsEmptyParticipants() {
        assertThatThrownBy(() -> calculator.calculate(new BigDecimal("10.00"), SplitType.EQUAL, List.of()))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void percentageSplitRejectsZeroPercentage() {
        assertThatThrownBy(() -> calculator.calculate(new BigDecimal("100.00"), SplitType.PERCENTAGE,
                List.of(new Participant(ana, new BigDecimal("100")), new Participant(bruno, BigDecimal.ZERO))))
                .isInstanceOf(BusinessException.class);
    }
}