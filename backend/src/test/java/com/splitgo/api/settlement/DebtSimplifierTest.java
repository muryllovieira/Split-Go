package com.splitgo.api.settlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class DebtSimplifierTest {

    private final DebtSimplifier simplifier = new DebtSimplifier();

    private final UUID ana = UUID.randomUUID();
    private final UUID bruno = UUID.randomUUID();
    private final UUID carla = UUID.randomUUID();
    private final UUID diego = UUID.randomUUID();
    private final UUID elisa = UUID.randomUUID();

    @Test
    void noTransfersWhenEveryoneIsSettled() {
        List<Transfer> transfers = simplifier.simplify(balances(ana, "0.00", bruno, "0.00"));

        assertThat(transfers).isEmpty();
    }

    @Test
    void collapsesChainIntoSingleTransfer() {
        List<Transfer> transfers = simplifier.simplify(balances(ana, "-50.00", bruno, "0.00", carla, "50.00"));

        assertThat(transfers).hasSize(1);
        assertTransfer(transfers.get(0), ana, carla, "50.00");
    }

    @Test
    void oneDebtorPaysSeveralCreditors() {
        List<Transfer> transfers = simplifier.simplify(balances(ana, "200.00", bruno, "65.00", carla, "-265.00"));

        assertThat(transfers).hasSize(2);
        assertTransfer(transfers.get(0), carla, ana, "200.00");
        assertTransfer(transfers.get(1), carla, bruno, "65.00");
    }

    @Test
    void settlesExactPairsBeforeGreedyMatching() {
        Map<UUID, BigDecimal> balances = balances(
                ana, "4.00", bruno, "5.00", carla, "-5.00", diego, "4.00", elisa, "-8.00");

        List<Transfer> transfers = simplifier.simplify(balances);

        assertThat(transfers).hasSize(3);
        assertThat(transfers).anySatisfy(t -> assertTransfer(t, carla, bruno, "5.00"));
        assertAllSettled(balances, transfers);
    }

    @Test
    void handlesCents() {
        List<Transfer> transfers = simplifier.simplify(balances(ana, "0.01", bruno, "-0.01"));

        assertThat(transfers).hasSize(1);
        assertTransfer(transfers.get(0), bruno, ana, "0.01");
    }

    @Test
    void neverNeedsMoreThanMembersMinusOneTransfers() {
        Map<UUID, BigDecimal> balances = balances(
                ana, "-10.00", bruno, "-20.00", carla, "-30.00", diego, "25.00", elisa, "35.00");

        List<Transfer> transfers = simplifier.simplify(balances);

        assertThat(transfers.size()).isLessThanOrEqualTo(balances.size() - 1);
        assertAllSettled(balances, transfers);
    }

    @Test
    void zeroBalanceWithDifferentScaleIsIgnored() {
        List<Transfer> transfers = simplifier.simplify(balances(ana, "0.00", bruno, "0"));

        assertThat(transfers).isEmpty();
    }

    @Test
    void rejectsBalancesThatDoNotSumToZero() {
        assertThatThrownBy(() -> simplifier.simplify(balances(ana, "10.00", bruno, "-5.00")))
                .isInstanceOf(IllegalStateException.class);
    }

    private static Map<UUID, BigDecimal> balances(Object... pairs) {
        Map<UUID, BigDecimal> balances = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            balances.put((UUID) pairs[i], new BigDecimal((String) pairs[i + 1]));
        }
        return balances;
    }

    private static void assertTransfer(Transfer transfer, UUID from, UUID to, String amount) {
        assertThat(transfer.fromMemberId()).isEqualTo(from);
        assertThat(transfer.toMemberId()).isEqualTo(to);
        assertThat(transfer.amount()).isEqualByComparingTo(amount);
    }

    private static void assertAllSettled(Map<UUID, BigDecimal> balances, List<Transfer> transfers) {
        Map<UUID, BigDecimal> remaining = new HashMap<>(balances);
        for (Transfer t : transfers) {
            assertThat(t.amount().signum()).isPositive();
            remaining.merge(t.fromMemberId(), t.amount(), BigDecimal::add);
            remaining.merge(t.toMemberId(), t.amount().negate(), BigDecimal::add);
        }
        assertThat(remaining.values()).allSatisfy(v -> assertThat(v).isEqualByComparingTo("0"));
    }
}