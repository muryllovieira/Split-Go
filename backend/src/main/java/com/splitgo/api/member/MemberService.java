package com.splitgo.api.member;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.group.Group;
import com.splitgo.api.group.GroupRepository;
import com.splitgo.api.member.dto.CreateMemberRequest;
import com.splitgo.api.member.dto.MemberResponse;
import com.splitgo.api.shared.ConflictException;
import com.splitgo.api.shared.NotFoundException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

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
}
