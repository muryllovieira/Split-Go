package com.splitgo.api.member;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.group.Group;
import com.splitgo.api.group.GroupRepository;
import com.splitgo.api.member.dto.CreateMemberRequest;
import com.splitgo.api.member.dto.MemberResponse;
import com.splitgo.api.shared.BusinessException;
import com.splitgo.api.shared.ConflictException;
import com.splitgo.api.shared.NotFoundException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private static final int MIN_MEMBERS = 2;

    public MemberService(MemberRepository memberRepository, GroupRepository groupRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public MemberResponse create(UUID groupId, CreateMemberRequest request) {
        String name = request.name().trim();
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
        if (memberRepository.existsByGroup_IdAndNameIgnoreCase(group.getId(), name)) {
            throw new ConflictException("Já existe um integrante chamado " + name + " neste grupo");
        }
        Member member = new Member(group, name);
        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> list(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Grupo não encontrado: " + groupId);
        }
        return memberRepository.findByGroup_IdOrderByCreatedAtAscNameAsc(groupId).stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public void remove(UUID groupId, UUID memberId) {
        Member member = memberRepository.findByIdAndGroup_Id(memberId, groupId)
                .orElseThrow(() -> new NotFoundException("Integrante não encontrado: " + memberId));

        if (memberRepository.hasEntries(memberId)) {
            throw new BusinessException(
                    "Não é possível remover " + member.getName() + ": possui lançamentos no grupo");
        }
        if (memberRepository.countByGroup_Id(groupId) <= MIN_MEMBERS) {
            throw new BusinessException("O grupo precisa de pelo menos " + MIN_MEMBERS + " integrantes");
        }
        memberRepository.delete(member);
    }
}
