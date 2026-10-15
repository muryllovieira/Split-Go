package com.splitgo.api.expense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.splitgo.api.shared.BusinessException;

public class SplitCalculator {

    private static final BigDecimal CENT = new BigDecimal("0.01");

    public Map<UUID, BigDecimal> calculate(BigDecimal amount, SplitType type, List<Participant> participants) {
        validateParticipants(participants);

        return switch (type) {
            case EQUAL -> equalSplit(amount, participants);
            case PERCENTAGE -> percentageSplit(amount, participants);
            case EXACT -> exactSplit(amount, participants);
        };
    }

    private Map<UUID, BigDecimal> percentageSplit(BigDecimal amount, List<Participant> participants) {
        BigDecimal totalPercentage = BigDecimal.ZERO;
        List<BigDecimal> exactShares = new ArrayList<>(participants.size());
        Map<UUID, BigDecimal> shares = new LinkedHashMap<>();

        for (Participant participant : participants) {
            BigDecimal percentage = participant.value();
            if (percentage == null || percentage.signum() <= 0) {
                throw new BusinessException("As porcentagens precisam ser informadas e maiores que zero");
            }
            totalPercentage = totalPercentage.add(percentage);
            exactShares.add(amount.multiply(percentage).divide(BigDecimal.valueOf(100)));
        }

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new BusinessException("As porcentagens dos participantes precisam somar 100");
        }

        List<BigDecimal> remainders = new ArrayList<>(participants.size());
        BigDecimal totalAllocated = BigDecimal.ZERO;
        for (BigDecimal exactShare : exactShares) {
            BigDecimal roundedDown = exactShare.setScale(2, RoundingMode.DOWN);
            remainders.add(exactShare.subtract(roundedDown));
            totalAllocated = totalAllocated.add(roundedDown);
        }

        int extraCents = amount.subtract(totalAllocated).movePointRight(2).intValueExact();
        List<Integer> remainderOrder = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            remainderOrder.add(i);
        }
        remainderOrder.sort(Comparator.comparing(remainders::get).reversed());

        BigDecimal[] allocatedShares = new BigDecimal[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            allocatedShares[i] = exactShares.get(i).setScale(2, RoundingMode.DOWN);
        }
        for (int i = 0; i < extraCents; i++) {
            int participantIndex = remainderOrder.get(i);
            allocatedShares[participantIndex] = allocatedShares[participantIndex].add(CENT);
        }

        for (int i = 0; i < participants.size(); i++) {
            shares.put(participants.get(i).memberId(), allocatedShares[i]);
        }
        return shares;
    }

    private Map<UUID, BigDecimal> exactSplit(BigDecimal amount, List<Participant> participants) {
        BigDecimal total = BigDecimal.ZERO;
        Map<UUID, BigDecimal> shares = new LinkedHashMap<>();

        for (Participant participant : participants) {
            BigDecimal value = participant.value();
            if (value == null || value.signum() < 0) {
                throw new BusinessException("Os valores fixos precisam ser informados e não podem ser negativos");
            }
            try {
                value = value.setScale(2, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException exception) {
                throw new BusinessException("Os valores fixos precisam ter no máximo duas casas decimais");
            }
            total = total.add(value);
            shares.put(participant.memberId(), value);
        }

        if (total.compareTo(amount) != 0) {
            throw new BusinessException(
                    "A soma dos valores fixos (" + total + ") precisa ser igual ao valor da despesa (" + amount + ")");
        }
        return shares;
    }

    private Map<UUID, BigDecimal> equalSplit(BigDecimal amount, List<Participant> participants) {
        int count = participants.size();
        BigDecimal base = amount.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
        BigDecimal leftover = amount.subtract(base.multiply(BigDecimal.valueOf(count)));
        int extraCents = leftover.movePointRight(2).intValueExact();

        Map<UUID, BigDecimal> shares = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            BigDecimal share = i < extraCents ? base.add(CENT) : base;
            shares.put(participants.get(i).memberId(), share);
        }
        return shares;
    }

    private void validateParticipants(List<Participant> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new BusinessException("A despesa precisa de pelo menos 1 participante");
        }
        Set<UUID> seen = new HashSet<>();
        for (Participant participant : participants) {
            if (!seen.add(participant.memberId())) {
                throw new BusinessException("Um integrante foi informado mais de uma vez na despesa");
            }
        }
    }
}