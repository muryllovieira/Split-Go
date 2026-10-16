package com.splitgo.api.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.group.Group;
import com.splitgo.api.group.GroupRepository;
import com.splitgo.api.member.Member;
import com.splitgo.api.member.MemberRepository;
import com.splitgo.api.payment.dto.CreatePaymentRequest;
import com.splitgo.api.payment.dto.PaymentResponse;
import com.splitgo.api.settlement.BalanceRepository;
import com.splitgo.api.settlement.MemberBalanceView;
import com.splitgo.api.shared.BusinessException;
import com.splitgo.api.shared.NotFoundException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final BalanceRepository balanceRepository;

    public PaymentService(PaymentRepository paymentRepository, GroupRepository groupRepository,
            MemberRepository memberRepository, BalanceRepository balanceRepository) {
        this.paymentRepository = paymentRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public PaymentResponse create(UUID groupId, CreatePaymentRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));

        Map<UUID, Member> groupMembers = memberRepository.findByGroup_IdOrderByCreatedAtAscNameAsc(groupId).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
        Member from = findMember(groupMembers, request.fromMemberId());
        Member to = findMember(groupMembers, request.toMemberId());

        if (from.getId().equals(to.getId())) {
            throw new BusinessException("Um integrante não pode pagar a si mesmo");
        }

        BigDecimal amount = request.amount().setScale(2);
        Map<UUID, BigDecimal> balances = currentBalances(groupId);
        BigDecimal debt = balances.get(from.getId()).negate();
        BigDecimal credit = balances.get(to.getId());

        if (debt.signum() <= 0) {
            throw new BusinessException(from.getName() + " não tem dívidas no grupo");
        }
        if (credit.signum() <= 0) {
            throw new BusinessException(to.getName() + " não tem valores a receber no grupo");
        }
        if (amount.compareTo(debt) > 0) {
            throw new BusinessException(from.getName() + " deve apenas " + debt);
        }
        if (amount.compareTo(credit) > 0) {
            throw new BusinessException(to.getName() + " tem apenas " + credit + " a receber");
        }

        Payment payment = paymentRepository.save(new Payment(group, from, to, amount));
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> list(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Grupo não encontrado: " + groupId);
        }
        return paymentRepository.findByGroup_IdOrderByCreatedAtDesc(groupId).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    private Map<UUID, BigDecimal> currentBalances(UUID groupId) {
        return balanceRepository.findBalancesByGroupId(groupId).stream()
                .collect(Collectors.toMap(MemberBalanceView::getMemberId, MemberBalanceView::getBalance));
    }

    private Member findMember(Map<UUID, Member> groupMembers, UUID memberId) {
        Member member = groupMembers.get(memberId);
        if (member == null) {
            throw new BusinessException("Integrante não pertence a este grupo: " + memberId);
        }
        return member;
    }
}