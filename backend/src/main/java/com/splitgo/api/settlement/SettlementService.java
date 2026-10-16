package com.splitgo.api.settlement;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.group.Group;
import com.splitgo.api.group.GroupRepository;
import com.splitgo.api.settlement.dto.SettlementResponse;
import com.splitgo.api.settlement.dto.SettlementResponse.MemberBalance;
import com.splitgo.api.settlement.dto.SettlementResponse.MemberSummary;
import com.splitgo.api.settlement.dto.SettlementResponse.TransferResponse;
import com.splitgo.api.shared.NotFoundException;

@Service
public class SettlementService {

    private final GroupRepository groupRepository;
    private final BalanceRepository balanceRepository;
    private final DebtSimplifier debtSimplifier;

    public SettlementService(GroupRepository groupRepository, BalanceRepository balanceRepository,
            DebtSimplifier debtSimplifier) {
        this.groupRepository = groupRepository;
        this.balanceRepository = balanceRepository;
        this.debtSimplifier = debtSimplifier;
    }

    @Transactional(readOnly = true)
    public SettlementResponse getSettlement(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

        List<MemberBalance> members = balanceRepository.findBalancesByGroupId(groupId).stream()
                .map(SettlementService::toMemberBalance)
                .toList();

        Map<UUID, BigDecimal> balances = new LinkedHashMap<>();
        Map<UUID, MemberSummary> summaries = new LinkedHashMap<>();
        for (MemberBalance member : members) {
            balances.put(member.id(), member.balance());
            summaries.put(member.id(), new MemberSummary(member.id(), member.name()));
        }

        List<TransferResponse> transfers = debtSimplifier.simplify(balances).stream()
                .map(t -> new TransferResponse(
                        summaries.get(t.fromMemberId()), summaries.get(t.toMemberId()), t.amount()))
                .toList();

        return new SettlementResponse(group.getBaseCurrency(), transfers.isEmpty(), members, transfers);
    }

    private static MemberBalance toMemberBalance(MemberBalanceView view) {
        BigDecimal balance = view.getPaid()
                .subtract(view.getConsumed())
                .add(view.getSent())
                .subtract(view.getReceived());
        return new MemberBalance(view.getMemberId(), view.getName(), view.getPaid(), view.getConsumed(),
                view.getSent(), view.getReceived(), balance);
    }
}