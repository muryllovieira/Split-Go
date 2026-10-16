package com.splitgo.api.settlement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class DebtSimplifier {

    private record Position(UUID memberId, BigDecimal amount) {
    }

    private static final Comparator<Position> LARGEST_FIRST = Comparator.comparing(Position::amount).reversed()
            .thenComparing(Position::memberId);

    public List<Transfer> simplify(Map<UUID, BigDecimal> balances) {
        BigDecimal total = balances.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.signum() != 0) {
            throw new IllegalStateException("A soma dos saldos deve ser zero. Encontrado total: " + total);
        }

        List<Position> debtors = new ArrayList<>();
        List<Position> creditors = new ArrayList<>();

        for (Map.Entry<UUID, BigDecimal> entry : balances.entrySet()) {
            BigDecimal balance = entry.getValue();
            int sign = balance.signum();

            if (sign < 0) {
                debtors.add(new Position(entry.getKey(), balance.negate()));
            } else if (sign > 0) {
                creditors.add(new Position(entry.getKey(), balance));
            }
        }

        debtors.sort(LARGEST_FIRST);
        creditors.sort(LARGEST_FIRST);

        List<Transfer> transfers = new ArrayList<>();

        Iterator<Position> debtorIterator = debtors.iterator();
        while (debtorIterator.hasNext()) {
            Position debtor = debtorIterator.next();

            Iterator<Position> creditorIterator = creditors.iterator();

            while (creditorIterator.hasNext()) {
                Position creditor = creditorIterator.next();

                if (debtor.amount().compareTo(creditor.amount()) == 0) {
                    transfers.add(new Transfer(debtor.memberId(), creditor.memberId(), debtor.amount()));
                    debtorIterator.remove();
                    creditorIterator.remove();

                    break;
                }
            }
        }

        PriorityQueue<Position> debtorQueue = new PriorityQueue<>(LARGEST_FIRST);
        PriorityQueue<Position> creditorQueue = new PriorityQueue<>(LARGEST_FIRST);
        debtorQueue.addAll(debtors);
        creditorQueue.addAll(creditors);

        while (!debtorQueue.isEmpty() && !creditorQueue.isEmpty()) {
            Position debtor = debtorQueue.poll();
            Position creditor = creditorQueue.poll();
            BigDecimal payment = debtor.amount().min(creditor.amount());

            transfers.add(new Transfer(debtor.memberId(), creditor.memberId(), payment));

            BigDecimal debtorLeft = debtor.amount().subtract(payment);
            BigDecimal creditorLeft = creditor.amount().subtract(payment);
            if (debtorLeft.signum() > 0) {
                debtorQueue.add(new Position(debtor.memberId(), debtorLeft));
            }
            if (creditorLeft.signum() > 0) {
                creditorQueue.add(new Position(creditor.memberId(), creditorLeft));
            }
        }

        return transfers;
    }
}