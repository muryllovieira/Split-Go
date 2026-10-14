package com.splitgo.api.group;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.group.dto.CreateGroupRequest;
import com.splitgo.api.group.dto.GroupResponse;
import com.splitgo.api.member.Member;
import com.splitgo.api.member.MemberRepository;
import com.splitgo.api.shared.BusinessException;
import com.splitgo.api.shared.ConflictException;
import com.splitgo.api.shared.NotFoundException;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public GroupService(GroupRepository groupRepository, MemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public GroupResponse create(CreateGroupRequest request) {
        String currency = request.baseCurrency();
        validateCurrency(currency);
        List<String> memberNames = normalizeMemberNames(request.members());

        Group group = groupRepository.save(new Group(request.name().trim(), currency));

        List<Member> members = memberNames.stream()
                .map(name -> new Member(group, name))
                .toList();
        memberRepository.saveAll(members);

        return GroupResponse.from(group);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(UUID groupId) {
        return groupRepository.findById(groupId)
                .map(GroupResponse::from)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado: " + groupId));
    }

    private void validateCurrency(String code) {
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Moeda não suportada: " + code);
        }
    }

    private List<String> normalizeMemberNames(List<String> names) {
        Set<String> seen = new HashSet<>();
        List<String> result = new ArrayList<>();

        for (String rawName : names) {
            String name = rawName.trim();
            if (!seen.add(name.toLowerCase(Locale.ROOT))) {
                throw new ConflictException("O integrante " + name + " foi informado mais de uma vez");
            }
            result.add(name);
        }
        return result;
    }
}
