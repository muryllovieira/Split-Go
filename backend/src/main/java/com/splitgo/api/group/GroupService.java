package com.splitgo.api.group;

import java.util.Currency;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.splitgo.api.group.dto.CreateGroupRequest;
import com.splitgo.api.group.dto.GroupResponse;
import com.splitgo.api.shared.BusinessException;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public GroupResponse create(CreateGroupRequest request) {
        String currency = request.baseCurrency();
        validateCurrency(currency);

        Group group = new Group(request.name().trim(), currency);
        return GroupResponse.from(groupRepository.save(group));
    }

    private void validateCurrency(String code) {
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Moeda não suportada: " + code);
        }
    }
}