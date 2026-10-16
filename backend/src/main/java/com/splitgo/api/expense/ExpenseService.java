package com.splitgo.api.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.expense.dto.CreateExpenseRequest;
import com.splitgo.api.expense.dto.ExpenseResponse;
import com.splitgo.api.expense.dto.ParticipantRequest;
import com.splitgo.api.group.Group;
import com.splitgo.api.group.GroupRepository;
import com.splitgo.api.member.Member;
import com.splitgo.api.member.MemberRepository;
import com.splitgo.api.shared.BusinessException;
import com.splitgo.api.shared.NotFoundException;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final SplitCalculator splitCalculator;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            GroupRepository groupRepository,
            MemberRepository memberRepository,
            SplitCalculator splitCalculator) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.splitCalculator = splitCalculator;
    }

    @Transactional
    public ExpenseResponse create(UUID groupId, CreateExpenseRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
        Map<UUID, Member> groupMembers = memberRepository.findByGroup_IdOrderByCreatedAtAscNameAsc(groupId).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        Member payer = findMember(groupMembers, request.payerId());
        List<Participant> participants = request.participants().stream()
                .map(participant -> toParticipant(groupMembers, participant))
                .toList();

        BigDecimal amount = request.amount().setScale(2);
        Map<UUID, BigDecimal> shares = splitCalculator.calculate(amount, request.splitType(), participants);

        Expense expense = new Expense(
                group,
                payer,
                request.description().trim(),
                amount,
                group.getBaseCurrency(),
                BigDecimal.ONE,
                amount,
                request.splitType(),
                request.expenseDate() == null ? LocalDate.now() : request.expenseDate());

        for (ParticipantRequest participant : request.participants()) {
            BigDecimal percentage = request.splitType() == SplitType.PERCENTAGE
                    ? participant.value()
                    : null;
            expense.addSplit(
                    findMember(groupMembers, participant.memberId()),
                    shares.get(participant.memberId()),
                    percentage);
        }

        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public ExpenseResponse get(UUID groupId, UUID expenseId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Grupo não encontrado: " + groupId);
        }
        Expense expense = expenseRepository.findByIdAndGroup_Id(expenseId, groupId)
                .orElseThrow(() -> new NotFoundException("Despesa não encontrada: " + expenseId));
        return ExpenseResponse.from(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Grupo não encontrado: " + groupId);
        }
        return expenseRepository.findByGroup_IdOrderByExpenseDateDescCreatedAtDesc(groupId).stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    private Participant toParticipant(Map<UUID, Member> groupMembers, ParticipantRequest participant) {
        findMember(groupMembers, participant.memberId());
        return new Participant(participant.memberId(), participant.value());
    }

    private Member findMember(Map<UUID, Member> groupMembers, UUID memberId) {
        Member member = groupMembers.get(memberId);
        if (member == null) {
            throw new BusinessException("Integrante não pertence a este grupo: " + memberId);
        }
        return member;
    }
}